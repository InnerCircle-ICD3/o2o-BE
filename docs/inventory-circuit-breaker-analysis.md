# 재고 관리 및 서킷브레이커 아키텍처 분석

## 1. 전체 플로우 다이어그램

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              재고 관리 전체 아키텍처                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│   Order       │     │   Product     │     │   Inventory   │
│   Service     │────▶│   Service     │────▶│   Service     │
└───────────────┘     └───────────────┘     └───────────────┘
        │                     │                       │
        │ OrderCreatedEvent   │                       │
        ▼                     ▼                       ▼
┌───────────────────────────────────────────────────────────────┐
│                    ProductEventListener                        │
│  @RedisCircuitBreaker(                                         │
│    name="product-stock-decrease",                              │
│    failureThreshold=3,                                         │
│    timeoutMinutes=1L                                           │
│  )                                                             │
└───────────────────────────────────────────────────────────────┘
        │                                   │
        │ Redis 정상                         │ Redis 장애 (3회 연속 실패)
        ▼                                   ▼
┌─────────────────────┐         ┌─────────────────────────────┐
│   정상 흐름          │         │   Fallback 흐름 (DB 직접)   │
├─────────────────────┤         ├─────────────────────────────┤
│ 1. Redis 재고 감소   │         │ 1. DB에서 재고 조회/생성    │
│    (decreaseStock)   │         │ 2. DB 재고 직접 업데이트    │
│ 2. 상태 변경 알림    │         │ 3. InventorySyncEvent 발행  │
│ 3. changedProductIds │         │ 4. 상태 변경 알림           │
│    Set에 추가        │         └─────────────────────────────┘
└─────────────────────┘                     │
        │                                   │
        ▼                                   ▼
┌───────────────────────────────────────────────────────────────┐
│                    데이터 동기화 계층                          │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  1. 일일 스케줄러 (매일 12:00)                                 │
│     ┌─────────────────────────────────────────────────────┐   │
│     │  InventorySyncScheduler.syncInventoryFromRedisToRdb()│   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────────┐    │   │
│     │  │ Changed IDs 처리 (Redis → DB)               │    │   │
│     │  │ - changedProductIds Set에서 ID 조회         │    │   │
│     │  │ - Redis 현재 재고로 DB 업데이트            │    │   │
│     │  │ - Lua 스크립트로 원자적 Pop & Delete        │    │   │
│     │  └─────────────────────────────────────────────┘    │   │
│     │                                                      │   │
│     │  ┌─────────────────────────────────────────────┐    │   │
│     │  │ Pending IDs 처리 (DB → Redis)               │    │   │
│     │  │ - pendingSyncProductIds Set에서 ID 조회     │    │   │
│     │  │ - DB 최신 재고로 Redis 업데이트            │    │   │
│     │  │ - Redis 정상 시에만 실행                    │    │   │
│     │  └─────────────────────────────────────────────┘    │   │
│     └─────────────────────────────────────────────────────┘   │
│                                                               │
│  2. 서킷브레이커 복구 시 캐시 워밍                             │
│     ┌─────────────────────────────────────────────────────┐   │
│     │  InventoryCacheWarmer.onCircuitBreakerRecovered()   │   │
│     │                                                      │   │
│     │  - OPEN/HALF_OPEN → CLOSED 상태 변경 감지           │   │
│     │  - changedProductIds Set에서 ID 조회                │   │
│     │  - 500개 단위 청크 처리                             │   │
│     │  - DB 조회 → Redis 캐시 저장                        │   │
│     └─────────────────────────────────────────────────────┘   │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

## 2. 핵심 컴포넌트별 상세 분석

### 2.1 재고 감소 흐름 (정상 vs 장애)

**정상 흐름 (Redis 정상)**:
```
OrderCreatedEvent
    ↓
ProductEventListener.handleOrderEvent()
    ↓
inventoryCachePersistence.decreaseStock()
    ↓ (Lua 스크립트로 원자적 감소)
Redis Hash: inventory:hash:{productId}.stock -= quantity
    ↓
InventoryChangeNotifier.notifyInventoryStatusChange()
    ↓
InventoryChangedEvent 발행 → Store 상태 변경 (재고 소진 시 CLOSED)
```

