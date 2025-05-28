[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/InnerCircle-ICD3/o2o-BE)

# EatnGo

## EatnGo 용어 정리

### 매장(Store) 관련 용어

|         변수명         |   한글명   | 설명                                                  |
|:-------------------:|:-------:|:----------------------------------------------------|
|        `id`         |  매장 ID  | 매장의 고유 ID                                          |
|   `storeOwnerId`    |  점주 ID  | 해당 매장을 소유한 점주계정 ID (매장:계정 1:1)                     |
|       `name`        |   매장명   | 매장의 이름                                            |
|    `description`    |  매장 설명  | 매장에 대한 설명                                         |
|      `address`      |   주소    | 매장의 주소 정보                                        |
|  `businessNumber`   |  사업자번호  | 매장의 사업자등록 번호                                      |
|   `contactNumber`   |   연락처   | 매장 또는 점주의 전화번호                                    |
|     `imageUrl`      | 대표 이미지  | 매장의 대표 이미지 URL (카드뷰에 표시)                          |
|   `businessHours`   |  영업시간   | 매장의 영업시간 정보                                       |
|      `status`       |  매장 상태  | 매장의 운영 상태 (PENDING, OPEN, CLOSED)                  |
|    `pickUpInfo`     |  픽업 정보  | 픽업 관련 정보                                          |
| `storeCategoryInfo` | 매장 카테고리 | 매장의 카테고리 정보      

### 주소(Address) 관련 용어

|      변수명       |  한글명   | 설명                                                  |
|:--------------:|:------:|:----------------------------------------------------|
| `roadAddress`  | 도로명 주소 | 매장의 도로명 주소                                        |
| `legalAddress` | 법정동 주소 | 매장의 법정동 주소                                        |
| `adminAddress` | 행정동 주소 | 매장의 행정동 주소                                        |
|  `coordinate`  |   좌표   | 매장의 위도, 경도 정보                                     |


### 매장 카테고리(StoreCategoryInfo) 관련 용어

|       변수명       |   한글명   | 설명                                                  |
|:---------------:|:-------:|:----------------------------------------------------|
| `storeCategory` | 매장 카테고리 | 매장의 분류 카테고리 (예: 빵, 카페, 분식 등)                     |
| `foodCategory`  | 음식 카테고리 | 매장의 음식 카테고리 (예: 햄버거, 소금빵, 모카빵 등)                 |

### 매장 구독(StoreSubscription) 관련 용어

|    변수명    |  한글명   | 설명                                                   |
|:---------:|:------:|:-----------------------------------------------------|
|   `id`    | 구독 ID  | 구독 정보의 고유 ID                                         |
| `userId`  | 사용자 ID | 구독한 사용자의 계정 ID                                       |
| `storeId` | 매장 ID  | 구독된 매장의 매장 ID                                        |

### 매장 관련 Value Object 상세 정보

#### `StoreNameVO`
- `value`: `String` - 매장명 (1~50자)

#### `DescriptionVO`
- `value`: `String` - 매장 설명 (최대 500자)

#### `RoadAddressVO`
- `fullAddress`: `String` - 전체 도로명 주소
- `zoneNo`: `String` - 우편번호

#### `LegalAddress`
- `fullAddress`: `String?` - 전체 지번 주소

#### `AdminAddress`
- `fullAddress`: `String?` - 전체 행정동 주소

#### `CoordinateVO`
- `latitude`: `Double` - 위도 (-90.0 ~ 90.0)
- `longitude`: `Double` - 경도 (-180.0 ~ 180.0)

#### `BusinessNumberVO`
- `value`: `String` - 사업자등록번호 (10자리 숫자)

#### `ContactNumberVO`
- `value`: `String` - 전화번호 (예: 02-123-4567 형식)

#### `ImageUrlVO`
- `value`: `String` - 이미지 URL (http/https로 시작)

#### `BusinessHourVO`
- `dayOfWeek`: `DayOfWeek` - 요일 (MONDAY ~ SUNDAY)
- `openTime`: `LocalTime` - 오픈 시간 (HH:mm 형식)
- `closeTime`: `LocalTime` - 종료 시간 (HH:mm 형식, openTime보다 이후여야 함)

