import { check, sleep } from "k6";
import http from "k6/http";
import { randomItem } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// O2O 종합 시나리오 - 실제 사용자 패턴 반영
export let options = {
  stages: [
    { duration: "1m", target: 30 },    // 오전 시간 (앱 확인)
    { duration: "2m", target: 80 },    // 점심 준비 시간
    { duration: "5m", target: 200 },   // 점심 러시 (11:30-12:30)
    { duration: "3m", target: 400 },   // 극한 피크 (12:00-12:30)
    { duration: "2m", target: 150 },   // 점심 후반
    { duration: "2m", target: 50 },    // 오후 시간
    { duration: "2m", target: 120 },   // 저녁 준비
    { duration: "4m", target: 350 },   // 저녁 러시 (18:00-19:30)
    { duration: "2m", target: 100 },   // 저녁 후반
    { duration: "1m", target: 0 },     // 마감
  ],
};

const BASE_URL = "http://localhost:8080";

const JWT_TOKENS = [
  "Bearer customer-token-1", "Bearer customer-token-2", "Bearer customer-token-3",
  "Bearer customer-token-4", "Bearer customer-token-5", "Bearer customer-token-6",
];

// 실제 서울 지역 좌표와 특성
const LOCATIONS = [
  { lat: 37.566500, lng: 126.978011, name: "명동", isBusinessDistrict: true },
  { lat: 37.517235, lng: 127.047325, name: "강남", isBusinessDistrict: true },
  { lat: 37.579617, lng: 126.977041, name: "홍대", isBusinessDistrict: false },
  { lat: 37.540705, lng: 126.956203, name: "용산", isBusinessDistrict: false },
  { lat: 37.494942, lng: 127.027618, name: "서초", isBusinessDistrict: true },
  { lat: 37.572859, lng: 126.971428, name: "종로", isBusinessDistrict: true },
];

const FOOD_CATEGORIES = ["KOREAN", "PIZZA", "CAFE", "BREAD", "RICECAKE"];
const SEARCH_KEYWORDS = ["치킨", "피자", "커피", "햄버거", "쌀국수", "초밥", "파스타", "샐러드", "떡"];

export default function () {
  const token = randomItem(JWT_TOKENS);
  const location = randomItem(LOCATIONS);
  const headers = {
    'Authorization': token,
    'Content-Type': 'application/json',
  };

  // 시간대별 사용자 행동 패턴
  const scenario = getScenarioByTime();
  
  switch (scenario) {
    case 'browse':
      browsingScenario(location, headers);
      break;
    case 'quick_order':
      quickOrderScenario(location, headers);
      break;
    case 'detailed_search':
      detailedSearchScenario(location, headers);
      break;
    case 'subscription_check':
      subscriptionScenario(location, headers);
      break;
    case 'order_management':
      orderManagementScenario(headers);
      break;
  }
  
  sleep(Math.random() * 4 + 1); // 1-5초 랜덤 대기
}

function getScenarioByTime() {
  const scenarios = ['browse', 'quick_order', 'detailed_search', 'subscription_check', 'order_management'];
  const weights = [0.3, 0.35, 0.15, 0.1, 0.1]; // 가중치
  
  const random = Math.random();
  let cumulative = 0;
  
  for (let i = 0; i < scenarios.length; i++) {
    cumulative += weights[i];
    if (random < cumulative) {
      return scenarios[i];
    }
  }
  return 'browse';
}

// 1. 단순 둘러보기 시나리오 (첫 화면, 무한스크롤)
function browsingScenario(location, headers) {
  // 첫 화면 로드
  let initialLoad = http.get(
    `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20`
  );
  
  check(initialLoad, {
    "둘러보기 첫 화면 로드": (r) => r.status === 200,
    "둘러보기 응답시간 < 1s": (r) => r.timings.duration < 1000,
  });

  sleep(3); // 화면 확인 시간

  // 무한스크롤 1-3회
  const scrollCount = Math.floor(Math.random() * 3) + 1;
  for (let i = 0; i < scrollCount; i++) {
    let scrollLoad = http.get(
      `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20&lastId=${Math.random().toString()}`
    );
    
    check(scrollLoad, {
      [`무한스크롤 ${i+1} 성공`]: (r) => r.status === 200,
    });
    
    sleep(2);
  }

  // 매장 상세 보기 (50% 확률)
  if (Math.random() < 0.5) {
    const storeId = Math.floor(Math.random() * 10) + 1;
    let storeDetail = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
    
    check(storeDetail, {
      "매장 상세 조회 성공": (r) => r.status === 200,
    });
  }
}

