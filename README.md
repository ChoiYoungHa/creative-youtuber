## 유튜브가 어려운 사람들을 위한 유튜브 시작 가이드 웹 애플리케이션
- https://creativeyoutuber.store
<br>

<img width="1384" alt="creative-youtuber" src="https://github.com/ChoiYoungHa/Portfolio/assets/64997345/6ff9a893-fa23-4709-80a3-45eb69eca00e">
<br>

유튜브를 하다보면 설명자료를 찾는 것도 시간이 많이들고, 조회수가 잘 나온 영상은 왜 그런지 분석할 필요가 있습니다. Creative Youtuber 서비스는 대본을 넣어주면 GPT Assistant가 관련 키워드를 추출해서 대본에 알맞는 영상들을 찾아줍니다. 또한 내가 만들 영상의 주제의 키워드를 입력하면 구독자 대비 조회수가 잘 나온 특이한 영상들을 찾아줍니다. 이를 통해 유튜브 크리에이터들의 시간을 절약해줍니다. 더불어 게시판 기능이 있어서 노하우를 공유할 수 있습니다.

## 1. 기술스택



### Backend

- gradle project
- spring boot 3.x
- java 17
- mysql 8.x
- spring data jpa
- thymleaf

### Infra

- AWS
    - EC2
    - RDS
    - Route53
- Docker
- Jenkins CI/CD
- Nginx

### ETC

- Youtube API
- Open AI API
- IntelliJ
- Pycharm
- Notion

## 2. 시스템 아키텍처



![프로젝트 아키텍처](https://github.com/ChoiYoungHa/Portfolio/assets/64997345/a2f896ff-0e36-4ce7-b624-d87aa3304c35)

## 3. API 문서
https://www.notion.so/API-467ec61273c841918440c3f9ce76c146?pvs=4

## 4. 모듈구조



**모델 계층의 의존 관계 흐름**

- application : xxxController, xxxService
    - 독립적으로 실행 가능한 어플리케이션 모듈
    - 하위에서 설계 했던 모듈들을 조립하여 실행 시킨다
    - 사실상 여기에서는 설계한 모든 모듈을 의존하여 실행한다
- domain : xxxDomain, xxxRepository
    - 시스템의 중심 도메인을 다루는 모듈
    - jpa Entity, (Entity) Repository가 직접적으로 연결되는 모듈
- ETC
    - 공통으로 쓰이는 것들을 모아둔 모듈
    - 공통 응답, 공통 에러 헨들러, 로그 설정 등을 정의함

## 5. 모니터링 시스템 추가
- 데이터 파이프라인 구축
  - Spring Boot log -> Kafka -> Logstash -> OpenSearch
  - 로그 발생 -> 실시간 로그 수집 -> 실시간 데이터 변환 -> 대시보드 시각화
- Spring WebFlux
  - WebClient를 이용한 비동기 및 논블로킹 방식의 HTTP 요청
  - 외부 API에 비동기 요청 후 응답은 논블로킹 방식으로 처리 동시성 및 성능 최적화

- 시스템 아키텍처
  - <img width="782" alt="springboot monitoring" src="https://github.com/ChoiYoungHa/creative-youtuber/assets/64997345/429444c6-330a-4992-ae25-5699a61caab9">
