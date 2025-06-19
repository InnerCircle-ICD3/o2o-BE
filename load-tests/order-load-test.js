import { check, sleep } from "k6";
import http from "k6/http";
import { randomItem } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// 주문 러시 시간 시뮬레이션 (점심시간 11:30-13:00, 저녁시간 17:30-19:30)
export let options = {
  stages: [
    { duration: "1m", target: 10 },    // 준비 단계
    { duration: "2m", target: 50 },    // 주문 시작
    { duration: "3m", target: 150 },   // 점심 러시 시작
    { duration: "5m", target: 300 },   // 피크 타임 (동시 주문 폭증)
    { duration: "2m", target: 500 },   // 극한 스파이크 (재고 경합 상황)
    { duration: "3m", target: 200 },   // 점진적 감소
    { duration: "2m", target: 50 },    // 러시 종료
    { duration: "1m", target: 0 },     // 완전 종료
  ],
};

const BASE_URL = "http://localhost:8080";

// 테스트용 JWT 토큰들 (실제로는 여러 사용자)
const JWT_TOKENS = [
  "Bearer test-token-1",
  "Bearer test-token-2", 
  "Bearer test-token-3",
  "Bearer test-token-4",
  "Bearer test-token-5",
];

// 인기 매장 ID들 (재고 경합이 발생하기 쉬운 매장들)
const POPULAR_STORE_IDS = [1, 2, 3, 4, 5];

// 일반 매장 ID들  
const NORMAL_STORE_IDS = [6, 7, 8, 9, 10, 11, 12, 13, 14, 15];

// 인기 상품 ID들 (재고 경합 대상)
const POPULAR_PRODUCT_IDS = [1, 2, 3, 4, 5];
const NORMAL_PRODUCT_IDS = [6, 7, 8, 9, 10, 11, 12, 13, 14, 15];

// 픽업 시간 옵션 (현재 시간 기준 30분~3시간 후)
function getRandomPickupTime() {
  const now = new Date();
  const pickupTime = new Date(now.getTime() + (Math.random() * 150 + 30) * 60000); // 30분~3시간 후
  return pickupTime.toISOString();
}

export default function () {
  const token = randomItem(JWT_TOKENS);
  const headers = {
    'Authorization': token,
    'Content-Type': 'application/json',
  };

  const userBehavior = Math.random();
  
  // 70% - 일반 주문 플로우
  if (userBehavior < 0.7) {
    normalOrderFlow(headers);
  }
  // 20% - 인기 매장 주문 (재고 경합)
  else if (userBehavior < 0.9) {
    popularStoreOrderFlow(headers);
  }
  // 10% - 대량 주문 (단체 주문)
  else {
    bulkOrderFlow(headers);
  }
  
  sleep(Math.random() * 2 + 0.5); // 0.5-2.5초 대기
}

function normalOrderFlow(headers) {
  const storeId = randomItem(NORMAL_STORE_IDS);
  const productId = randomItem(NORMAL_PRODUCT_IDS);
  
  // 1. 매장 상세 조회 (주문 전 확인)
  let storeDetail = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
  
  check(storeDetail, {
    "매장 상세 조회 성공": (r) => r.status === 200,
    "매장 상세 응답시간 < 500ms": (r) => r.timings.duration < 500,
  });

  sleep(2); // 메뉴 선택 시간

  // 2. 주문 생성
  const orderPayload = JSON.stringify({
    storeId: storeId,
    pickupDateTime: getRandomPickupTime(),
    orderItems: [
      {
        productId: productId,
        productName: `상품 ${productId}`,
        quantity: Math.floor(Math.random() * 3) + 1, // 1-3개
        price: Math.floor(Math.random() * 20000) + 5000, // 5000-25000원
      }
    ]
  });

  let orderResponse = http.post(`${BASE_URL}/api/v1/orders`, orderPayload, { headers });
  
  check(orderResponse, {
    "일반 주문 생성 성공": (r) => r.status === 200 || r.status === 201,
    "일반 주문 응답시간 < 2s": (r) => r.timings.duration < 2000,
    "일반 주문 응답 데이터 존재": (r) => r.body && r.body.length > 0,
  });

  // 3. 주문 상세 조회 (확인)
  if (orderResponse.status === 200 || orderResponse.status === 201) {
    const responseBody = orderResponse.json();
    if (responseBody && responseBody.data && responseBody.data.orderId) {
      sleep(1);
      
      let orderDetail = http.get(`${BASE_URL}/api/v1/orders/${responseBody.data.orderId}`, { headers });
      
      check(orderDetail, {
        "주문 상세 조회 성공": (r) => r.status === 200,
        "주문 상세 응답시간 < 800ms": (r) => r.timings.duration < 800,
      });
    }
  }
}

