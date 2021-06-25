# :fork_and_knife: eat-enjoy
## :rice: 개요
- 사용자가 선택한 레스토랑에 원하는 날짜, 시간으로 빠르고 간편하게 예약을 할 수 있도록 도와주는 서비스 입니다.
- 백엔드 기술에 좀 더 집중하기 위해 클라이언트는 카카오 오븐 프로토타입으로 대체하였습니다.

## :rice: 사용 기술 및 환경
[![java](https://img.shields.io/badge/Java-11-orange)](https://docs.oracle.com/en/java/javase/11/docs/api/index.html) [![framework](https://img.shields.io/badge/Spring%20Boot-2.4.2-brightgreen)](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) [![build tool](https://img.shields.io/badge/Gradle-6.7.1-gray)](https://docs.gradle.org/6.7.1/userguide/userguide.html) [![mybatis](https://img.shields.io/badge/MyBatis-3.5.6-lightgrey)](https://mybatis.org/mybatis-3/ko/index.html) [![mariadb](https://img.shields.io/badge/MariaDB-10.2.12-blue)](https://mariadb.com/kb/en/mariadb-10212-release-notes/) [![redis](https://img.shields.io/badge/Redis-3.0.5-red)](https://github.com/microsoftarchive/redis/blob/win-3.0.504/Redis%20on%20Windows%20Release%20Notes.md) [![checkstyle](https://img.shields.io/badge/codestyle-캠퍼스%20핵데이%20Java%20코딩%20컨벤션-yellow)](https://naver.github.io/hackday-conventions-java/) [![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-blue)](https://www.jenkins.io/)

## :rice: Git Flow
#### :rice_cracker: Branch 전략
<img src="https://woowabros.github.io/img/2017-10-30/git-flow_overall_graph.png" width="500">

- **master(main):** 제품으로 출시될 수 있는 브랜치 입니다.
- **develop:** 다음 출시 버전을 개발하는 브랜치 입니다.
- **feature:** 기능을 개발하는 브랜치 입니다.
- **release:** 이번 출시 버전을 준비하는 브랜치 입니다.
- **hotfix:** 출시 버전에서 발생한 버그를 수정 하는 브랜치 입니다.

&nbsp;&nbsp;:bulb: **참고:** [우린 Git-flow를 사용하고 있어요](https://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html)

#### :rice_cracker: Commit Type
- **기능(feat):** 새로운 기능을 추가한 경우 사용합니다.
- **버그(fix):** 버그를 수정한 경우 사용합니다.
- **리팩토링(refactor):** 코드 리팩토링한 경우 사용합니다.
- **형식(style):** 코드 형식, 정렬, 주석 등의 변경(동작에 영향을 주는 코드 변경 없음)한 경우 사용합니다.
- **테스트(test):** 테스트 추가, 테스트 리팩토링(제품 코드 수정 없음, 테스트 코드에 관련된 모든 변경에 해당)한 경우 사용합니다.
- **문서(docs):** 문서를 수정(제품 코드 수정 없음)한 경우 사용합니다.
- **기타(chore):** 빌드 업무 수정, 패키지 매니저 설정 등 위에 해당되지 않는 모든 변경(제품 코드 수정 없음)일 경우 사용합니다.

## :rice: Wiki
- 해당 프로젝트의 상세 정보는 [Wiki](https://github.com/f-lab-edu/eat-enjoy/wiki)를 통해 확인할 수 있습니다.

## :rice: CI
- Jenkins를 Multibranch Pipeline을 사용하여 Build 자동화를 적용 하였습니다.
- [eat-enjoy Jenkins 주소](http://49.50.165.208:18080/)
