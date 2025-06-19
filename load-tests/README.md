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

## 테스트 실행

### 기본 헬스체크 테스트
```bash
k6 run load-tests/basic-test.js
```

### 매장 API 테스트
```bash
k6 run load-tests/store-api-test.js
```

### 인증 필요 API 테스트
```bash
# JWT 토큰을 실제 값으로 수정한 후 실행
k6 run load-tests/authenticated-test.js
```

## 결과 해석

- **VUs (Virtual Users)**: 동시 사용자 수
- **Iterations**: 총 요청 수
- **http_req_duration**: 응답 시간
- **http_req_failed**: 실패율
- **checks**: 테스트 통과율

## 모니터링

Circuit Breaker 메트릭과 함께 모니터링하려면:

```bash
# Prometheus & Grafana 실행
cd monitoring
docker-compose -f docker-compose.production.yml up -d

# 부하테스트 실행
k6 run load-tests/store-api-test.js

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