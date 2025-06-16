# Circuit Breaker 대시보드 Import 가이드

## 빠른 시작

### 1. JSON 파일로 Import
```bash
1. Grafana 접속: http://localhost:3000
2. 왼쪽 메뉴 "+" → "Import"
3. "Upload JSON file" → circuit-breaker-dashboard.json 선택
4. 데이터소스: Prometheus 선택
5. Import 클릭
```

### 2. 대시보드 구성

#### 상단 상태 카드 (우아한형제들 스타일)
- **CLOSED** (초록): 정상 동작 중인 Circuit Breaker 수
- **OPEN** (빨강): 차단된 Circuit Breaker 수  
- **HALF_OPEN** (노랑): 복구 시도 중인 Circuit Breaker 수
- **총 개수** (파랑): 전체 Circuit Breaker 수

#### 메트릭스 차트
- **TPS**: 성공/실패 호출 처리량
- **응답시간**: 95th, 50th percentile
- **Fallback**: Fallback 실행 횟수
- **실패 카운트**: 연속 실패 횟수
- **상태 변화**: Circuit Breaker 상태 전환

### 3. 실제 수집되는 메트릭스
```
circuit_breaker_state              # 0: CLOSED, 1: OPEN, 2: HALF_OPEN
circuit_breaker_calls_total        # result=success/failure
circuit_breaker_fallbacks_total    # Fallback 실행
circuit_breaker_calls_duration     # 응답시간 히스토그램
circuit_breaker_state_transitions_total  # 상태 변화
circuit_breaker_failure_count      # 실패 카운트
```

### 4. 알림 설정 권장사항
- OPEN 상태 Circuit Breaker 발생 시 즉시 알림
- Fallback 실행률 급증 시 알림
- 응답시간 95th percentile > 5초 시 알림 