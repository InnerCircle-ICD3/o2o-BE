import { check, sleep } from "k6";
import http from "k6/http";
import { randomItem } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// 점진적 증가 패턴 - 첫 화면 접속 시나리오
export let options = {
  stages: [
    { duration: "30s", target: 20 },   // 30초간 20명까지 점진 증가 (앱 오픈)
    { duration: "2m", target: 100 },   // 2분간 100명까지 증가 (점심시간 러시)
    { duration: "3m", target: 200 },   // 3분간 200명까지 증가 (피크 시간)
    { duration: "2m", target: 300 },   // 2분간 300명까지 급증 (스파이크)
    { duration: "1m", target: 100 },   // 1분간 100명으로 감소
    { duration: "1m", target: 0 },     // 1분간 0명까지 감소
  ],
};

const BASE_URL = "http://localhost:8080";

// 서울 주요 지역 좌표 (실제 사용자 분포 고려)
const LOCATIONS = [
  { lat: 37.566500, lng: 126.978011, name: "명동" },
  { lat: 37.572859, lng: 126.971428, name: "종로" },
  { lat: 37.517235, lng: 127.047325, name: "강남" },
  { lat: 37.540705, lng: 126.956203, name: "용산" },
  { lat: 37.579617, lng: 126.977041, name: "홍대" },
  { lat: 37.494942, lng: 127.027618, name: "서초" },
];

// 실제 검색어 패턴
const SEARCH_KEYWORDS = [
  "치킨", "피자", "커피", "햄버거", "족발", "중국집", 
  "일식", "한식", "양식", "분식", "디저트", "술집"
];

const STORE_CATEGORIES = [
  "KOREAN", "CHINESE", "JAPANESE", "WESTERN", "CHICKEN", 
  "PIZZA", "CAFE", "DESSERT", "FASTFOOD"
];

export default function () {
  const location = randomItem(LOCATIONS);
  const userScenario = Math.random();
  
  // 80% - 첫 화면 접속 시나리오
  if (userScenario < 0.8) {
    firstScreenScenario(location);
  }
  // 15% - 검색 시나리오  
  else if (userScenario < 0.95) {
    searchScenario(location);
  }
  // 5% - 지도 뷰 시나리오
  else {
    mapViewScenario(location);
  }
  
  sleep(Math.random() * 3 + 1); // 1-4초 랜덤 대기
}

function firstScreenScenario(location) {
  // 1. 첫 화면 매장 리스트 로드
  let initialLoad = http.get(
    `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20`
  );
  
  check(initialLoad, {
    "첫 화면 로드 성공": (r) => r.status === 200,
    "첫 화면 응답시간 < 1s": (r) => r.timings.duration < 1000,
  });

  sleep(2); // 사용자가 화면을 보는 시간

  // 2. 무한스크롤 시뮬레이션 (2-4번 스크롤)
  let scrollCount = Math.floor(Math.random() * 3) + 2;
  let lastId = null;
  
  for (let i = 0; i < scrollCount; i++) {
    if (initialLoad.json && initialLoad.json()?.data?.paginationToken) {
      lastId = initialLoad.json().data.paginationToken;
    }
    
    let scrollLoad = http.get(
      `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20${lastId ? `&lastId=${lastId}` : ''}`
    );
    
    check(scrollLoad, {
      [`무한스크롤 ${i+1} 성공`]: (r) => r.status === 200,
      [`무한스크롤 ${i+1} 응답시간 < 800ms`]: (r) => r.timings.duration < 800,
    });
    
    sleep(1 + Math.random()); // 스크롤 간격
  }

  // 3. Pull to Refresh 시뮬레이션 (30% 확률)
  if (Math.random() < 0.3) {
    sleep(3); // 사용자가 앱을 잠시 사용 후
    
    let refreshLoad = http.get(
      `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&size=20`
    );
    
    check(refreshLoad, {
      "새로고침 성공": (r) => r.status === 200,
      "새로고침 응답시간 < 1s": (r) => r.timings.duration < 1000,
    });
  }
}

function searchScenario(location) {
  const keyword = randomItem(SEARCH_KEYWORDS);
  
  // 1. 검색어 자동완성
  let suggestions = http.get(`${BASE_URL}/api/v1/search/suggestions?keyword=${keyword.substring(0, 1)}`);
  
  check(suggestions, {
    "자동완성 성공": (r) => r.status === 200,
    "자동완성 응답시간 < 300ms": (r) => r.timings.duration < 300,
  });

  sleep(0.5); // 타이핑 시간

  // 2. 실제 검색
  let searchResult = http.get(
    `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&searchText=${keyword}&size=20`
  );
  
  check(searchResult, {
    "검색 성공": (r) => r.status === 200,
    "검색 응답시간 < 1.5s": (r) => r.timings.duration < 1500,
  });

  // 3. 카테고리 필터 적용 (50% 확률)
  if (Math.random() < 0.5) {
    sleep(2);
    const category = randomItem(STORE_CATEGORIES);
    
    let filteredResult = http.get(
      `${BASE_URL}/api/v1/search/store?latitude=${location.lat}&longitude=${location.lng}&searchText=${keyword}&storeCategory=${category}&size=20`
    );
    
    check(filteredResult, {
      "카테고리 필터 성공": (r) => r.status === 200,
      "카테고리 필터 응답시간 < 1s": (r) => r.timings.duration < 1000,
    });
  }
}

function mapViewScenario(location) {
  // 지도 뷰 매장 포인트 로드
  let mapLoad = http.get(
    `${BASE_URL}/api/v1/search/store/map?latitude=${location.lat}&longitude=${location.lng}`
  );
  
  check(mapLoad, {
    "지도 로드 성공": (r) => r.status === 200,
    "지도 응답시간 < 800ms": (r) => r.timings.duration < 800,
  });

  // 지도 이동 시뮬레이션 (2-3회)
  let moveCount = Math.floor(Math.random() * 2) + 2;
  
  for (let i = 0; i < moveCount; i++) {
    sleep(2); // 지도 이동 시간
    
    // 좌표를 약간씩 이동
    const newLat = location.lat + (Math.random() - 0.5) * 0.01;
    const newLng = location.lng + (Math.random() - 0.5) * 0.01;
    
    let mapMove = http.get(
      `${BASE_URL}/api/v1/search/store/map?latitude=${newLat}&longitude=${newLng}`
    );
    
    check(mapMove, {
      [`지도 이동 ${i+1} 성공`]: (r) => r.status === 200,
      [`지도 이동 ${i+1} 응답시간 < 600ms`]: (r) => r.timings.duration < 600,
    });
  }
} 