#### `PickUpInfoVO`
- `pickupDay`: `StoreEnum.PickupDay` - 픽업 가능 요일 (TODAY, TOMORROW)
- `pickupStartTime`: `LocalTime` - 픽업 시작 시간 (HH:mm 형식)
- `pickupEndTime`: `LocalTime` - 픽업 종료 시간 (HH:mm 형식, startTime보다 이후여야 함)

#### `ReviewInfoVO`
- `ratingAverage`: `Double` - 평균 별점 (0.0 ~ 5.0)
- `ratingCount`: `Int` - 리뷰 개수

#### `StoreCategoryVO`
- `value`: `String` - 매장 카테고리 값 (1~10자)

#### `FoodCategoryVO`
- `value`: `String` - 음식 카테고리 값
# 주문

## 도메인

### 주문(Order)

| 변수명          | 한글명 | 설명 |
|--------------| --- | --- |
| id           | 식별자 | 주문 식별자 |
| orderNumber  | 주문번호 | 주문 번호 ( unique 한 식별자, TsId 로 만들어짐 ) |
| orderItems   | 주문상품 목록 | 주문상품 목록 |
| customerId   | 손님Id | 손님 식별자 |
| storeOwnerId | 상점Id | 상점 식별자 |
| status       | 주문상태 | - CREATED - CONFIRMED- CANCELED- DONE |

### 주문상품(OrderItem)

| 변수명 | 한글명 | 설명 |
| --- | --- | --- |
| id | 식별자 | 주문상품 식별자 |
| productId | 상품Id | 상품 식별자 ( 이 때문에 상품Id는 softDelete 하는 것이 좋음) |
| productName | 상품명 | 주문했을 당시의 상품 명을 기록 |
| quantity | 개수 | 주문한 상품 개수 |
| price | 가격 | 주문했을 당시의 상품 가격과 상품 개수를 곱한 값 |

### 주문 상태 내역 (OrderStatusHistory)
| 변수명       | 한글명  | 설명                 |
|-----------|------|--------------------|
| id        | 식별자  | 주문 상태 내역 식별자       |
| orderId   | 주문Id | 주문 식별자             |
| status    | 주문상태 | 이 내역이 기록될 당시 주문 내역 |
| userType  | 유저타입 | 유저타입(상점, 유저)       |
| createdAt | 생성시각 | 해당 내역이 생성된 시각      |

## 이벤트
### 주문 이벤트(OrderEvent)

주문 생성 이벤트 (CreateOrderEvent)
- 상품 (재고 차감)
- 상점 (신규 주문 생성 알람)

주문 수락 이벤트 (ConfirmOrderEvent)
- 유저 (주문 수락 알람) 

주문 취소 이벤트 (CancelOrderEvent)
- 상품 (재고 복원)

주문 완료 이벤트 (DoneOrderEvent)
- 없음

## 흐름

``` mermaid
sequenceDiagram
    participant User
    participant Server
    participant Store

    User->>Server: 주문 요청 (상품 정보(가격, 개수 등)) 

    Server->>Server: 상품 정보 유효성 검증
    Server->>Server: 재고 차감 시도
    alt 재고 부족
        Server-->>User: 주문 실패 응답
    else 재고 충분
        Server->>Server: 주문 생성(CREATED)
        Server-->>User: 주문 생성(CREATED) 완료
    end

    alt 유저 주문 취소
        User->>Server: 주문 취소(CANCELED) 요청
        Server-->>User: 주문 취소(CANCELED) 완료
    end

    alt 상점 주문 취소
        Store->>Server: 주문 취소(CANCELED) 요청
        Server-->>Store: 주문 취소(CANCELED) 완료
    end

    Store->>Server: 주문 수락(CONFIRMED)

    alt 주문 수락 이후
        User->>Server: 주문 취소(CANCELED) 시도
        Server-->>User: 주문 취소(CANCELED) 불가
    end

    User->>Server: 상품 수령후 주문 완료(DONE)

```

## Search 관련 용어 정리

