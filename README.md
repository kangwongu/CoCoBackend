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
- Gradle 7.4.1
- Spring Data JPA
- Spring Security
- Java JWT

**Database**
-  AWS RDS (MySQL 8.0.28)

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
- 회원가입 시, 이메일/닉네임 중복확인 기능
- 로그인, 로그아웃 기능
- 관리자 기능 (게시글 / 댓글 강제 삭제 기능)
- 게시글 CURD
- 게시글 필터 기능 (모집중/모집완료, 최신순, 조회순, 댓글순)
- 게시글 검색 기능
- 프로필 페이지에서 본인이 작성한 게시글 / 댓글 조회 기능

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
<summary>6-2. 검색 기능</summary>
<div markdown="1">

<h3>선정한 이유</h3>

- 사용자 피드백 중, 게시글을 검색기능이 없어서 아쉽다는 피드백을 받았고, 이를 반영하기 위해 만든 기능입니다.
- 사용자 피드백을 받기 전에는 크게 필요성을 실감하지 못했었습니다.  
하지만, 사용해주신 분들이 불편함을 알려주셨고, 이를 통해 개발은 사용자 관점에서 바라봐야 한다는 것을 알게 된 기능이라 선정하였습니다.

<h4>사용자 피드백 내용</h4>

![검색1](https://user-images.githubusercontent.com/59812251/183784904-ef3b0e9b-d8ea-40ab-880d-2a000dc4b8b3.png)
![검색기능2](https://user-images.githubusercontent.com/59812251/183784913-aa0387d4-0c6a-494c-8930-3795cc038622.png)

<br>

<h3>흐름</h3>

![댓글순 정렬](https://user-images.githubusercontent.com/59812251/183607255-12a174d4-ff67-463d-aa43-eda2d7b720c5.png)

📌 [PostController 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/controller/PostController.java#L66-L76)

📌 [PostService 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/service/PostService.java#L266-L338)

📌 [PostRepository 코드확인](https://github.com/kangwongu/CoCoBackend/blob/77dcb6b55af6b6b02587e03919dfde0bc77a3f49/src/main/java/com/igocst/coco/repository/PostRepository.java#L17)
</div>
</details>
<details>
<summary>6-3. 게시글 필터 기능 (댓글 많은 순)</summary>
<div markdown="1">

<h3>선정한 이유</h3>

- 사용자 피드백 중, 게시글을 정렬하는 필터 기능이 있었으면 좋겠다는 피드백을 받았고, 이를 반영하기 위한 만든 기능입니다.  
필터는 총 4가지로, 모집중/모집완료, 최신순, 조회순, 댓글순으로 구성되어 있고, 그 중 댓글순을 선정한 이유는 아래와 같습니다.
- 댓글수가 많은 순서대로 게시글을 정렬해야 하는 부분을 어떻게 해결해야 하는지 고민하다가, 정렬 알고리즘인 버블 정렬을 활용하여 문제를 해결했습니다.  
좋은 해결방안임은 확신할 수 없지만, 이전에 배웠던 정렬 알고리즘을 활용하여 문제를 해결할 수 있었던 점이 기억에 남아 선정하게 되었습니다.

<h4>사용자 피드백 내용</h4>

![필터기능1](https://user-images.githubusercontent.com/59812251/183784955-c40a4f2b-dc25-406a-b0cb-c7f0a3905614.png)

<br>

<h3>흐름</h3>

![댓글순 정렬](https://user-images.githubusercontent.com/59812251/183607255-12a174d4-ff67-463d-aa43-eda2d7b720c5.png)

📌 [PostController 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/controller/PostController.java#L101-L106)

📌 [PostService 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/service/PostService.java#L439-L462)

📌 [PostRepository 코드확인](https://github.com/kangwongu/CoCoBackend/blob/main/src/main/java/com/igocst/coco/repository/PostRepository.java#L14)
</div>
</details>

<br>

## 7. 트러블 슈팅

### 게시글 조회 수 중복 증가 

:bookmark: [블로그에 정리했던 내용](https://velog.io/@kwg527/Spring-%EC%A1%B0%ED%9A%8C%EC%88%98-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A4%91%EB%B3%B5-%EB%B0%A9%EC%A7%80)

- 고객 피드백 내용

![조회수](https://user-images.githubusercontent.com/59812251/183630172-34f5eced-6a9c-49e0-9d4b-21d482755a7b.png)
![조회수2](https://user-images.githubusercontent.com/59812251/183784346-9af824f2-fd4b-4acb-b2d9-e22b65720fd8.png)


- 문제
  - 특정 게시글에 접근했을 때 조회수가 증가하는데, 계속 동일한 게시글에 접근해도 조회수가 계속 증가하는 문제가 있었습니다.

- 문제 해결
  - 방문한 게시글 번호를 쿠키에 담아, 해당 게시글에 다시 접근해도 조회수가 증가하지 않도록 문제를 해결하였습니다.
  - 쿠키의 만료시간을 1일로 설정하였고, 동일 IP에서 1일동안 동일한 게시글에 접근해도 조회 수가 계속 올라가지 않도록 했습니다.

<br>

<details>
<summary><b>:bulb: 기존 방식</b></summary>
<div markdown="1">

<br>

기존에 조회수 증가 로직은 아래와 같았습니다.

| PostService.java
``` java
@Transactional
public ResponseEntity<PostReadResponseDto> readPost(Long postId, MemberDetails memberDetails) {
    ...
    // 조회 수 증가
    updateHits(postId);
    ...
}

// 조회수 증가 로직
@Transactional
public int updateHits(Long id) {
    return postRepository.updateHits(id);
}
```

| PostRepository.java
``` java
public interface PostRepository extends JpaRepository<Post, Long> {
    ...
    @Modifying
    @Query("update Post p set p.hits = p.hits + 1 where p.id = :id")
    int updateHits(Long id);
}
```

- 특정 게시글 ID의 hits(조회 수)를 증가시킵니다.
  - **특정 게시글 ID에 접근할 때마다 hits(조회 수)가 증가하는 문제가 발생합니다**
</div>
</details>

<br>

<details>
<summary><b>:bulb: 개선한 방식</b></summary>
<div markdown="1">

<br>

이렇게 조회 수가 중복으로 증가되는 문제를 해결하기 위해 메소드를 추가했습니다.

| PostService.java
``` java
@Transactional
public ResponseEntity<PostReadResponseDto> readPost(Long postId, MemberDetails memberDetails, HttpServletRequest request, HttpServletResponse response) {
    // 조회 수 중복 방지
    updateHits(postId, request, response);
}

// 조회 수 중복 방지
private void updateHits(Long postId, HttpServletRequest request, HttpServletResponse response) {
    Cookie oldCookie = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("postView")) {
                oldCookie = cookie;
            }
        }
    }

    if (oldCookie != null) {
        if (!oldCookie.getValue().contains("["+ postId.toString() +"]")) {
            increaseHits(postId);  // postId에 해당하는 게시글의 조회수 증가
            oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
            oldCookie.setPath("/");
            oldCookie.setDomain("cocoding.xyz");
            oldCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(oldCookie);
        }
    } else {
        increaseHits(postId);   // postId에 해당하는 게시글의 조회수 증가
        Cookie newCookie = new Cookie("postView", "[" + postId + "]");
        newCookie.setPath("/");
        newCookie.setDomain("cocoding.xyz");
        newCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(newCookie);
    }
}
```

- 클라이언트가 게시글에 접근하면 postView(쿠키)에 게시글 번호(id값)를 담습니다.
- postView를 사용하기 위해 'oldCookie'변수에 담아서 사용했습니다. (이후로는 oldCookie로 통일하겠습니다!)
  - 특정 게시글에 접근했을 때, oldCookie에 해당 게시글 id가 없다면 조회 수를 1 증가시키고 oldCookie에 해당 게시글 id를 추가했습니다.
    - 이 시점부터 이 게시글에 여러 번 접근해도, 조회수가 증가하지 않습니다. (최초 접근시만 조회 수가 증가됩니다)
  - 만약 oldCookie에 값이 없다는 것은 어느 게시글에도 접근한 적이 없으므로, 접근한 게시글의 조회수를 1 증가시키고 게시글 id를 oldCookie에 추가합니다.
    - 이 시점부터 이 게시글에 여러 번 접근해도, 조회수가 증가하지 않습니다. (최초 접근시만 조회 수가 증가됩니다)

📌 [변경 코드 확인](https://github.com/BreedingMe/CoCoBackend/pull/176/files)

</div>
</details>

<br>

### 배포 환경에서 쿠키 동작 문제

:bookmark: [블로그에 정리했던 내용](https://velog.io/@kwg527/Spring-AWS-%EB%B0%B0%ED%8F%AC-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EC%BF%A0%ED%82%A4-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

- 문제
  - 로컬 환경에서 테스트를 했을 때는 쿠키가 잘 동작하는데, 배포 환경에서는 쿠키가 클라이언트에 저장이 안되어서 조회 수 중복 방지 기능이 동작하지 않는 문제가 발생했습니다.

- 문제 해결
  - 로컬 환경에서는 쿠키의 도메인을 설정해주지 않아도 되는데, 배포 환경에서는 쿠키 도메인을 설정해주어야 한다는 것을 알게 되었고, 이를 적용해 해결하였습니다.

<br>

<details>
<summary><b>:bulb: 기존 방식</b></summary>
<div markdown="1">

<br>

기존에 조회수 증가 로직은 아래와 같았습니다.
로컬에서 테스트중이었고, oldCookie에 도메인을 설정해 주지 않은 상황입니다.

| PostService.java
``` java
// 조회 수 중복 방지
private void updateHits(Long postId, HttpServletRequest request, HttpServletResponse response) {
    Cookie oldCookie = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("postView")) {
                oldCookie = cookie;
            }
        }
    }

    if (oldCookie != null) {
        if (!oldCookie.getValue().contains("["+ postId.toString() +"]")) {
            increaseHits(postId);
            oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
            oldCookie.setPath("/");
            oldCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(oldCookie);
        }
    } else {
        increaseHits(postId);
        Cookie newCookie = new Cookie("postView", "[" + postId + "]");
        newCookie.setPath("/");
        newCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(newCookie);
    }
}
```
- 로컬에서는 정상적으로 동작하지만, 배포 환경에서 테스트하니 쿠키가 동작하지 않는 문제가 발생하였습니다.

</div>
</details>

<br>

<details>
<summary><b>:bulb: 개선한 방식</b></summary>
<div markdown="1">

<br>

배포 환경에서 쿠키의 도메인을 설정해주지 않으면 내부 전용 쿠키가 되고 로컬에서만 적용된다는 문제점을 알게 되었습니다.  
이를 해결하기 위해 쿠키의 도메인을 설정해 해당 도메인으로 쿠키를 전달하도록 개선하였습니다.

| PostService.java
``` java
// 조회 수 중복 방지
private void updateHits(Long postId, HttpServletRequest request, HttpServletResponse response) {
    Cookie oldCookie = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("postView")) {
                oldCookie = cookie;
            }
        }
    }

    if (oldCookie != null) {
        if (!oldCookie.getValue().contains("["+ postId.toString() +"]")) {
            increaseHits(postId);
            oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
            oldCookie.setPath("/");
            oldCookie.setDomain("cocoding.xyz");
            oldCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(oldCookie);
        }
    } else {
        increaseHits(postId);
        Cookie newCookie = new Cookie("postView", "[" + postId + "]");
        newCookie.setPath("/");
        newCookie.setDomain("cocoding.xyz");
        newCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(newCookie);
    }
}
```

📌 [변경 코드 확인](https://github.com/BreedingMe/CoCoBackend/pull/181/files#diff-48a80a6196151da0e8bc16802ba19988153fa471ed6f20d3fa61f0f094d867c4)

</div>
</details>

<br>

## 8. 기타 개선한 부분

📌 [Optional 사용](https://github.com/BreedingMe/CoCoBackend/wiki/Optional-%EC%82%AC%EC%9A%A9)  
📌 [예외 처리](https://github.com/BreedingMe/CoCoBackend/wiki/%EC%98%88%EC%99%B8-%EC%B2%98%EB%A6%AC)  
📌 [@Setter 사용하지 않기](https://github.com/BreedingMe/CoCoBackend/wiki/@Setter-%EC%82%AC%EC%9A%A9%ED%95%98%EC%A7%80-%EC%95%8A%EA%B8%B0)

<br>

## 9. 회고 / 느낀 점
> [최종 프로젝트 회고](https://velog.io/@kwg527/%ED%9A%8C%EA%B3%A0-%EB%82%B4%EB%B0%B0%EC%BA%A0-%EC%B5%9C%EC%A2%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%9A%8C%EA%B3%A0)

<br>

## 10. 기타
> [팀 Github repository](https://github.com/BreedingMe/CoCoBackend)