function popularStoreOrderFlow(headers) {
  const storeId = randomItem(POPULAR_STORE_IDS);
  const productId = randomItem(POPULAR_PRODUCT_IDS);
  
  // 인기 매장의 경우 더 빠른 주문 진행 (재고 경합 상황 시뮬레이션)
  sleep(0.5); // 빠른 결정
  
  // 동시 주문으로 재고 경합 발생 시뮬레이션
  const orderPayload = JSON.stringify({
    storeId: storeId,
    pickupDateTime: getRandomPickupTime(),
    orderItems: [
      {
        productId: productId,
        productName: `인기상품 ${productId}`,
        quantity: Math.floor(Math.random() * 5) + 1, // 1-5개 (더 많은 수량)
        price: Math.floor(Math.random() * 30000) + 10000, // 10000-40000원
      }
    ]
  });

  let orderResponse = http.post(`${BASE_URL}/api/v1/orders`, orderPayload, { headers });
  
  check(orderResponse, {
    "인기매장 주문 처리": (r) => r.status === 200 || r.status === 201 || r.status === 409, // 409는 재고 부족
    "인기매장 주문 응답시간 < 3s": (r) => r.timings.duration < 3000,
  });

  // 재고 부족 시 다른 상품으로 재시도 (30% 확률)
  if (orderResponse.status === 409 && Math.random() < 0.3) {
    sleep(1);
    
    const alternativeProductId = randomItem(NORMAL_PRODUCT_IDS);
    const retryPayload = JSON.stringify({
      storeId: storeId,
      pickupDateTime: getRandomPickupTime(),
      orderItems: [
        {
          productId: alternativeProductId,
          productName: `대체상품 ${alternativeProductId}`,
          quantity: 1,
          price: Math.floor(Math.random() * 20000) + 8000,
        }
      ]
    });

    let retryResponse = http.post(`${BASE_URL}/api/v1/orders`, retryPayload, { headers });
    
    check(retryResponse, {
      "재시도 주문 성공": (r) => r.status === 200 || r.status === 201,
      "재시도 주문 응답시간 < 2s": (r) => r.timings.duration < 2000,
    });
  }
}

function bulkOrderFlow(headers) {
  const storeId = randomItem([...POPULAR_STORE_IDS, ...NORMAL_STORE_IDS]);
  
  // 대량 주문 (3-7개 상품)
  const itemCount = Math.floor(Math.random() * 5) + 3;
  const orderItems = [];
  
  for (let i = 0; i < itemCount; i++) {
    const productId = Math.random() < 0.3 ? 
      randomItem(POPULAR_PRODUCT_IDS) : 
      randomItem(NORMAL_PRODUCT_IDS);
      
    orderItems.push({
      productId: productId,
      productName: `상품 ${productId}`,
      quantity: Math.floor(Math.random() * 3) + 1,
      price: Math.floor(Math.random() * 25000) + 5000,
    });
  }

  const bulkOrderPayload = JSON.stringify({
    storeId: storeId,
    pickupDateTime: getRandomPickupTime(),
    orderItems: orderItems
  });

  let bulkOrderResponse = http.post(`${BASE_URL}/api/v1/orders`, bulkOrderPayload, { headers });
  
  check(bulkOrderResponse, {
    "대량 주문 처리": (r) => r.status === 200 || r.status === 201 || r.status === 409,
    "대량 주문 응답시간 < 5s": (r) => r.timings.duration < 5000, // 더 긴 처리 시간 허용
  });

  // 대량 주문 후 주문 목록 조회 (20% 확률)
  if ((bulkOrderResponse.status === 200 || bulkOrderResponse.status === 201) && Math.random() < 0.2) {
    sleep(2);
    
    let orderList = http.get(`${BASE_URL}/api/v1/customers/orders?size=10`, { headers });
    
    check(orderList, {
      "주문 목록 조회 성공": (r) => r.status === 200,
      "주문 목록 응답시간 < 1s": (r) => r.timings.duration < 1000,
    });
  }
} 