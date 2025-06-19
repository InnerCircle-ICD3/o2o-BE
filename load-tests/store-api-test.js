import { check, sleep } from "k6";
import http from "k6/http";

export let options = {
  stages: [
    { duration: "1m", target: 20 },   // 1분 동안 20명까지 증가
    { duration: "3m", target: 100 },  // 3분 동안 100명까지 증가
    { duration: "1m", target: 0 },    // 1분 동안 0명까지 감소
  ],
};

const BASE_URL = "https://customer.eatngo.org";

export default function () {
  // 매장 목록 조회
  let storeListRes = http.get(`${BASE_URL}/api/v1/search/store?size=10&latitude=37.569755&longitude=127.049845`);
  
  check(storeListRes, {
    "store list status is 200": (r) => r.status === 200,
    "store list response time < 1s": (r) => r.timings.duration < 1000,
  });

  // 매장 상세 조회 (로그인 없이)
  let storeDetailRes = http.get(`${BASE_URL}/api/v1/stores/1`);
  
  check(storeDetailRes, {
    "store detail status is 200": (r) => r.status === 200,
    "store detail response time < 500ms": (r) => r.timings.duration < 500,
  });

  // 검색 API
  let searchRes = http.get(`${BASE_URL}/api/v1/search/store?size=10&latitude=37.569755&longitude=127.04984&keyword=카페`);
  
  check(searchRes, {
    "search status is 200": (r) => r.status === 200,
    "search response time < 2s": (r) => r.timings.duration < 2000,
  });

  sleep(1); // 1초 대기
} 