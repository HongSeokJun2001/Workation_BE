# Workation API 명세서

작성 기준일: 2026-08-24  
작성 기준: `Workation_BE/src/main/java/com/kh/workation`에 현재 구현되어 활성화된 Controller 매핑

## 공통 사항

- Base URL: 서버 실행 환경 기준. 예: `http://localhost:8080`
- Content-Type: 요청 body가 있는 API는 `application/json`
- 인증 방식: `Authorization: Bearer {accessToken}`
- Spring Security 설정은 전체 요청을 허용하지만, `InterceptorConfig`에서 경로별 JWT 검증을 수행한다.

### 인증 규칙

| 경로 | 인증 | 허용 토큰 |
| --- | --- | --- |
| `/auth/**` | 불필요 | 없음 |
| `/public/**` | 불필요 | 없음 |
| `/admin/super/**` | 필요 | `role=SUPER` 관리자 JWT |
| `/admin/company/**` | 필요 | `role=COMPANY` 관리자 JWT |
| `/lobby/**` | 필요 | 유효한 로그인 JWT |

### 공통 에러

| 상황 | HTTP Status | 응답 body |
| --- | --- | --- |
| 인증 헤더 없음 또는 잘못된 JWT | `401 Unauthorized` | 없음 |
| 권한이 맞지 않는 관리자 요청 | `403 Forbidden` | 없음 또는 문자열 메시지 |
| 요청값 검증 실패/비즈니스 오류 | `400 Bad Request` | 문자열 메시지 |

## API 목록

| Method | URI | 인증 | 설명 |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | 불필요 | 관리자/직원 로그인 및 JWT 발급 |
| `GET` | `/admin/super/member/list` | SUPER | 최고관리자/본사관리자 목록 조회 |
| `GET` | `/admin/company/member/admin-list` | COMPANY | 같은 회사의 본사관리자 목록 조회 |
| `GET` | `/admin/company/member/employee-list` | COMPANY | 같은 회사의 직원 목록 조회 |
| `GET` | `/admin/super/member/admin/{adminId}` | SUPER | 관리자 계정 상세 조회 |
| `GET` | `/admin/company/member/admin/{adminId}` | COMPANY | 같은 회사의 본사관리자 계정 상세 조회 |
| `GET` | `/admin/company/member/employee/{employeeId}` | COMPANY | 같은 회사의 직원 계정 상세 조회 |
| `PUT` | `/admin/super/member/admin/{adminId}` | SUPER | 관리자 계정 수정 |
| `PUT` | `/admin/company/member/admin/{adminId}` | COMPANY | 같은 회사의 본사관리자 계정 수정 |
| `PUT` | `/admin/company/member/employee/{employeeId}` | COMPANY | 같은 회사의 직원 계정 수정 |
| `GET` | `/public/company/check` | 불필요 | 회사 정보 일치 여부 확인 |
| `GET` | `/public/employee/check-login-id` | 불필요 | 직원 로그인 아이디 사용 가능 여부 확인 |
| `POST` | `/public/employee/signup` | 불필요 | 직원 회원가입 신청 |
| `GET` | `/notices` | 불필요 | 공지사항 목록 조회 |

## Auth API

### 로그인

`POST /auth/login`

로그인 유형에 따라 관리자 또는 직원을 조회하고 JWT access token을 발급한다. 토큰 만료 시간은 현재 구현 기준 1시간이다.

#### Request Body

| 필드 | 타입 | 필수 | 설명 | 예시 |
| --- | --- | --- | --- | --- |
| `loginId` | string | Y | 로그인 아이디 | `admin` |
| `password` | string | Y | 비밀번호 | `test` |
| `loginType` | string | Y | 로그인 유형. `ADMIN`, `EMPLOYEE` | `ADMIN` |

```json
{
  "loginId": "admin",
  "password": "test",
  "loginType": "ADMIN"
}
```

