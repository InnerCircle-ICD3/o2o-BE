# K6 부하 테스트

## 설치
```bash
# macOS
brew install k6

# Ubuntu/Debian
sudo apt update
sudo apt install k6

# Windows (chocolatey)
choco install k6
```

## 🚀 빠른 시작 (웹 대시보드 포함)

### 1. 모든 기능이 포함된 부하테스트
```bash
chmod +x load-tests/run-with-dashboard.sh
./load-tests/run-with-dashboard.sh
```

### 2. 개별 테스트 실행 (웹 대시보드 + HTML 리포트)
```bash
# 환경변수 설정
export K6_WEB_DASHBOARD=true
export K6_WEB_DASHBOARD_EXPORT="monitoring/k6-reports/my-report.html"

# 테스트 실행
k6 run --out influxdb=http://localhost:8086/k6 load-tests/o2o-comprehensive-test.js
```

## 📊 대시보드 접속

테스트 실행 후 다음 URL에서 결과를 확인할 수 있습니다:

- **k6 웹 대시보드**: http://localhost:5665 (실시간 모니터링)
- **Grafana**: http://localhost:3000 (Circuit Breaker 메트릭)
- **Prometheus**: http://localhost:9090 (원본 메트릭)

## 테스트 시나리오

### 종합 O2O 부하테스트 (`o2o-comprehensive-test.js`)

모든 주요 기능을 포함한 통합 시나리오:

- **둘러보기 (40%)** - 첫 화면 접속, 무한스크롤, 매장 상세 조회
- **상세 검색 (25%)** - 키워드 검색, 자동완성, 카테고리 필터링, 매장 비교
- **주문 (25%)** - 매장 선택 → 상품 조회 → 주문 생성 → 확인
- **주문 관리 (10%)** - 주문 목록, 상세 조회, 상태별 액션

**부하 패턴**: 하루 종일 실제 사용자 패턴 (오전→점심러시→오후→저녁러시)

## 🎯 실제 사용 패턴

### 주문 시나리오
1. 매장 상품 조회 → 메뉴 선택 → 주문 생성 → 주문 확인
2. 20% 확률로 주문 후 즉시 취소 (변심)
3. 10% 확률로 주문 목록에서 재확인

### 주문 관리 시나리오
1. 내 주문 목록 조회 (상태 필터링 40% 확률)
2. 커서 기반 페이지네이션으로 과거 주문 더 보기
3. 주문 상태별 액션:
   - `PENDING/CONFIRMED` → 취소
   - `READY` → 픽업 확인
   - `COMPLETED` → 완료 확인

## 📈 HTML 리포트 기능

테스트 완료 후 `monitoring/k6-reports/` 디렉토리에 HTML 리포트가 생성됩니다:

```
monitoring/k6-reports/
└── o2o-comprehensive-report_20241219_143022.html
```

HTML 리포트에는 다음이 포함됩니다:
- 📊 실시간 그래프 (응답시간, 처리량, 오류율)
- 📋 상세 메트릭 (p95, p99 응답시간 등)
- 🎯 체크 결과 (각 시나리오별 성공률)
- 📝 요약 보고서

## 결과 해석

- **VUs (Virtual Users)**: 동시 사용자 수
- **Iterations**: 총 요청 수
- **http_req_duration**: 응답 시간
- **http_req_failed**: 실패율
- **checks**: 테스트 통과율

## Circuit Breaker 모니터링

부하테스트와 함께 Circuit Breaker 상태를 모니터링:

```bash
# Prometheus & Grafana 실행
cd monitoring
docker-compose -f docker-compose.production.yml up -d

# 부하테스트 실행
./load-tests/run-with-dashboard.sh

# Grafana에서 Circuit Breaker 대시보드 확인
# http://localhost:3000
```

## 옵션 설정

각 스크립트의 `options` 객체에서 부하 패턴 조정 가능:

```javascript
export let options = {
  stages: [
    { duration: "2m", target: 100 }, // 2분간 100명까지
    { duration: "5m", target: 100 }, // 5분간 100명 유지
    { duration: "2m", target: 200 }, // 2분간 200명까지
    { duration: "5m", target: 200 }, // 5분간 200명 유지
    { duration: "2m", target: 0 },   // 2분간 0명까지
  ],
};
```

## 🔧 트러블슈팅

### JWT 토큰 만료 시
```javascript
// o2o-comprehensive-test.js 또는 order-load-test.js에서 토큰 업데이트
const JWT_TOKENS = [
  "Bearer your-new-token-1",
  "Bearer your-new-token-2",
];
```

### 서버 연결 실패 시
```bash
# 서버 상태 확인
curl http://localhost:8080/health

# 또는 실제 도메인으로 변경
# BASE_URL을 https://customer.eatngo.org로 수정
``` 