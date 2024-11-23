# daeng-ggu-backend


### 🐶프로젝트 설명
🐾댕꾸 - 댕댕이 꾸미기🐾 </br>
'댕꾸'는 프리미엄 강아지 미용 중계 서비스입니다. </br>
반려견 보호자와 디자이너 사이의 미용 견적 요청, 예약, 결제, 모니터링까지의 전체 중계 서비스를 제공합니다. </br>
</br></br>

### 🛠프로젝트 디렉토리 구조
```
daeng-ggu-backend/
├── .github/
│   └── workflows/
│       └── develop.yml                # GitHub Actions CI/CD 파이프라인 설정 파일 (dev)
├── build.gradle                       # Gradle 빌드 파일
├── gradle/
│   └── wrapper/
├── scripts/                           # 배포 스크립트 디렉토리
│   └── deploy.sh                      # EC2에 배포하는 스크립트
├── src/
│   └── main/
│       └── java/
│       └── resources/
├── appspec.yml                        # CodeDeploy 배포 설정 파일
└── README.md 
``` 
</br></br>

### 📤배포 Flow
<img src="https://github.com/user-attachments/assets/2e451a3e-bb54-416b-88c0-c1785263d509" alt="배포 Flow" width="600"/></br>
</br></br>

### 📝Git 컨벤션
- 브랜치 네이밍
    - [ 접두사/#Jira 티켓번호-작업내용 ]
    - 예시) feature/#88-user-authentication
- 커밋 메시지 포맷
    - [ 타입: 내용 ]
```
======================= 반드시 콜론(:) 을 붙여야 합니다. =========================
Feat:             새로운 기능을 추가
Fix:              버그 수정
Design:           CSS 등 사용자 UI 디자인 변경
!BREAKING CHANGE: 커다란 API 변경의 경우
!HOTFIX:          급하게 치명적인 버그를 고쳐야하는 경우
Style:            코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우
Refactor:         코드 리팩토링
Comment:          필요한 주석 추가 및 변경
Docs:             문서 수정
Test:             빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음
Rename:           파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우
Remove:           파일을 삭제하는 작업만 수행한 경우
```
</br></br>

### ✔브랜치 전략
- 메인 브랜치
    - main(master) : 운영
    - develop : 개발
- 보조 브랜치 - 일정 기간 동안만 유지
    - feature : 새로운 기능 추가
    - release : 신규 배포 준비
    - bugfix : 버그 수정
    - hotfix : 긴급 수정