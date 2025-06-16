# Grafana 대시보드 Import 가이드

## 방법 1: 웹 UI를 통한 수동 Import

### 단계별 진행:

1. **Grafana 접속**
   ```
   http://localhost:3000
   ID: admin / PW: admin123 (또는 설정한 비밀번호)
   ```

2. **Import 메뉴 접근**
   - 왼쪽 사이드바에서 "+" (Plus) 아이콘 클릭
   - 드롭다운에서 "Import" 선택

3. **JSON 파일 업로드**
   - "Upload JSON file" 버튼 클릭
   - `custom-circuit-breaker-dashboard.json` 파일 선택
   - "Load" 버튼 클릭

4. **설정 확인**
   - Dashboard name: "EatNGo Circuit Breaker 모니터링"
   - Folder: "General" (또는 원하는 폴더)
   - Unique identifier (UID): 자동 생성됨

5. **데이터소스 선택**
   - Prometheus 데이터소스 선택
   - 만약 없다면 먼저 데이터소스 추가 필요

6. **Import 완료**
   - "Import" 버튼 클릭
   - 대시보드가 자동으로 열림

## 방법 2: JSON 텍스트 복사/붙여넣기

1. **JSON 파일 내용 복사**
   ```bash
   cat monitoring/grafana/custom-circuit-breaker-dashboard.json | pbcopy
   ```

2. **Grafana Import 페이지**
   - "Import via panel json" 텍스트 영역에 붙여넣기
   - "Load" → 데이터소스 선택 → "Import"

## 방법 3: Docker Compose 자동 프로비저닝

### 설정 완료 후 컨테이너 재시작:
```bash
cd monitoring
docker-compose -f docker-compose.production.yml down
docker-compose -f docker-compose.production.yml up -d
```

### 자동 프로비저닝 확인:
- Grafana 시작 시 자동으로 대시보드 로드
- "EatNGo" 폴더에서 대시보드 확인 가능
- 수정 사항은 파일 변경 후 10초 내 자동 반영

## 방법 4: Grafana API 사용

```bash
# API를 통한 대시보드 import
curl -X POST \
  http://localhost:3000/api/dashboards/db \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer YOUR_API_KEY' \
  -d @custom-circuit-breaker-dashboard.json
```

## 트러블슈팅

### 문제 1: 데이터소스가 없음
```bash
# Prometheus 데이터소스 추가
1. Configuration → Data Sources
2. Add data source → Prometheus
3. URL: http://prometheus:9090 (Docker 환경)
4. Save & Test
```

### 문제 2: 메트릭스가 보이지 않음
```bash
# Prometheus에서 메트릭스 확인
curl http://localhost:9090/api/v1/label/__name__/values | grep circuit_breaker
```

### 문제 3: 권한 오류
```bash
# Grafana 컨테이너 권한 확인
docker exec -it eatngo-grafana ls -la /etc/grafana/provisioning/dashboards/
```

## 대시보드 수정 및 관리

### 대시보드 수정:
1. 대시보드에서 "Settings" (톱니바퀴) 클릭
2. "JSON Model" 탭에서 JSON 확인/수정
3. "Save dashboard" 클릭

### 대시보드 내보내기:
1. "Share" → "Export" → "Save to file"
2. JSON 파일로 저장하여 백업/공유

### 버전 관리:
- Git에 JSON 파일 커밋하여 버전 관리
- 팀원들과 대시보드 설정 공유 가능 