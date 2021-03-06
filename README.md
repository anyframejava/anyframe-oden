Anyframe Oden
===
Anyframe Open Deployment ENvironment (이하 ODEN)은 CI (Continuous Integration) 환경을 통해 빌드된 어플리케이션 컴포넌트 및 각종 설정파일, 웹파일 등을 원하는 배포대상서버에 편리하게 배포할 수 있도록 하는 배포관리 툴이다.


### 특징
최근의 일반적인 중대형 개발 프로젝트의 프로세스는 요구정의로부터 시작하여 분석 및 아키텍처정의, 설계, 개발, 이행 등으로 진행하는 것이 최근의 추세이며, 여러 벤더들은 이러한 각 공정 단계에 특화된 다양한 툴을 제공하고 있다.
그러나 분석/아키텍처정의/설계 단계에서 활용할 수 있는 다양한 툴과는 달리, 개발단계 이후 활용할 수 있는 툴의 범위는 다소 부족한 듯 하다. 특히, 개발한 어플리케이션 컴포넌트들을 개발서버 및 테스트서버 혹은 그 너머의 운영서버 등에 배포할 수 있는 전문화된 배포관리 툴에 대한 선택의 폭은 매우 작은 것이 현실이다.

![](README_image/overview_somethingmissing_1.png)

프로젝트의 일반적 생명주기는 요구 분석, 설계 구현, 이행으로 나눠진다. 요구 분석, 설계,구현 단계에서는 다양한 툴을 활용 할 수 있으나 이행 단계의 배포 단계는 활용 할 수 있는 툴이 적다.
이러한 전문적인 배포관리툴의 부재로 인해, 현장 프로젝트에서는 몇가지 어려움에 직면하게 된다.

첫째, 자동화 및 정형화된 배포관리가 이루어지지 않아 프로젝트 진행 및 운영 시 추가적인 리소스가 필요하게 된다.

기존에는 수작업 또는 CI엔진에 의한 복사 등으로 배포를 실시하였는데, 이를 관리하기 위해 QAO (Quality Assurance Officer: 품질관리자) 혹은 SA (Software Architect: 소프트웨어 아키텍트) 등에 의한 배포관리가 전문적으로 이루어져야 했음
배포대상서버가 여러대일 경우, 해당 작업을 단순반복하여 처리해야 하므로 번거로운 작업을 수행해야 함

둘째, 배포 시 고려할 수 있는 다양한 배포 방법에 일일이 대응하기가 어렵게 된다.

전문적인 배포관리 툴이 없다면 전체 배포, 원하는 것만 배포, 변경된 사항만 배포 등 현장에서 요구하는 다양한 배포 방식에 일일이 대응하기 어려움
특히, 개발서버에서는 대개 변경된 사항만 배포되면 족함에도 불구하고, 일일이 비교하는 것이 번거롭기 때문에 전체를 한꺼번에 배포할 경우가 많음
실제로 일일이 비교하여 배포하는 경우라도, 누락되는 것이 있어 결국 배포에 실패하게 되는 경우가 발생함

셋째, 표준화 및 정형화된 프로세스에 기반한 개발 및 이행단계 진행이 어렵게 된다.

프로세스화된 배포 환경의 부재는 매번 배포시마다 업무의 혼란 및 리소스의 낭비 여지를 제공하게 됨
이를 위해 배포 정책 설정, 스냅샷/롤백, 로그분석, 스케쥴링/배치, 워크플로우 적용 등 다양한 기능들이 필요함
Oden은 이러한 어려움을 극복하기 위한 자동화된 배포관리 환경을 제공한다.


ODEN은 다음과 같은 주요 특징을 지닌다.
* ODEN은 Spring, OSGi 등 Java 기술을 활용하고 있으며, 현장 프로젝트의 다양한 상황에 대응하기 위한 개방적이고 유연한 구조를 통해 손쉽게 확장이 가능한 개방형 아키텍처를 지향한다. 더불어 각종 UNIX, Mac OS X, MS Windows 등 다양한 플랫폼 환경을 지원한다.
* 배포 가능한 배포 대상에 대한 일괄 배포 (full-deployment), 배포 가능한 배포 대상 중 원하는 것만 선택적으로 배포 (selective-deployment), 배포 가능한 배포 대상 중 변경된 것만 배포 (incremental-deployment) 등 다양한 형태의 배포 방법을 지원한다.
* 다양한 배포환경에 적용할 수 있으며, 대량/대용량 배포물에 대한 안정적인 배포작업을 실행할 수 있도록 안정적인 성능을 보장한다.
* CLI (Command Line Interface) 및 GUI (Graphical User Interface) 모두에서 사용할 수 있는 편리한 사용환경을 제공한다.

### 3 Minute Guide

#### 설치 및 실행
* Anyframe-Oden-x.x.x-bin.zip 압축해제(설치 경로에 공백이 있으면 안됨)
* Server실행: (core/bin폴더)startup.cmd 실행
* Agent실행: (core/bin폴더)startup-agent.cmd 실행
* Admin실행: (admin폴더)startup.cmd 실행

#### Job추가
* Admin접속: http ://localhost:9880
* 로그인: oden/oden0(기본계정)으로 로그인
* Job추가: Job목록화면에서 우측상단 Job추가 버튼 클릭
* 전환된 화면에서 Job Name입력(e.g. myapp)
* Source의 Directory에는 배포할 파일이 있는 최상위 폴더 경로 입력(e.g. C:/myapp/dist)
* Target에는 파일이 배포될 곳 입력. Name 입력(e.g. myapp0). URL은 localhost:9872 입력. PATH는 파일이 배포될 경로 입력(e.g. C:/tomcat/webapps/myapp)
* 하단의 Save버튼 클릭하여 Job 저장
* Job목록화면에서 방금 추가한 myapp 이름 클릭
* Job편집화면에서 Source >> Mapping >> Auto Mapping 클릭. 값이 안나오면 무시.
* 하단의 Save버튼 클릭하여 변경내용 저장
* Job 목록 화면 우측 상단 job 버튼을 클릭하면 Job 추가 화면이 열리며, Job 이름과 배포 할 대상 파일의 정보와 배포 될 곳의 정보를 입력하고 하단의 Save 버튼을 클릭한다.

![](README_image/Picture 2.png)

#### 배포
* Job목록 화면에서 Action컬럼의 첫번째 아이콘(달리기) 클릭
* 전환된 화면에서 우측 상단의 Preview버튼 클릭
* 배포할 목록 확인하고 배포를 하기위해 맨 아래 Deploy all버튼 클릭
* 배포진행화면이 나옴. 배포 진행 상세 내역은 왼쪽의 Status 메뉴 클릭하여 확인 가능
* 배포할 목록에서 일부만 배포하고 싶은 경우, 제거할 파일의 우측의 X클릭. 맨 아래 Deploy버튼 클릭하여 배포. 이 경우 현재 보여지는 페이지의 파일만 배포됨

![](README_image/Picture 4.png)

#### 결과 확인
* 배포가 완료되면 job목록화면에서 해당 Job의 status클릭
* 배포된 파일 확인

![](README_image/Picture 5_0.png)

#### ant연계
* 위에 추가한 Job(myapp)를 ant로 배포하고자 하는 경우 아래와 같이 ant스크립트에 추가

```html
<target name="deploy">
    <exec executable="{ODEN 설치경로}/core/bin/runc.cmd" dir="{ODEN 설치경로}/core/bin" failonerror="true">
    <arg line-"deploy run myapp"/>
    </exec>
</target>
```

