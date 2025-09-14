# React 전환 및 프론트엔드 개선 계획

## 1. 개요 (Overview)
기존 Spring Boot + Thymeleaf 구조의 프론트엔드를 React 기반의 SPA(Single Page Application)으로 전환하여, 더 나은 사용자 경험과 향상된 성능, 그리고 유지보수성을 확보하는 것을 목표로 합니다.

## 2. 기술 스택 (Tech Stack)
- **UI Framework**: React
- **Routing**: React Router
- **API Communication**: Axios
- **Styling**: Material-UI (or Bootstrap for React)
- **State Management**: React Context API (초기) / Redux Toolkit (확장 시)
- **Build Tool**: Create React App

## 3. 프로젝트 구조 (Project Structure)
Spring Boot 프로젝트 내에 `frontend` 디렉토리를 생성하여 React 프로젝트를 관리합니다.

```
/
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/ (재사용 가능한 UI 컴포넌트)
│   │   ├── pages/ (각 페이지 컴포넌트)
│   │   ├── services/ (API 연동 로직)
│   │   ├── hooks/ (커스텀 훅)
│   │   ├── styles/ (글로벌 스타일)
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   └── ...
├── src/
│   └── main/
│       └── ... (기존 Spring Boot 소스)
└── ...
```

## 4. 컴포넌트 설계 (Component Design)
- **`SearchPage.js`**: 메인 검색 페이지
- **`PlayerPage.js`**: 플레이어 정보 및 최근 매치 리스트 출력 페이지
- **`MatchPage.js`**: 특정 매치의 상세 정보 페이지
- **`SearchBar.js`**: 재사용 가능한 검색창 컴포넌트
- **`MatchListItem.js`**: 매치 리스트의 개별 아이템 컴포넌트
- **`StatCard.js`**: K/D, 데미지 등 개별 스탯 표시 카드 컴포넌트
- **`LoadingSpinner.js`**: 데이터 로딩 시 표시될 스피너
- **`ErrorMessage.js`**: API 오류 발생 시 보여줄 메시지

## 5. 페이지 및 라우팅 (Pages and Routing)
`React Router`를 사용하여 다음과 같이 라우팅을 구성합니다.
- `/`: `SearchPage`
- `/players/:nickname`: `PlayerPage`
- `/matches/:matchId`: `MatchPage`

## 6. API 연동 (API Integration)
- `src/services/api.js` 파일에서 Axios 인스턴스를 생성하고 모든 API 요청을 중앙에서 관리합니다.
- `searchPlayer(nickname)`, `getMatchDetails(matchId)` 와 같은 함수를 만들어 각 컴포넌트에서 호출하여 사용합니다.

## 7. 상태 관리 (State Management)
- 초기에는 `useState`, `useEffect`와 같은 기본 훅과 `Context API`를 사용하여 간단한 전역 상태(예: 검색된 플레이어 정보)를 관리합니다.
- 애플리케이션이 복잡해지면 `Redux Toolkit`을 도입하여 상태 관리를 고도화합니다.

## 8. 빌드 및 통합 (Build and Integration)
1. React 프로젝트(`frontend` 디렉토리)에서 `npm run build`를 실행하여 정적 빌드 파일을 생성합니다.
2. 생성된 빌드 결과물(`build` 폴더 내의 파일들)을 Spring Boot의 `src/main/resources/static` 디렉토리로 복사합니다.
3. Spring Boot의 `PubgController`는 API 요청만 처리하도록 수정하고, 모든 프론트엔드 라우팅 요청은 React의 `index.html`을 반환하도록 설정하여 클라이언트 사이드 라우팅을 지원합니다.

## 9. 개발 단계 (Development Steps)
1. **(1단계)** `create-react-app`으로 `frontend` 프로젝트 생성 및 기본 설정
2. **(2단계)** `react-router-dom`, `axios`, `material-ui` 등 필요 라이브러리 설치
3. **(3단계)** 기본 프로젝트 구조(폴더 및 파일) 생성
4. **(4단계)** `SearchPage` 및 `SearchBar` 컴포넌트 구현
5. **(5단계)** API 서비스 로직 구현 (플레이어 검색)
6. **(6단계)** `PlayerPage` 구현 및 API 연동
7. **(7단계)** `MatchPage` 구현 및 API 연동
8. **(8단계)** 전체적인 스타일링 및 사용자 경험(UX) 개선
9. **(9단계)** Spring Boot와 빌드 통합 및 배포 설정