// 2. 빠른 주문 시나리오 (자주 가는 매장, 단골 메뉴)
function quickOrderScenario(location, headers) {
  const storeId = Math.floor(Math.random() * 5) + 1; // 자주 가는 매장 (1-5)
  
  // 바로 주문 (매장 상세 조회 생략)
  const quickOrderPayload = JSON.stringify({
    storeId: storeId,
    pickupDateTime: getRandomPickupTime(),
    orderItems: [
      {
        productId: Math.floor(Math.random() * 10) + 1,
        productName: "단골 메뉴",
        quantity: 1,
        price: 15000,
      }
    ]
  });

  let quickOrder = http.post(`${BASE_URL}/api/v1/orders`, quickOrderPayload, { headers });
  
  check(quickOrder, {
    "빠른 주문 성공": (r) => r.status === 200 || r.status === 201,
    "빠른 주문 응답시간 < 2s": (r) => r.timings.duration < 2000,
  });

  // 주문 확인
  if (quickOrder.status === 200 || quickOrder.status === 201) {
    sleep(1);
    const orderData = quickOrder.json();
    if (orderData && orderData.data && orderData.data.orderId) {
      let orderCheck = http.get(`${BASE_URL}/api/v1/orders/${orderData.data.orderId}`, { headers });
      check(orderCheck, {
        "빠른 주문 확인": (r) => r.status === 200,
      });
    }
  }
}

// 3. 상세 검색 시나리오 (키워드 검색, 필터링, 비교)
function detailedSearchScenario(location, headers) {
  const keyword = randomItem(SEARCH_KEYWORDS);
  
  // 1. 검색어 자동완성
  let autoComplete = http.get(`${BASE_URL}/api/v1/search/suggestions?keyword=${keyword.substring(0, 1)}`);
  check(autoComplete, {
    "자동완성 성공": (r) => r.status === 200,
  });

  sleep(1);

  // 2. 키워드 검색
  let searchResult = http.get(
    `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&searchText=${keyword}&size=20`
  );
  
  check(searchResult, {
    "키워드 검색 성공": (r) => r.status === 200,
    "키워드 검색 응답시간 < 1.5s": (r) => r.timings.duration < 1500,
  });

  sleep(2);

  // 3. 카테고리 필터 적용
  const category = randomItem(FOOD_CATEGORIES);
  let filteredSearch = http.get(
    `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&searchText=${keyword}&storeCategory=${category}&size=20`
  );
  
  check(filteredSearch, {
    "필터 검색 성공": (r) => r.status === 200,
  });

  sleep(2);

  // 4. 여러 매장 비교 (2-3개 매장 상세 보기)
  const compareCount = Math.floor(Math.random() * 2) + 2;
  for (let i = 0; i < compareCount; i++) {
    const storeId = Math.floor(Math.random() * 15) + 1;
    let comparison = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
    
    check(comparison, {
      [`매장 비교 ${i+1} 성공`]: (r) => r.status === 200,
    });
    
    sleep(3); // 매장 정보 검토 시간
  }
}

// 4. 구독 관리 시나리오
function subscriptionScenario(location, headers) {
  // 구독 매장 목록 조회
  let subscriptions = http.get(`${BASE_URL}/api/v1/store-subscriptions/store-ids`, { headers });
  
  check(subscriptions, {
    "구독 목록 조회 성공": (r) => r.status === 200,
    "구독 목록 응답시간 < 500ms": (r) => r.timings.duration < 500,
  });

  sleep(1);

  // 구독 매장들의 상세 정보 확인 (구독 목록이 있다면)
  if (subscriptions.status === 200) {
    // 시뮬레이션: 구독 매장 1-2개 확인
    const checkCount = Math.floor(Math.random() * 2) + 1;
    for (let i = 0; i < checkCount; i++) {
      const storeId = Math.floor(Math.random() * 10) + 1;
      let favoriteStore = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
      
      check(favoriteStore, {
        [`구독매장 ${i+1} 확인 성공`]: (r) => r.status === 200,
      });
      
      sleep(2);
    }
  }
}

// 5. 주문 관리 시나리오
function orderManagementScenario(headers) {
  // 내 주문 목록 조회
  let myOrders = http.get(`${BASE_URL}/api/v1/customers/orders?size=10`, { headers });
  
  check(myOrders, {
    "주문 목록 조회 성공": (r) => r.status === 200,
    "주문 목록 응답시간 < 800ms": (r) => r.timings.duration < 800,
  });

  sleep(2);

  // 최근 주문 상세 조회 (50% 확률)
  if (Math.random() < 0.5) {
    const orderId = Math.floor(Math.random() * 100) + 1; // 시뮬레이션
    let orderDetail = http.get(`${BASE_URL}/api/v1/orders/${orderId}`, { headers });
    
    check(orderDetail, {
      "주문 상세 조회": (r) => r.status === 200 || r.status === 404, // 404도 정상 (주문이 없을 수 있음)
    });

    // 주문 상태 변경 (30% 확률로 취소 시도)
    if (orderDetail.status === 200 && Math.random() < 0.3) {
      sleep(1);
      let cancelOrder = http.post(`${BASE_URL}/api/v1/orders/${orderId}/cancel`, {}, { headers });
      
      check(cancelOrder, {
        "주문 취소 시도": (r) => r.status === 200 || r.status === 400, // 400도 정상 (취소 불가 상태)
      });
    }
  }
}

// 헬퍼 함수들
function getRandomPickupTime() {
  const now = new Date();
  const pickupTime = new Date(now.getTime() + (Math.random() * 180 + 30) * 60000); // 30분~3시간 후
  return pickupTime.toISOString();
} 