#### Response `200 OK`

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `accessToken` | string | JWT access token |
| `tokenType` | string | 토큰 타입. 현재 `Bearer` |
| `role` | string | 권한. `SUPER`, `COMPANY`, `EMPLOYEE` |

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "role": "SUPER"
}
```

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `401 Unauthorized` | `관리자 아이디 또는 비밀번호가 올바르지 않습니다.` |
| `401 Unauthorized` | `직원 아이디 또는 비밀번호가 올바르지 않습니다.` |
| `401 Unauthorized` | `로그인 유형이 올바르지 않습니다.` |

## Member API

### 최고관리자 및 본사관리자 목록 조회

`GET /admin/super/member/list`

ADMIN 테이블에서 최고관리자와 본사관리자 목록을 조회한다.

#### Authorization

`Authorization: Bearer {SUPER 관리자 accessToken}`

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `status` | string | N | `ALL` | 계정 상태. `ALL`, `ACTIVE`, `LOCKED` |
| `target` | string | N | `ALL` | 조회 대상. `ALL`, `SUPER`, `COMPANY` |

#### Response `200 OK`

`AdminListResponse[]`

```json
[
  {
    "adminId": 1,
    "companyId": null,
    "companyName": null,
    "companyLabel": "-",
    "loginId": "admin",
    "role": "SUPER",
    "status": "ACTIVE"
  }
]
```

### 본사관리자 목록 조회

`GET /admin/company/member/admin-list`

JWT에 포함된 `companyId` 기준으로 같은 회사의 본사관리자 목록을 조회한다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `status` | string | N | `ALL` | 계정 상태. `ALL`, `ACTIVE`, `LOCKED` 등 |

#### Response `200 OK`

`AdminListResponse[]`

```json
[
  {
    "adminId": 2,
    "companyId": 1,
    "companyName": "더미 회사 1",
    "companyLabel": "더미 회사 1(1)",
    "loginId": "companyadmin01",
    "role": "COMPANY",
    "status": "ACTIVE"
  }
]
```

### 직원 목록 조회

`GET /admin/company/member/employee-list`

JWT에 포함된 `companyId` 기준으로 같은 회사의 직원 목록을 조회한다. 현재 구현은 `Employee` 엔티티를 그대로 반환하므로 암호화된 `password` 필드가 응답에 포함될 수 있다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `status` | string | N | `ALL` | 직원 상태. `ALL`, `ACTIVE`, `LOCKED` 등 |

#### Response `200 OK`

`Employee[]`

```json
[
  {
    "employeeId": 1,
    "companyId": 1,
    "loginId": "employee01",
    "password": "$2a$10$exampleEncodedPassword",
    "empNo": 1001,
    "employeeName": "직원 01",
    "phone": "01010000001",
    "email": "employee01@dummy.com",
    "department": "개발팀",
    "position": "사원",
    "workationAvailDays": 10,
    "status": "ACTIVE",
    "hireDate": "2026-08-20",
    "resignDate": null,
    "isProgressed": "N"
  }
]
```

### 최고관리자용 관리자 계정 상세 조회

`GET /admin/super/member/admin/{adminId}`

#### Authorization

`Authorization: Bearer {SUPER 관리자 accessToken}`

#### Path Variables

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `adminId` | number | 관리자 고유 번호 |

#### Response `200 OK`

`AdminDetailResponse`

```json
{
  "companyId": 1,
  "companyName": "더미 회사 1",
  "companyLabel": "더미 회사 1(1)",
  "loginId": "companyadmin01",
  "role": "COMPANY",
  "status": "ACTIVE"
}
```

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `404 Not Found` | `해당 관리자 계정을 찾을 수 없습니다.` |

### 본사관리자용 관리자 계정 상세 조회

`GET /admin/company/member/admin/{adminId}`

JWT의 `companyId`와 대상 관리자 계정의 `companyId`가 같고 대상 권한이 `COMPANY`인 경우만 조회된다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Path Variables

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `adminId` | number | 관리자 고유 번호 |

#### Response `200 OK`

`AdminDetailResponse`

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `403 Forbidden` | `해당 관리자 계정을 찾을 수 없습니다.` |
| `403 Forbidden` | `조회 권한이 없는 관리자 계정입니다.` |

### 본사관리자용 직원 계정 상세 조회

`GET /admin/company/member/employee/{employeeId}`

JWT의 `companyId`와 대상 직원의 `companyId`가 같은 경우만 조회된다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Path Variables

| 이름 | 타입 | 설명 |
| --- | --- | --- |
| `employeeId` | number | 직원 고유 번호 |

#### Response `200 OK`

`EmployeeDetailResponse`

```json
{
  "companyId": 1,
  "companyName": "더미 회사 1",
  "companyLabel": "더미 회사 1(1)",
  "loginId": "employee01",
  "empNo": 1001,
  "employeeName": "직원 01",
  "phone": "01010000001",
  "email": "employee01@dummy.com",
  "department": "개발팀",
  "position": "사원",
  "workationAvailDays": 10,
  "status": "ACTIVE",
  "hireDate": "2026-08-20",
  "resignDate": null,
  "isProgressed": "N"
}
```

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `403 Forbidden` | `해당 직원 계정을 찾을 수 없습니다.` |
| `403 Forbidden` | `조회 권한이 없는 직원 계정입니다.` |

### 최고관리자용 관리자 계정 수정

`PUT /admin/super/member/admin/{adminId}`

#### Authorization

`Authorization: Bearer {SUPER 관리자 accessToken}`

#### Request Body

`AdminUpdateRequest`

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `loginId` | string | Y | 변경할 로그인 아이디 |
| `password` | string | N | 변경할 비밀번호. 미입력/공백이면 기존 비밀번호 유지. 입력 시 8~15자, 특수문자 포함 |
| `status` | string | Y | 계정 상태. 예: `ACTIVE`, `LOCKED` |

```json
{
  "loginId": "companyadmin01",
  "password": "test!1234",
  "status": "ACTIVE"
}
```

#### Response `200 OK`

`AdminDetailResponse`

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `400 Bad Request` | `해당 관리자 계정을 찾을 수 없습니다.` |
| `400 Bad Request` | `비밀번호는 8~15자이며 특수문자를 포함해야 합니다.` |

### 본사관리자용 관리자 계정 수정

`PUT /admin/company/member/admin/{adminId}`

JWT의 `companyId`와 대상 관리자 계정의 `companyId`가 같고 대상 권한이 `COMPANY`인 경우만 수정된다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Request Body

`AdminUpdateRequest`

#### Response `200 OK`

`AdminDetailResponse`

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `400 Bad Request` | `해당 관리자 계정을 찾을 수 없습니다.` |
| `400 Bad Request` | `수정 권한이 없는 관리자 계정입니다.` |
| `400 Bad Request` | `비밀번호는 8~15자이며 특수문자를 포함해야 합니다.` |

### 본사관리자용 직원 계정 수정

`PUT /admin/company/member/employee/{employeeId}`

JWT의 `companyId`와 대상 직원의 `companyId`가 같은 경우만 수정된다.

#### Authorization

`Authorization: Bearer {COMPANY 관리자 accessToken}`

#### Request Body

`EmployeeUpdateRequest`

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `loginId` | string | Y | 직원 로그인 아이디 |
| `password` | string | N | 변경할 비밀번호. 미입력/공백이면 기존 비밀번호 유지. 입력 시 8~15자, 특수문자 포함 |
| `empNo` | number | Y | 사번 |
| `employeeName` | string | Y | 직원 이름 |
| `phone` | string | Y | 전화번호 |
| `email` | string | Y | 이메일 |
| `department` | string | N | 부서 |
| `position` | string | N | 직급 |
| `workationAvailDays` | number | Y | 워케이션 사용 가능 일수 |
| `status` | string | Y | 계정 상태. 예: `ACTIVE`, `LOCKED` |
| `hireDate` | string | Y | 입사일. `yyyy-MM-dd` |
| `resignDate` | string/null | N | 퇴사일. `yyyy-MM-dd` |

```json
{
  "loginId": "employee01",
  "password": "test!1234",
  "empNo": 1001,
  "employeeName": "직원 01",
  "phone": "01010000001",
  "email": "employee01@dummy.com",
  "department": "개발팀",
  "position": "사원",
  "workationAvailDays": 10,
  "status": "ACTIVE",
  "hireDate": "2026-08-20",
  "resignDate": null
}
```

#### Response `200 OK`

`EmployeeDetailResponse`
#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `400 Bad Request` | `해당 직원 계정을 찾을 수 없습니다.` |
| `400 Bad Request` | `수정 권한이 없는 직원 계정입니다.` |
| `400 Bad Request` | `비밀번호는 8~15자이며 특수문자를 포함해야 합니다.` |

## Public Member API

### 회사 정보 확인

`GET /public/company/check`

사업자등록번호와 회사명이 일치하는 회사가 있는지 확인한다.

#### Query Parameters

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `businessNo` | string | Y | 사업자등록번호 |
| `companyName` | string | Y | 회사명 |

#### Response `200 OK`

```json
true
```

### 직원 로그인 아이디 중복 확인

`GET /public/employee/check-login-id`

EMPLOYEE 테이블에서 로그인 아이디 중복 여부를 확인한다. 응답값은 “사용 가능 여부”다.

#### Query Parameters

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `loginId` | string | Y | 확인할 직원 로그인 아이디 |

#### Response `200 OK`

```json
true
```

- `true`: 사용 가능
- `false`: 이미 사용 중

### 직원 회원가입 신청

`POST /public/employee/signup`

회사 정보와 아이디/비밀번호를 검증한 뒤 회원가입 신청 상태의 직원을 생성한다. 생성 시 `status=LOCKED`, `isProgressed=N`, `workationAvailDays=0`으로 저장된다.

#### Request Body

`EmployeeSignupRequest`

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `businessNo` | string | Y | 사업자등록번호 |
| `companyName` | string | Y | 회사명 |
| `loginId` | string | Y | 직원 로그인 아이디 |
| `password` | string | Y | 비밀번호. 8~15자, 특수문자 포함 |
| `empNo` | number | Y | 사번 |
| `employeeName` | string | Y | 직원 이름 |
| `phone` | string | Y | 전화번호 |
| `email` | string | Y | 이메일 |
| `department` | string | N | 부서명 |
| `position` | string | N | 직급 |

```json
{
  "businessNo": "1111111111",
  "companyName": "더미 회사 1",
  "loginId": "employee11",
  "password": "test!1234",
  "empNo": 1011,
  "employeeName": "직원 11",
  "phone": "01010000011",
  "email": "employee11@dummy.com",
  "department": "개발팀",
  "position": "사원"
}
```

#### Response `200 OK`

현재 구현은 생성된 `Employee` 엔티티를 그대로 반환하므로 암호화된 `password` 필드가 응답에 포함될 수 있다.

```json
{
  "employeeId": 11,
  "companyId": 1,
  "loginId": "employee11",
  "password": "$2a$10$exampleEncodedPassword",
  "empNo": 1011,
  "employeeName": "직원 11",
  "phone": "01010000011",
  "email": "employee11@dummy.com",
  "department": "개발팀",
  "position": "사원",
  "workationAvailDays": 0,
  "status": "LOCKED",
  "hireDate": "2026-08-24",
  "resignDate": null,
  "isProgressed": "N"
}
```

#### Error

| Status | 응답 body 예시 |
| --- | --- |
| `400 Bad Request` | `회사 정보가 일치하지 않습니다.` |
| `400 Bad Request` | `이미 사용 중인 아이디입니다.` |
| `400 Bad Request` | `비밀번호는 8~15자이며 특수문자를 포함해야 합니다.` |

## Notice API

### 공지사항 목록 조회

`GET /notices`

공지사항 목록을 조회한다. 별도 인증 인터셉터 대상 경로가 아니므로 현재 구현 기준 인증 없이 호출 가능하다.

#### Response `200 OK`

`Notice[]`

```json
[
  {
    "noticeId": 1,
    "admin": {
      "adminId": 1,
      "companyId": null,
      "loginId": "admin",
      "password": "$2a$10$exampleEncodedPassword",
      "role": "SUPER",
      "status": "ACTIVE"
    },
    "noticeTitle": "공지 제목",
    "noticeContent": "공지 내용",
    "viewCount": 0,
    "status": "Y",
    "createDate": "2026-08-24T10:00:00",
    "updateDate": "2026-08-24T10:00:00"
  }
]
```

## 현재 컨트롤러 기준 미구현 또는 비활성 API

아래 컨트롤러/매핑은 파일은 존재하지만 현재 구현된 활성 API로 보기는 어렵다.

| 위치 | 상태 |
| --- | --- |
| `ApplicationController` | `@RestController`만 있고 매핑 메소드 없음 |
| `ReservationController` | `@RestController`만 있고 매핑 메소드 없음 |
| `FacilityController` | `/api/facilities`, `/api/facilities/{facilityId}` 구현이 클래스 단위로 주석 처리됨 |

## 구현상 주의 사항

- `Employee` 엔티티를 직접 반환하는 API는 암호화된 비밀번호가 응답 JSON에 포함될 수 있다.
- `Notice` 엔티티는 `Admin`을 지연 로딩 관계로 가지고 있어 직렬화 시 환경에 따라 응답 구조 또는 Lazy Loading 문제가 발생할 수 있다.
- `@RequestBody` DTO에 Bean Validation 어노테이션이 없어서 필수값 누락은 컨트롤러 진입 전 자동 검증되지 않는다.
- `PUT` 수정 API는 body에 포함된 값으로 엔티티 필드를 그대로 덮어쓴다. 선택 수정이 필요한 경우 프론트에서 기존 값을 함께 보내야 한다.