**장애 흐름 (Redis 장애 - 서킷브레이커 OPEN)**:
```
OrderCreatedEvent
    ↓
ProductEventListener.handleOrderEvent()
    ↓ (Redis 연결 실패 3회 누적)
서킷브레이커: CLOSED → OPEN
    ↓
handleOrderEventFallback()
    ↓
processStockDecreaseFromDB()
    ├── DB에서 Inventory 조회 (없으면 생성)
    ├── inventory.decreaseStock()
    ├── inventoryPersistence.updateStock()
    ├── inventorySyncPublisher.publishEvent(InventorySyncEvent)
    └── InventoryChangeNotifier.notifyInventoryStatusChange()
```

### 2.2 Circuit Breaker 상태 전이

```
         ┌─────────────┐
         │   CLOSED    │ ◀─── 초기 상태 (정상)
         │ (정상 동작)  │
         └──────┬──────┘
                │
                │ 실패 임계치 도달 (3회)
                ▼
         ┌─────────────┐
         │    OPEN     │ ◀─── 모든 요청 fallback
         │ (장애 상태)  │      1분 동안 유지
         └──────┬──────┘
                │
                │ timeoutMinutes 경과 (1분)
                ▼
         ┌─────────────┐
         │ HALF_OPEN   │ ◀─── 복구 시도
         │ (복구 시도)  │      다음 요청으로 상태 결정
         └──────┬──────┘
                │
         ┌──────┴──────┐
         │             │
    성공 (3회)      실패
         │             │
         ▼             ▼
    ┌───────────┐  ┌───────────┐
    │  CLOSED   │  │   OPEN    │
    └───────────┘  └───────────┘
```

### 2.3 재고 동기화 메커니즘

**두 가지 Set 관리**:
1. `changedProductIds`: Redis에서 변경된 상품 ID (정상 흐름)
2. `pendingSyncProductIds`: DB에서 변경된 상품 ID (장애 흐름)

**동기화 방향**:

| 상황 | 주 저장소 | 동기화 방향 | 설명 |
|------|-----------|-------------|------|
| 정상 | Redis | Redis → DB | 일일 스케줄러로 DB 업데이트 |
| 장애 | DB | DB → Redis | Pending Set을 통해 복구 시 동기화 |

## 3. 주요 파일 경로

| 컴포넌트 | 경로 |
|----------|------|
| ProductEventListener | `eatngo-core/src/main/kotlin/com/eatngo/product/event/ProductEventListener.kt` |
| InventorySyncScheduler | `eatngo-infra/src/main/kotlin/com/eatngo/inventory/batch/InventorySyncScheduler.kt` |
| InventoryCacheWarmer | `eatngo-infra/src/main/kotlin/com/eatngo/inventory/batch/InventoryCacheWarmer.kt` |
| CircuitBreakerManager | `circuit-breaker/src/main/kotlin/com/eatngo/common/circuitbreaker/service/CircuitBreakerManager.kt` |
| RedisCircuitBreakerAspect | `circuit-breaker/src/main/kotlin/com/eatngo/common/circuitbreaker/aspect/RedisCircuitBreakerAspect.kt` |
| InventoryCachePersistence | `eatngo-infra/src/main/kotlin/com/eatngo/inventory/infra/InventoryCachePersistence.kt` |
| InventoryPersistence | `eatngo-infra/src/main/kotlin/com/eatngo/inventory/infra/InventoryPersistence.kt` |

## 4. 개발자 관점 분석

### 4.1 잘 설계된 부분 ✅

1. **서킷브레이커 패턴 적절한 구현**
   - Redis 장애 시 즉시 DB로 fallback하여 서비스 중단 방지
   - 상태 전이 명확 (CLOSED → OPEN → HALF_OPEN → CLOSED)
   - Prometheus 메트릭으로 모니터링 가능

2. **원자적 처리**
   - Lua 스크립트로 Redis 재고 감소를 원자적으로 처리
   - 경쟁 조건(race condition) 방지

3. **이중 저장소 전략**
   - Redis (빠른 읽기/쓰기) + DB (영구 저장)
   - 장애 복구 메커니즘 존재

4. **이벤트 기반 아키텍처**
   - OrderCreatedEvent, OrderCanceledEvent로 재고 변경
   - InventoryChangedEvent로 매장 상태 자동 변경

5. **청크 처리**
   - 500개 단위로 대량 처리
   - 부하 분산

### 4.2 개선이 필요한 부분 ⚠️

