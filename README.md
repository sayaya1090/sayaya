[![Licensed under MIT](https://img.shields.io/github/license/sayaya1090/sayaya.svg?maxAge=2592000)](https://raw.githubusercontent.com/sayaya1090/sayaya/main/LICENSE)
[![Test status](https://github.com/sayaya1090/sayaya/actions/workflows/deploy-jib.yaml/badge.svg)](https://github.com/sayaya1090/sayaya/actions/workflows/deploy-jib.yaml)
[![Test status](https://github.com/sayaya1090/sayaya/actions/workflows/deploy-static.yaml/badge.svg)](https://github.com/sayaya1090/sayaya/actions/workflows/deploy-static.yaml)


![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![RxJS](https://img.shields.io/badge/rxjs-%23B7178C.svg?style=for-the-badge&logo=reactivex&logoColor=white)
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
![Markdown](https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white)
![Selenium](https://img.shields.io/badge/-selenium-%43B02A?style=for-the-badge&logo=selenium&logoColor=white)



## 프로젝트 개요

간단한 블로그 서비스입니다. 

이것저것 사용하되 전체적으로 통합되어 있는 서비스를 구축하고 운용해봄으로써, 다음과 같은 목적을 달성합니다.
  - 최신 라이브러리를 적용할 수 있는 테스트베드
  - 도전적인 설계를 적용하고 그 가능성을 검증합니다


## 기능
  - 마크다운으로 포스트를 작성합니다. 뷰어를 제공하여 WYSIWYG로 작성할 수 있습니다.
  - 포스트를 작성하고 저장하면, 깃헙에 내용이 백업됩니다.
  - 공개한 포스트는 검색엔진에 노출됩니다.
  - 로그인을 위한 이메일은 vault에 저장되어 보호됩니다.

## 흥미로운 부분
  - OKD의 관리 편의성 확인
  - GWT, Elemental2를 통해 자바 코드로 프론트엔드 작성
    - IoC를 위해 Dagger 라이브러리 사용 필요
    - Rx 라이브러리가 있어야 그럭저럭 현대적인 코드 작성이 가능함
    - 자바니까 lombok 써야 편함
    - 코틀린을 쓰려면 GWT->J2CL로 변경 필요한듯
    - 레퍼런스 부재가 문제
  - web component를 사용한 마이크로 프론트엔드 적용
    - 스크립트, CSS 공유 방안 필요
    - Fontawesome 사용 시 좀 골치아픈 부분이 있음 
    - 재사용 되는 컴포넌트에만 작성하는 것이 좋은듯
    - DOM 트리가 격리됨으로써 퍼포먼스에 긍정적 영향이 있는지 확인 필요
  - CNPG의 퍼포먼스 
    - local 스토리지 사용 시 native에 준하는 성능을 얻을 수 있었음. 
    - 하지만 장기간 운영해본 결과, 파드 다운/마스터 이전 시 안정성이 떨어짐
  - vault 퍼포먼스
    - ceph 스토리지 사용 시 성능이 매우 떨어짐. 데이터 볼륨의 성능이 매우 중요한듯
  - fabric8를 사용한 쿠버네티스 시뮬레이션
    - 쿠버네티스가 알아서 해 주는 부분을 직접 구현해야..
    - 이 케이스에 대해서는 매우 불편함. 다른 활용사례 모색 필요
  - 셀레니움을 사용한 프론트엔드 테스트 코드 작성
    - 코드 커버리지에 포현이 안 되는 문제가 있음
    - 백엔드 코드와 같은 방식으로 일관적인 테스트 코드 작성이 가능
  - 임시 DOM SSR을 활용한 SEO
    - 일단 구글은 의도한대로 검색엔진에 노출되지만, 구글은 검색엔진에 의해 JS코드가 동작하기 때문일수도 있음
  - OAuth2 구현
    - 디폴트로 메모리 세션 사용함. 횡적 확장을 위해서는 Redis 세션으로 변경 필요

## 구성요소
  - activity: 마이크로 프론트엔드 구현을 위한 공통 인터페이스
  - authentication: 인증 기능을 제공하는 라이브러리
  - blog: 공개한 포스트 검색, 상세보기 등 / SSR 
  - card-ui: 포스트 카드 UI 라이브러리
  - charts: ArgoCD를 사용하여 배포하기 위한 헬름차트 모음 
  - entity: DB 테이블 작성/인덱스 관리 등 편의를 위한 JPA 모듈(운영에는 사용하지 않음)
  - login: 로그인/로그아웃 기능 제공
  - login-ui: 로그인 UI
  - marked: marked 라이브러리를 사용하기 위한 인터페이스
  - post: 포스트 작성, 관리 기능 제공
  - post-data: DTO 라이브러리
  - post-ui: 포스트 작성, 관리 UI
  - shell: 서비스 디스커버리를 통한, 현재 활성화 된 서비스 목록 제공
  - shell-ui: UI 진입점(프론트엔드 모듈의 Docking platform) / 메뉴 UI
  - vault: 개인정보 관리를 위한 vault 저장소 인터페이스


