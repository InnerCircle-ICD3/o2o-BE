#!/bin/bash

# O2O 서비스 부하테스트 실행 스크립트
echo "🚀 O2O 서비스 부하테스트 시작"
echo "=================================="

# 결과 디렉토리 생성
mkdir -p load-test-results
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# 1. 서버 상태 확인
echo "📊 서버 상태 확인 중..."
curl -f http://localhost:8080/health > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ 서버가 실행되지 않았습니다. localhost:8080을 확인해주세요."
    exit 1
fi
echo "✅ 서버 상태 정상"

# 2. 기본 헬스체크 테스트
echo ""
echo "🔍 1단계: 기본 헬스체크 테스트"
echo "==============================="
k6 run --out json=load-test-results/health-check_${TIMESTAMP}.json load-tests/simple-test.js

# 3. 매장 검색 부하테스트
echo ""
echo "🏪 2단계: 매장 검색 부하테스트"
echo "==============================="
k6 run --out json=load-test-results/store-search_${TIMESTAMP}.json load-tests/store-search-load-test.js

# 대기 시간 (서버 안정화)
echo "⏳ 서버 안정화 대기 (30초)..."
sleep 30

# 4. 주문 부하테스트 (JWT 토큰 필요 - 실제 토큰으로 수정 필요)
echo ""
echo "🛒 3단계: 주문 부하테스트"
echo "=========================="
echo "⚠️  JWT 토큰을 실제 값으로 수정해주세요!"
read -p "계속하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    k6 run --out json=load-test-results/order-load_${TIMESTAMP}.json load-tests/order-load-test.js
else
    echo "주문 테스트를 건너뜁니다."
fi

# 대기 시간
echo "⏳ 서버 안정화 대기 (30초)..."
sleep 30

# 5. 종합 O2O 시나리오 테스트
echo ""
echo "🎯 4단계: 종합 O2O 시나리오 테스트"
echo "=================================="
k6 run --out json=load-test-results/comprehensive_${TIMESTAMP}.json load-tests/o2o-comprehensive-test.js

# 결과 요약
echo ""
echo "📋 테스트 완료 - 결과 요약"
echo "=========================="
echo "결과 파일 위치: load-test-results/"
ls -la load-test-results/*${TIMESTAMP}*

echo ""
echo "🎉 모든 부하테스트가 완료되었습니다!"
echo ""
echo "📊 Grafana 대시보드에서 Circuit Breaker 메트릭을 확인하세요:"
echo "   http://localhost:3000"
echo ""
echo "📈 k6 결과 분석을 위해 다음 명령어를 사용하세요:"
echo "   k6 cloud login (k6 Cloud 연동 시)"
echo "   또는 JSON 파일을 분석 도구로 처리" 