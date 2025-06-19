import { check, sleep } from "k6";
import http from "k6/http";
import { randomItem } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// O2O 종합 시나리오 - 실제 사용자 패턴 반영
export let options = {
  stages: [
    { duration: "1m", target: 30 },    // 오전 시간 (앱 확인)
    // { duration: "2m", target: 80 },    // 점심 준비 시간
    // { duration: "5m", target: 200 },   // 점심 러시 (11:30-12:30)
    // { duration: "3m", target: 400 },   // 극한 피크 (12:00-12:30)
    // { duration: "2m", target: 150 },   // 점심 후반
    // { duration: "2m", target: 50 },    // 오후 시간
    // { duration: "2m", target: 120 },   // 저녁 준비
    // { duration: "4m", target: 350 },   // 저녁 러시 (18:00-19:30)
    // { duration: "2m", target: 100 },   // 저녁 후반
    // { duration: "1m", target: 0 },     // 마감
  ],
};

const BASE_URL = "https://customer.eatngo.org";

// 실제 JWT 토큰 2개만 사용
const JWT_TOKENS = [
  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwicm9sZXMiOlsiVVNFUiIsIkNVU1RPTUVSIl0sImN1c3RvbWVySWQiOjMsIm5pY2tuYW1lIjoi67CV7ZiE7KO8IiwiaWF0IjoxNzUwMzUyODk4LCJleHAiOjE3NTAzNTY0OTh9.UgT0ZX0JFV2P4-yToDhBV29S_wmW1bdVJkd1i4gec_U",
  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZXMiOlsiVVNFUiIsIkNVU1RPTUVSIl0sImN1c3RvbWVySWQiOjIsIm5pY2tuYW1lIjoi7J2064uk7JiBIiwiaWF0IjoxNzUwMzUyOTgyLCJleHAiOjE3NTAzNTY1ODJ9.nlXM94L0PZCuUOZz7saVTqWk2l9PVKSyG9ET-dw5vu0",
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
// const SEARCH_KEYWORDS = ["치킨", "피자", "커피", "햄버거", "쌀국수", "초밥", "파스타", "샐러드", "떡", "빵", "카페"];
const SEARCH_KEYWORDS = ["%EC%B9%98%ED%82%A8", "%ED%94%BC%EC%9E%90", "%EC%BB%A4%ED%94%BC", "%ED%96%84%EB%B2%84%EA%B1%B0", "%EC%8C%80%EA%B5%AD%EC%88%98", "%EC%B4%88%EB%B0%A5", "%ED%8C%8C%EC%8A%A4%ED%83%80", "%EC%83%90%EB%9F%AC%EB%93%9C", "%EB%96%A1", "%EB%B9%B5", "%EC%B9%B4%ED%8E%98"]

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
    case 'detailed_search':
      detailedSearchScenario(location, headers);
      break;
    case 'order':
      orderScenario(location, headers);
      break;
    case 'order_management':
      orderManagementScenario(headers);
      break;
  }
  
  sleep(Math.random() * 4 + 1); // 1-5초 랜덤 대기
}

function getScenarioByTime() {
  const scenarios = ['browse', 'detailed_search', 'order', 'order_management'];
  const weights = [0.4, 0.25, 0.25, 0.1]; // 가중치
  
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
  // const scrollCount = Math.floor(Math.random() * 3) + 1;
  // for (let i = 0; i < scrollCount; i++) {
  //   let scrollLoad = http.get(
  //     `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20&lastId=${Math.random().toString()}`
  //   );
  //
  //   check(scrollLoad, {
  //     [`무한스크롤 ${i+1} 성공`]: (r) => r.status === 200,
  //   });
  //
  //   sleep(2);
  // }

  // 매장 상세 보기 (50% 확률)
  if (Math.random() < 0.5) {
    const storeId = Math.floor(Math.random() * 5) + 1;
    
    // 매장 기본 정보 조회
    let storeDetail = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
    
    check(storeDetail, {
      "매장 상세 조회 성공": (r) => r.status === 200,
      "매장 상세 응답시간 < 800ms": (r) => r.timings.duration < 800,
    });
    
    sleep(1);
    
    // 매장 상품 목록 조회 (30% 확률)
    if (Math.random() < 0.3) {
      let storeProducts = http.get(`${BASE_URL}/api/v1/stores/${storeId}/products`, { headers });
      
      check(storeProducts, {
        "매장 상품 조회 완료": (r) => r.status === 200 || r.status === 404, // 상품이 없으면 404도 정상
        "매장 상품 응답시간 < 600ms": (r) => r.timings.duration < 600,
      });
    }
  }
}

