# EatNGo 모니터링 시스템

## 📊 구성 요소
- **Prometheus**: 메트릭 수집 및 저장
- **Grafana**: 대시보드 시각화

## 🚀 로컬 실행

### 1. 개발용 (로컬 호스트)
```bash
cd monitoring
docker-compose up -d
```
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/eatngo123)

### 2. 운영용 (실제 도메인)
```bash
cd monitoring
export GRAFANA_PASSWORD="복잡한_비밀번호"
docker-compose -f docker-compose.production.yml up -d
```

## 🌍 팀원 공유 방법

### Option 1: 각자 로컬 실행
```bash
git pull origin main
cd monitoring  
docker-compose up -d
# 각자 localhost:3000 접근
```

### Option 2: AWS 공용 서버 배포
1. EC2 인스턴스 생성
2. Docker 설치
3. 프로젝트 클론 후 production 모드 실행
4. 도메인: monitor.eatngo.org 설정

## 📈 메트릭 확인 방법

### Circuit Breaker 메트릭 생성
1. API 호출 (구독 조회 등)
2. Redis 장애 시뮬레이션
3. Grafana에서 실시간 확인

### 주요 메트릭
- `circuit_breaker_state`: Circuit Breaker 상태
- `circuit_breaker_calls`: 호출 성공/실패 수
- `circuit_breaker_fallbacks`: Fallback 실행 수

## 🔧 설정 파일

```
monitoring/
├── docker-compose.yml              # 로컬 개발용
├── docker-compose.production.yml   # 운영 배포용
├── prometheus/
│   ├── prometheus.yml              # 로컬용 (localhost:8080/8081)
│   └── prometheus-production.yml   # 운영용 (www.eatngo.org)
└── grafana/
    ├── provisioning/               # 자동 설정
    └── dashboards/                 # 대시보드 정의
```

## 🚨 운영 배포 시 필요사항

1. **DNS 설정**: monitor.eatngo.org
2. **SSL 인증서**: Let's Encrypt or AWS Certificate Manager  
3. **보안 그룹**: 9090, 3000 포트 열기
4. **환경변수**: GRAFANA_PASSWORD 설정 