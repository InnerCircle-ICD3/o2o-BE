# MongoDB 인덱스 생성 및 관리
> 생성한 인덱스 관리용

## SearchStore
### MongoDB 인덱스
```js
db.SearchStore.createIndex({ location: "2dsphere" })
```
### Atlas Search 인덱스
TODO: store category, food category 인덱스 analyzer 개선
- ex) "치킨 떡볶이" 검색 시 어느 범위까지 허용할지(food category에 "치킨"과 "떡볶이"가 모두 포함된 경우일지, 아니면 둘 중 하나라도 포함된 경우 보여줄지)
- custom analyzer를 사용하여 "치킨"과 "떡볶이"를 모두 포함하는 경우에만 검색되도록 설정
```json
{
  "mappings": {
    "dynamic": false, // true로 설정하면 동적으로 필드가 추가됨 -> false로 설정하면 명시한 필드에만 인덱싱을 적용
    "fields": {
      "storeName": {
        "type": "string",
        "analyzer": "lucene.nori"
      },
      "category": {
        "type": "string",
        "analyzer": "lucene.keyword"
      },
      "open": {
        "type": "boolean"
      },
      "openTime": {
        "type": "date"
      },
      "closeTime": {
        "type": "date"
      },
      "location": {
        "type": "geo"
      }
    }
  }
}
```

#### nori vs standard Analyzer 비교
|       항목        | standard 분석기      | nori 분석기 (한국어 전용)                   |
|:---------------:|:------------------|:------------------------------------|
|      대상 언어      | 영어 중심, 유럽 언어 전반   | 한국어 특화 형태소 분석기                      |
|    토큰 분리 방식     | 띄어쓰기 기반, 구두점 제거 등 | 형태소 기반 분절 (어간 추출 포함)                |
| ex) "치킨을 좋아한다"  | ["치킨을", "좋아한다"]   | ["치킨", "좋아", "합니다"] (분석기 설정에 따라 다름) |
|      사용 대상      | 영어 검색, 국제적 문서     | 한국어 문서/검색어                          |

analyzer
- 전체 텍스트 처리 과정. 자르고 다듬고 걸러줌...
- tokenizer: 단어를 자르는 역할

analyzer vs searchAnalyzer
- analyzer: 인덱스 저장할 때 사용
- searchAnalyzer: 사용자 검색어를 처리할 때 사용

atlas tokenizer 설정
- decompoundMode: compound, mixed, none
  - compound : 분해된 토큰만 사용 (ex: "한국전력공사" -> "한국", "전력", "공사")
  - mixed : 분해된 토큰과 원본 토큰 모두 사용 (ex: "한국전력공사" -> "한국전력공사", "한국", "전력", "공사")
  - none : 원본 토큰만 사용 (ex: "한국전력공사" -> "한국전력공사")
  - 검색 유연성 높이려면 mixed, 정확도 높이려면 compound
- user_dictionary: 사용자 사전. 직접 정의해서 고정된 품사로 인식하도록 함
  - 사용예시 : `"user_dictionary": "치킨,명사\n먹태깡,명사"`
    - user_dictionary 있으면 -> "먹태깡"
    - user_dictionary 없으면 -> "먹", "태", "깡"
- stopwords: 불용어. 검색에서 제외(조사, 접속사, 추임새 등)
  - `"stopwords": ["그리고", "또한", "은", "는", "이", "가"]`