// 2. 주문 시나리오 (매장 선택 → 메뉴 선택 → 주문)
function orderScenario(location, headers) {
  // 상품이 있을 가능성이 높은 매장 ID (1-5번)를 우선 선택
  const storeId = Math.floor(Math.random() * 5) + 1;
  
  // 1. 매장 상품 조회 (메뉴 확인)
  let storeProducts = http.get(`${BASE_URL}/api/v1/stores/${storeId}/products`, { headers });
  
  check(storeProducts, {
    "매장 상품 조회 완료": (r) => r.status === 200 || r.status === 404, // 상품이 없으면 404도 정상
    "매장 상품 응답시간 < 1s": (r) => r.timings.duration < 1000,
  });

  // 상품이 없으면 주문 시나리오 건너뛰기
  if (storeProducts.status === 404) {
    console.log(`매장 ${storeId}에 상품이 없어서 주문을 건너뜁니다.`);
    return;
  }

  sleep(3); // 메뉴 선택 시간

  // 2. 주문 생성
  const orderItems = [];
  const itemCount = Math.floor(Math.random() * 3) + 1; // 1-3개 상품
  
  // 실제 상품이 있는 매장의 상품 ID 범위를 줄임 (1-5번 상품만 사용)
  for (let i = 0; i < itemCount; i++) {
    orderItems.push({
      productId: Math.floor(Math.random() * 5) + 1, // 1-5번 상품만
      productName: `상품 ${Math.floor(Math.random() * 5) + 1}`,
      quantity: Math.floor(Math.random() * 2) + 1, // 1-2개
      price: Math.floor(Math.random() * 20000) + 5000, // 5000-25000원
    });
  }

  const orderPayload = JSON.stringify({
    storeId: storeId,
    pickupDateTime: getRandomPickupTime(),
    orderItems: orderItems
  });

  let orderResponse = http.post(`${BASE_URL}/api/v1/orders`, orderPayload, { headers });
  
  check(orderResponse, {
    "주문 생성 완료": (r) => r.status === 200 || r.status === 201 || r.status === 400, // 400도 허용 (재고 부족 등)
    "주문 생성 응답시간 < 3s": (r) => r.timings.duration < 3000,
    "주문 응답 데이터 존재": (r) => r.body && r.body.length > 0,
  });

  // 3. 주문 확인 및 후속 액션
  if (orderResponse.status === 200 || orderResponse.status === 201) {
    sleep(1);
    
    try {
      const orderData = orderResponse.json();
      if (orderData && orderData.data && orderData.data.orderId) {
        const orderId = orderData.data.orderId;
        
        // 주문 상세 확인
        let orderCheck = http.get(`${BASE_URL}/api/v1/orders/${orderId}`, { headers });
        check(orderCheck, {
          "주문 상세 확인": (r) => r.status === 200,
          "주문 확인 응답시간 < 1s": (r) => r.timings.duration < 1000,
        });

        // 20% 확률로 주문 후 즉시 취소 (변심)
        if (Math.random() < 0.2) {
          sleep(2); // 잠시 생각하고
          
          let cancelOrder = http.post(`${BASE_URL}/api/v1/orders/${orderId}/cancel`, {}, { headers });
          
          check(cancelOrder, {
            "주문 즉시 취소": (r) => r.status === 200 || r.status === 400,
            "취소 응답시간 < 1s": (r) => r.timings.duration < 1000,
          });
        }
        
                 // 10% 확률로 주문 목록에서 재확인
         else if (Math.random() < 0.1) {
           sleep(1);
           
           let myOrders = http.get(`${BASE_URL}/api/v1/customers/orders?size=5`, { headers });
           
           check(myOrders, {
             "주문 후 목록 확인": (r) => r.status === 200,
           });
           
           // 추가 페이지 조회 (30% 확률)
           if (Math.random() < 0.3 && myOrders.status === 200) {
             try {
               const ordersData = myOrders.json();
               if (ordersData && ordersData.data && ordersData.data.lastId) {
                 sleep(1);
                 
                 let nextPage = http.get(`${BASE_URL}/api/v1/customers/orders?size=5&lastId=${ordersData.data.lastId}`, { headers });
                 
                 check(nextPage, {
                   "다음 페이지 조회": (r) => r.status === 200,
                 });
               }
             } catch (e) {
               // JSON 파싱 실패 시 무시
             }
           }
         }
      }
    } catch (e) {
      // JSON 파싱 실패 시 로그만 남기고 계속 진행
      console.log("주문 응답 데이터 파싱 실패");
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
    const storeId = Math.floor(Math.random() * 5) + 1;
    let comparison = http.get(`${BASE_URL}/api/v1/stores/${storeId}`, { headers });
    
    check(comparison, {
      [`매장 비교 ${i+1} 성공`]: (r) => r.status === 200,
    });
    
    sleep(3); // 매장 정보 검토 시간
  }
}

// 4. 주문 관리 시나리오
function orderManagementScenario(headers) {
  // 주문 상태 필터 (40% 확률로 특정 상태만 조회)
  let ordersUrl = `${BASE_URL}/api/v1/customers/orders?size=10`;
  
  if (Math.random() < 0.4) {
    const statusFilter = randomItem(['CREATED', 'READY', 'CONFIRMED', 'CANCELED', 'DONE']);
    ordersUrl += `&status=${statusFilter}`;
  }
  
  // 내 주문 목록 조회
  let myOrders = http.get(ordersUrl, { headers });
  
  check(myOrders, {
    "주문 목록 조회 성공": (r) => r.status === 200,
    "주문 목록 응답시간 < 800ms": (r) => r.timings.duration < 800,
  });

  sleep(2);
  
  // 추가 페이지 조회 (20% 확률로 과거 주문 더 보기)
  if (Math.random() < 0.2 && myOrders.status === 200) {
    try {
      const ordersData = myOrders.json();
      if (ordersData && ordersData.data && ordersData.data.lastId) {
        sleep(1);
        
        let nextPageOrders = http.get(`${BASE_URL}/api/v1/customers/orders?size=10&lastId=${ordersData.data.lastId}`, { headers });
        
        check(nextPageOrders, {
          "과거 주문 추가 조회": (r) => r.status === 200,
          "추가 페이지 응답시간 < 600ms": (r) => r.timings.duration < 600,
        });
        
        sleep(1);
      }
    } catch (e) {
      // JSON 파싱 실패 시 무시
    }
  }

  // 주문 목록에서 실제 주문 ID 가져오기
  if (myOrders.status === 200) {
    try {
      const ordersData = myOrders.json();
      if (ordersData && ordersData.data && ordersData.data.orders && ordersData.data.orders.length > 0) {
        const randomOrder = randomItem(ordersData.data.orders);
        const orderId = randomOrder.orderId;
        
        // 주문 상세 조회
        let orderDetail = http.get(`${BASE_URL}/api/v1/orders/${orderId}`, { headers });
        
        check(orderDetail, {
          "주문 상세 조회 성공": (r) => r.status === 200,
          "주문 상세 응답시간 < 600ms": (r) => r.timings.duration < 600,
        });

        sleep(1);

        // 주문 상태에 따른 액션 (30% 확률)
        if (orderDetail.status === 200 && Math.random() < 0.3) {
          const orderDetailData = orderDetail.json();
          if (orderDetailData && orderDetailData.data) {
            const orderStatus = orderDetailData.data.status;
            
            // 주문 상태별 시나리오
            if (orderStatus === 'PENDING' || orderStatus === 'CONFIRMED') {
              // 취소 가능한 상태
              let cancelOrder = http.post(`${BASE_URL}/api/v1/orders/${orderId}/cancel`, {}, { headers });
              
              check(cancelOrder, {
                "주문 취소 성공": (r) => r.status === 200 || r.status === 400,
              });
            } else if (orderStatus === 'READY') {
              // 픽업 준비 완료 상태
              let readyOrder = http.post(`${BASE_URL}/api/v1/orders/${orderId}/ready`, {}, { headers });
              
              check(readyOrder, {
                "주문 준비 확인": (r) => r.status === 200 || r.status === 400,
              });
            } else if (orderStatus === 'COMPLETED') {
              // 완료된 주문
              let doneOrder = http.post(`${BASE_URL}/api/v1/orders/${orderId}/done`, {}, { headers });
              
              check(doneOrder, {
                "주문 완료 확인": (r) => r.status === 200 || r.status === 400,
              });
            }
          }
        }
      }
    } catch (e) {
      // JSON 파싱 실패 시 시뮬레이션 주문 ID 사용
      const orderId = Math.floor(Math.random() * 100) + 1;
      let orderDetail = http.get(`${BASE_URL}/api/v1/orders/${orderId}`, { headers });
      
      check(orderDetail, {
        "주문 상세 조회 (시뮬레이션)": (r) => r.status === 200 || r.status === 404,
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