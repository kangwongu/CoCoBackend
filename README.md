# CoCo
![CocoBanner2](https://user-images.githubusercontent.com/103922744/180134172-04dda2fd-bebd-48ef-9a1c-77ae638b4979.jpg)
<br>
<br>

## 1. 프로젝트 개요
- 제작기간: 2022.06.24 ~ 2022.07.29
- 참여인원: 4명
- 주제: 사이드 프로젝트 구인 플랫폼
- 기획의도: 원하는 사이드 프로젝트를 기획하고 함께할 인원을 모집할 수 있는 플랫폼

<br>

## 2. 사용기술
**Backend**
- Java 11
- Spring Boot 2.7.1
- Gradle
- Spring Data JPA
- Spring Security
- Java JWT

**Database**
-  AWS RDS (MySQL)

**Infra**
- AWS ELB
- AWS EC2
- AWS S3
- AWS CloudFront

**CI/CD**
- Github Action

**Frontend**
- HTML5
- CSS3
- JavaScript
- Bulma
- JQuery
- Webpack
- Node.js

<br>

## 3. ERD 설계
![수정  CoCo (1)](https://user-images.githubusercontent.com/59812251/183559776-06765c85-f5fe-43e9-bc06-587140dc316a.png)

<br>

## 4. 아키텍쳐 설계
<img width="1549" alt="스크린샷 2022-08-09 오후 2 20 22" src="https://user-images.githubusercontent.com/59812251/183570700-46ce5b35-9676-45bc-839a-6556162d3f66.png">

<br>

## 5. 기여한 기능
#### 담당한 기능: 회원 관련 기능 / 게시글 관련 기능
- 회원가입 기능
- 로그인, 로그아웃 기능
- 게시글 CURD
- 게시글 필터 기능
- 게시글 검색 기능

<br>

## 6. 기억에 남는 기능
<details>
<summary>6-1. JWT를 사용한 로그인 기능</summary>
<div markdown="1">

<h3>선정한 이유</h3>

- 배우지 않은 부분을 맡게 되었고, 열심히 구글링을 통해 해결한 기능입니다.
- refresh Token은 적용하지 못해 반쪽짜리 기능이지만, 배우지 않은 부분도 구글링을 통해 해결 할 수 있다는 생각을 갖게 해준 기능이라 선정했습니다.

<br>

<h3>흐름</h3>

<h4>로그인)</h4>

![JWT로그인1](https://user-images.githubusercontent.com/59812251/183603074-169a74c9-d6f6-418f-b18b-ffcc1dba57df.png)

- 중복된 회원정보가 있는지, 비밀번호가 틀리진 않았는지를 검사하고 통과하면, JWT 토큰을 클라이언트에 발급합니다.


📌 [MemberController 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/controller/MemberController.java#L25-L30)

📌 [MemberService 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/service/MemberService.java#L51-L87)

📌 [JwtTokenProvider 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/security/jwt/JwtTokenProvider.java#L29-L40)

📌 [MemberRepository 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/repository/MemberRepository.java)

<br>
<br>

<h4>토큰 검증)</h4>

![JWT로그인2](https://user-images.githubusercontent.com/59812251/183603045-e9eb7703-62bb-45d7-9a1d-e08dd0949b11.png)

- 시큐리티 클래스에 JWT 필터를 등록했습니다.
- 임의로 열어둔 URL을 제외한 모든 요청에서 JWT 토큰의 유효성을 검증합니다.
  - 토큰이 유효하면, 로그인된 사용자 정보가 Controller로 넘어가고, 이를 사용할 수 있습니다.


📌 [JwtAuthenticationFilter 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/security/filter/JwtAuthenticationFilter.java)

📌 [JwtTokenProvider 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/security/jwt/JwtTokenProvider.java#L42-L96)

📌 [SecurityConfiguration 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/security/SecurityConfiguration.java#L80-L81)

📌 [MemberDetailsService 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/security/MemberDetailsService.java)

</div>
</details>
<details>
<summary>6-2. 게시글 필터 기능 (댓글 많은 순)</summary>
<div markdown="1">

<h3>선정한 이유</h3>

- 사용자 피드백 중, 게시글을 정렬하는 필터 기능이 있었으면 좋겠다는 피드백을 받았고, 이를 반영하기 위한 만든 기능입니다.
- 댓글수가 많은 순서대로 게시글을 정렬해야 하는 부분을 어떻게 해결해야 하는지 고민하다가, 정렬 알고리즘인 버블 정렬을 활용하여 문제를 해결했습니다.  
좋은 해결방안임은 확신할 수 없지만, 이전에 배웠던 정렬 알고리즘을 활용하여 문제를 해결할 수 있었던 점이 기억에 남아 선정하게 되었습니다.

<br>

<h3>흐름</h3>

![댓글순 정렬](https://user-images.githubusercontent.com/59812251/183607255-12a174d4-ff67-463d-aa43-eda2d7b720c5.png)

📌 [PostController 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/controller/PostController.java#L66-L76)

📌 [PostService 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/service/PostService.java#L266-L338)

📌 [PostRepository 코드확인](https://github.com/kangwongu/CoCoBackend/blob/77dcb6b55af6b6b02587e03919dfde0bc77a3f49/src/main/java/com/igocst/coco/repository/PostRepository.java#L17)


</div>
</details>
