#!/bin/bash

# k6 웹 대시보드와 HTML 리포트를 사용한 부하테스트 실행 스크립트
echo "🚀 k6 웹 대시보드 부하테스트 시작"
echo "====================================="

# 결과 디렉토리 생성
mkdir -p monitoring/k6-reports
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# 서버 상태 확인
echo "📊 서버 상태 확인 중..."
curl -f http://localhost:8080/health > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 서버가 실행되지 않았습니다. localhost:8080을 확인해주세요."
    exit 1
fi
echo "✅ 서버 상태 정상"

# Docker Compose로 모니터링 스택 실행
echo ""
echo "🐳 모니터링 스택 시작 중..."
cd monitoring
docker-compose -f docker-compose.production.yml up -d
cd ..

echo "⏳ 서비스 준비 대기 (10초)..."
sleep 10

echo ""
echo "📊 접속 가능한 대시보드:"
echo "  • Grafana: http://localhost:3000"
echo "  • Prometheus: http://localhost:9090"
echo "  • k6 웹 대시보드: http://localhost:5665"
echo ""

# 종합 O2O 시나리오 실행
echo "🎯 종합 O2O 부하테스트 시작"
echo "========================="
echo "📋 포함된 시나리오:"
echo "  • 둘러보기 (40%) - 첫 화면, 무한스크롤, 매장 상세"
echo "  • 상세 검색 (25%) - 키워드 검색, 필터링, 매장 비교"
echo "  • 주문 (25%) - 매장 선택 → 메뉴 → 주문 → 확인"
echo "  • 주문 관리 (10%) - 주문 목록, 상세 조회, 상태 변경"
echo ""

read -p "계속하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    export K6_WEB_DASHBOARD=true
    export K6_WEB_DASHBOARD_EXPORT="monitoring/k6-reports/o2o-comprehensive-report_${TIMESTAMP}.html"
    k6 run --out influxdb=http://localhost:8086/k6 load-tests/o2o-comprehensive-test.js
else
    echo "테스트를 취소했습니다."
    exit 0
fi

echo ""
echo "🎉 부하테스트 완료!"
echo ""
echo "📊 결과 확인:"
echo "  • 실시간 그래프: http://localhost:5665"
echo "  • HTML 리포트: monitoring/k6-reports/o2o-comprehensive-report_${TIMESTAMP}.html"
echo "  • Grafana 대시보드: http://localhost:3000"
echo "  • Prometheus 메트릭: http://localhost:9090"
echo ""
echo "📂 생성된 리포트 파일:"
ls -la monitoring/k6-reports/o2o-comprehensive-report_${TIMESTAMP}.html 2>/dev/null || echo "  리포트 파일을 찾을 수 없습니다."
echo ""
echo "🔧 모니터링 스택 중지하려면:"
echo "  cd monitoring && docker-compose -f docker-compose.production.yml down" 