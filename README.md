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