1. **데이터 일관성 위험 (가장 중요)**

   **문제**: 정상 흐름에서 Redis가 **단일 소스(Source of Truth)**로 동작
   - Redis의 변경된 재고가 DB로 동기화되는 것은 **매일 12:00에 한 번**
   - Redis 장애 발생 시 최대 24시간 동안의 변경이 DB에 반영되지 않음

   **시나리오**:
   ```
   09:00 - Redis 재고 50개
   09:00-18:00 - 30개 주문 (Redis에서 감소)
   14:00 - Redis 장애 발생
   14:05 - 서킷브레이커 OPEN, DB fallback 시작
          → DB에는 여전히 50개로 기록됨!
   15:00 - 5개 주문 (DB에서 감소 → DB에는 45개)
   16:00 - Redis 복구, 캐시 워밍 실행
          → changedProductIds에 있는 ID들만 워밍
          → DB의 45개가 아닌 이전 값으로 복구될 가능성!
   ```

   **해결 방법 제안**:
   - Redis 변경 시 즉시 DB에도 반영 (Write-Through 캐싱)
   - 또는 변경 시마다 DB에 이벤트를 발행하여 즉시 동기화

2. **캐시 워밍의 불완전성**

   **문제**: `InventoryCacheWarmer`는 `changedProductIds`만 워밍
   - 서킷브레이커 OPEN期间, DB 직접 업데이트된 레코드는 `changedProductIds`에 추가되지 않음
   - 대신 `pendingSyncProductIds`에 추가됨 (코드에 로직이 있어야 함)

   **확인 필요**:
   - `processStockDecreaseFromDB()`에서 `pendingSyncProductIds`에 추가하는 로직이 있는가?
   - 현재 코드: `InventorySyncEvent`만 발행하고 있음

3. **일일 스케줄러 의존성**

   **문제**: DB 동기화가 하루에 한 번 (12:00)만 실행
   - 24시간 동안 Redis와 DB가 불일치할 수 있음
   - Redis 장애 시 데이터 손실 위험

4. **트랜잭션 경계**

   **문제**: `ProductEventListener`에 `@Transactional`이 있지만
   - Redis 연산은 트랜잭션에 포함되지 않음 (Redis는 ACID 지원 안 함)
   - DB 업데이트 후 Redis 업데이트 실패 시 불일치 발생 가능

5. **병목 지점**

   - `changedProductIds` Set이 단일 키로 관리되어 대규모 트래픽 시 병목 가능
   - 분산 락이나 파티셔닝 고려 필요

### 4.3 정상성 판단

**결론**: **부분적으로 정상적이나, 데이터 일관성에 취약함**

**등급**: ⚠️ **양호 (보완 필요)**

| 항목 | 평가 | 비고 |
|------|------|------|
| 가용성 | ✅ 우수 | 서킷브레이커로 장애 대응 잘 됨 |
| 성능 | ✅ 우수 | Redis 캐시로 빠른 응답 |
| 일관성 | ⚠️ 취약 | Redis-DB 간 24시간 불일치 가능 |
| 복구성 | ✅ 양호 | 캐시 워밍 메커니즘 있음 |
| 모니터링 | ✅ 우수 | Prometheus 메트릭 지원 |

## 5. 검증 방법

### 5.1 정상 흐름 테스트
```bash
# 1. 주문 생성
curl -X POST /api/orders -d '{"productId": 1, "quantity": 2}'

# 2. Redis 재고 확인
redis-cli HGET "inventory:hash:1" "stock"

# 3. changedProductIds 확인
redis-cli SMEMBERS "changedProductIds"

# 4. DB 재고 확인 (아직 반영 안 됨)
# SELECT stock FROM inventory WHERE product_id = 1;
```

### 5.2 장애 상황 테스트
```bash
# 1. Redis 중지
docker-compose stop redis

# 2. 주문 생성 (fallback 발생)
curl -X POST /api/orders -d '{"productId": 1, "quantity": 2}'

# 3. DB 재고 확인 (감소되어야 함)
# SELECT stock FROM inventory WHERE product_id = 1;

# 4. 서킷브레이커 상태 확인
# actuator circuit-breaker 상태 확인

# 5. Redis 재시작 후 복구 확인
docker-compose start redis
# 캐시 워밍 확인
```

### 5.3 동기화 테스트
```bash
# 12:00 스케줄러 실행 대기 또는 수동 실행
# 로그 확인: "Redis → RDB 재고 동기화 작업 완료"

# DB와 Redis 재고 비교
redis-cli HGET "inventory:hash:1" "stock"
# SELECT stock FROM inventory WHERE product_id = 1;
```