|    용어    |     Variable      | 설명                                                   |
|:--------:|:-----------------:|:-----------------------------------------------------|
|    검색    |  `searchFilter`   | 사용자가 입력한 검색어 및 필터 정보                                 |
|  검색할 가게  |   `searchStore`   | MongoDB에 저장된 가게 정보 객체                                |
|  지도 검색   | `searchStoreMap`  | Redis에 캐싱되는 지도 기반 가게 정보                              |
| 위치(위경도)  |   `coordinate`    | 사용자/매장의 위치 정보 (위도, 경도)                               |
|    위도    |    `latitude`     | 위도                                                   |
|    경도    |    `longitude`    | 경도                                                   |
|  검색 쿼리   |   `searchQuery`   | 위치와 필터 등을 포함한 검색 요청 객체                               |
|    분류    |    `category`     | 음식 종류 분류 (예: 한식, 중식 등)                               |
|   영업시간   |  `businessHours`  | 가게의 운영 시간 정보                                         |
|   오픈시간   |    `openTime`     | 가게 오픈 시간                                             |
|   종료시간   |    `closeTime`    | 가게 영업 종료 시간                                          |
|  도로명주소   |   `roadAddress`   | 가게의 도로명 주소 정보                                        |
|  가게 상태   |   `storeStatus`   | 가게의 운영 상태 (예: 영업 중, 준비 중, 휴무 등)                      |
|  박스 좌표   |       `box`       | 지도에서 검색 시 사용하는 영역 단위 (0.005 기준 사각형)                  |
|  좌상단 좌표  |     `topLeft`     | `Box` 내에서 좌측 상단 위경도                                  |
|  우하단 좌표  |   `bottomRight`   | `Box` 내에서 우측 하단 위경도                                  |
| 자동완성 키워드 | `searchRecommend` | 검색어 자동완성에 사용되는 추천 키워드 리스트                            |
|  가게 이름   |    `storeName`    | 가게의 이름                                               |
|  가게 이미지  |   `storeImage`    | S3에 저장될 매장의 대표 이미지                                   |
|  가게 ID   |     `storeId`     | MongoDB에서 가게를 식별하는 고유 ID                             |
|  거리 정보   |   `distanceKm`    | 사용자의 현재 위치와 가게 간의 거리 정보 (km 단위)                      |
|  재고 수량   |      `stock`      | 가게의 재고 수량 정보                                         |
|  픽업 시간   |   `pickupTime`    | 사용자가 검색 필터에서 사용하는 픽업 가능 시간 (예: 11:30분에 픽업 가능한 가게 검색) |


### 상품(Product) 관련 용어

| 한글명        | 영문명             | 설명                                       |
|------------|-----------------|------------------------------------------|
| 상품         | product         | 매장에 등록하는 상품의 단위                          |
| 큰 사이즈 잇고백  | largeEatNGoBag  | 상품의 한 종류 (상품 중 큰 크기의 잇고백)                |
| 중간 사이즈 잇고백 | mediumEatNGoBag | 상품의 한 종류 (상품 중 중간 크기의 잇고백)               |
| 작은 사이즈 잇고백 | smallEatNGoBag  | 상품의 한 종류 (상품 중 작은 크기의  잇고백)              |
| 상품 이름      | name            | 상품의 이름                                   |
| 상품 이미지 주소  | imageUrl        | 상품 이미지의 저장 주소(url)                       |
| 상품 상태      | productStatus   | 상품의 판매 여부를 나타내는 상태(판매중, 판매 중지 상태, 품절 상태) |
| 상품 구성 요소   | foodTypes       | 상품을 이루는 구성 요소                            |
| 음식         | food            | 상품 구성 요소를 이루는 요소                         |
| 음식 이름      | foodName        | 음식의 이름                                   |

### 상품 가격(Product Price) 관련 용어

| 한글명    | 영문명           | 설명                     |
|--------|---------------|------------------------|
| 상품 정상가 | originalPrice | 상품의 초기 가격              |
| 상품 할인가 | finalPrice    | 상품 정상가에 할인율이 적용된 최종 가격 |
| 할인율    | discountRate  | 상품 정상가에 적용될 할인율        |

### 상품 재고(Product Inventory) 관련 용어

| 한글명    | 영문명      | 설명                    |
|--------|----------|-----------------------|
| 상품 수량  | quantity | 점주가 수동으로 설정한 판매 가능 수량 |
| 상품 재고량 | stock    | 현재 실시간 남은 재고          |
