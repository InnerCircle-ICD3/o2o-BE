[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/InnerCircle-ICD3/o2o-BE)

# EatnGo

## EatnGo 용어 정리

|    용어    |         Class         |     Variable      | 설명                                  |
|:--------:|:---------------------:|:-----------------:|:------------------------------------|
|    검색    |    `SearchFilter`     |  `searchFilter`   | 사용자가 입력한 검색어 및 필터 정보                |
|  검색할 가게  |     `SearchStore`     |   `searchStore`   | MongoDB에 저장된 가게 정보 객체               |
|  지도 검색   |   `SearchStoreMap`    | `searchStoreMap`  | Redis에 캐싱되는 지도 기반 가게 정보             |
| 위치(위경도)  |      `Location`       |    `location`     | 사용자의 현재 위치 (위도, 경도)                 |
|    위도    |           -           |       `lat`       | 위도                                  |
|    경도    |           -           |       `lng`       | 경도                                  |
|  검색 쿼리   | `SearchStoreQueryDto` |   `searchQuery`   | 위치와 필터 등을 포함한 검색 요청 객체              |
|    분류    |      `Category`       |    `category`     | 음식 종류 분류 (예: 한식, 중식 등)              |
|   오픈시간   |    `BusinessHours`    |  `businessHours`  | 가게의 운영 시간 정보                        |
|  도로명주소   |     `RoadAddress`     |   `roadAddress`   | 가게의 도로명 주소 정보                       |
|  가게 상태   |     `StoreStatus`     |   `storeStatus`   | 가게의 운영 상태 (예: 영업 중, 준비 중, 휴무 등)     |
|  박스 좌표   |         `Box`         |       `box`       | 지도에서 검색 시 사용하는 영역 단위 (0.005 기준 사각형) |
|  좌상단 좌표  |      `Location`       |     `topLeft`     | `Box` 내에서 좌측 상단 위경도                 |
|  우하단 좌표  |      `Location`       |   `bottomRight`   | `Box` 내에서 우측 하단 위경도                 |
| 자동완성 키워드 |   `SearchRecommend`   | `searchRecommend` | 검색어 자동완성에 사용되는 추천 키워드 리스트           |