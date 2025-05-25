[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/InnerCircle-ICD3/o2o-BE)

# EatnGo

## EatnGo 용어 정리

|    용어    |         Class         |     Variable      | 설명                                                   |
|:--------:|:---------------------:|:-----------------:|:-----------------------------------------------------|
|    검색    |    `SearchFilter`     |  `searchFilter`   | 사용자가 입력한 검색어 및 필터 정보                                 |
|  검색할 가게  |     `SearchStore`     |   `searchStore`   | MongoDB에 저장된 가게 정보 객체                                |
|  지도 검색   |   `SearchStoreMap`    | `searchStoreMap`  | Redis에 캐싱되는 지도 기반 가게 정보                              |
| 위치(위경도)  |      `Location`       |    `location`     | 사용자의 현재 위치 (위도, 경도)                                  |
|    위도    |           -           |       `lat`       | 위도                                                   |
|    경도    |           -           |       `lng`       | 경도                                                   |
|  검색 쿼리   | `SearchStoreQueryDto` |   `searchQuery`   | 위치와 필터 등을 포함한 검색 요청 객체                               |
|    분류    |      `Category`       |    `category`     | 음식 종류 분류 (예: 한식, 중식 등)                               |
|   영업시간   |    `BusinessHours`    |  `businessHours`  | 가게의 운영 시간 정보                                         |
|   오픈시간   |           -           |    `openTime`     | 가게 오픈 시간                                             |
|   종료시간   |           -           |    `closeTime`    | 가게 영업 종료 시간                                          |
|  도로명주소   |     `RoadAddress`     |   `roadAddress`   | 가게의 도로명 주소 정보                                        |
|  가게 상태   |     `StoreStatus`     |   `storeStatus`   | 가게의 운영 상태 (예: 영업 중, 준비 중, 휴무 등)                      |
|  박스 좌표   |         `Box`         |       `box`       | 지도에서 검색 시 사용하는 영역 단위 (0.005 기준 사각형)                  |
|  좌상단 좌표  |      `Location`       |     `topLeft`     | `Box` 내에서 좌측 상단 위경도                                  |
|  우하단 좌표  |      `Location`       |   `bottomRight`   | `Box` 내에서 우측 하단 위경도                                  |
| 자동완성 키워드 |   `SearchRecommend`   | `searchRecommend` | 검색어 자동완성에 사용되는 추천 키워드 리스트                            |
|  가게 이름   |           -           |    `storeName`    | 가게의 이름                                               |
|  가게 이미지  |           -           |   `storeImage`    | S3에 저장될 매장의 대표 이미지                                   |
|  가게 ID   |           -           |     `storeId`     | MongoDB에서 가게를 식별하는 고유 ID                             |
|  거리 정보   |           -           |   `distanceKm`    | 사용자의 현재 위치와 가게 간의 거리 정보 (km 단위)                      |
|  재고 수량   |           -           |      `stock`      | 가게의 재고 수량 정보                                         |
|  픽업 시간   |           -           |   `pickupTime`    | 사용자가 검색 필터에서 사용하는 픽업 가능 시간 (예: 11:30분에 픽업 가능한 가게 검색) |