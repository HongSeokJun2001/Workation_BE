package com.kh.workation.crew.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.workation.auth.model.service.AuthService;
import com.kh.workation.common.model.vo.PageInfo;
import com.kh.workation.common.template.Pagination;
import com.kh.workation.crew.model.dto.CrewResponse;
import com.kh.workation.crew.model.service.CrewService;
import com.kh.workation.crew.model.vo.Crew;
import com.kh.workation.crew.model.vo.CrewMemberHist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
public class CrewController {

    @Autowired
    private CrewService crewService;

    @Autowired
    private AuthService authService;

    private String getToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        return authService.isValidToken(token) ? token : null;
    }

    private boolean isSuperAdmin(String token) {
        return token != null && authService.isSuperAdminToken(token);
    }

    private boolean isCrewLeader(String token, Crew crew) {
        if (token == null || crew == null) {
            return false;
        }

        String loginId = authService.getLoginId(token);
        if (loginId == null || crew.getEmployee() == null) {
            return false;
        }

        return loginId.equals(crew.getEmployee().getLoginId());
    }

    @Operation(summary = "게시글 목록 조회 (페이징)", description = "페이지 번호(cpage)에 해당하는 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "list": [{}, {}, {}],
                              "pi": {
                                "listCount": 42,
                                "currentPage": 1,
                                "pageLimit": 5,
                                "boardLimit": 5,
                                "maxPage": 9,
                                "startPage": 1,
                                "endPage": 5
                              }
                            }
                            """)))
    @GetMapping("/crews")
    public ResponseEntity<HashMap<String, Object>> selectCrewList(
            @RequestParam(value = "cpage", defaultValue = "1") int currentPage,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        int boardLimit = 5;
        int pageLimit = 5;
        Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);

        Page<Crew> page = crewService.selectCrewList(pageable);
        List<Crew> list = page.getContent();
        long listCount = page.getTotalElements();

        PageInfo pi = Pagination.getPageInfo((int) listCount, currentPage, pageLimit, boardLimit);

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("list", list);
        hm.put("pi", pi);

        return ResponseEntity.status(HttpStatus.OK).body(hm);
    }

    @GetMapping("/crews/{crewId}")
    public ResponseEntity<Crew> selectCrew(@PathVariable int crewId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Crew c = crewService.selectCrew(crewId);
        return ResponseEntity.status(HttpStatus.OK).body(c);
    }

    @GetMapping("/crews/search")
    public ResponseEntity<HashMap<String, Object>> searchCrewList(
            @RequestParam(value = "cpage", defaultValue = "1") int currentPage,
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (getToken(authHeader) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        int boardLimit = 5;
        int pageLimit = 5;
        Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);

        Page<Crew> page = crewService.searchCrewList(keyword, pageable);
        List<Crew> list = page.getContent();
        long searchCount = page.getTotalElements();

        PageInfo pi = Pagination.getPageInfo((int) searchCount, currentPage, pageLimit, boardLimit);

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("pi", pi);
        hm.put("list", list);

        return ResponseEntity.status(HttpStatus.OK).body(hm);
    }

    @Operation(summary = "크루 모집 글 작성", description = "크루 모집 글을 작성합니다. JWT 토큰에서 작성자 정보를 추출하므로 로그인이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "body로 success/fail 응답")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/crews")
    public ResponseEntity<String> insertCrew(@RequestBody Crew c,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        if (c == null) {
            return ResponseEntity.badRequest().body("fail");
        }

        Crew result = crewService.insertCrew(c);
        return ResponseEntity.ok(result != null ? "success" : "fail");
    }

    @PutMapping("/crews/{crewId}")
    public ResponseEntity<String> updateCrew(@PathVariable("crewId") int crewId,
            @RequestBody Crew c,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        Crew existingCrew = crewService.selectCrew(crewId);
        if (existingCrew == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("fail");
        }

        if (!isSuperAdmin(token) && !isCrewLeader(token, existingCrew)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        if (c == null) {
            return ResponseEntity.badRequest().body("fail");
        }

        c.setCrewId(crewId);
        if (c.getEmployee() == null) {
            c.setEmployee(existingCrew.getEmployee());
        }

        Crew updateCr = crewService.updateCrew(c);
        return ResponseEntity.ok(updateCr != null ? "success" : "fail");
    }

    @DeleteMapping("/crews/{crewId}")
    public ResponseEntity<String> deleteCrew(@PathVariable("crewId") int crewId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        Crew existingCrew = crewService.selectCrew(crewId);
        if (existingCrew == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("fail");
        }

        if (!isSuperAdmin(token) && !isCrewLeader(token, existingCrew)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        int result = crewService.deleteCrew(crewId);
        return ResponseEntity.ok(result > 0 ? "success" : "fail");
    }

    @PostMapping("/crews/{crewId}/join")
    public ResponseEntity<String> joinCrew(@PathVariable int crewId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        String loginId = authService.getLoginId(token);
        CrewMemberHist result = crewService.joinCrew(crewId, loginId);

        return ResponseEntity.ok(result != null ? "success" : "fail");
    }

    @GetMapping("/crews/mylist")
    public ResponseEntity<List<CrewMemberHist>> selectMyCrewList(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String loginId = authService.getLoginId(token);
        return ResponseEntity.ok(crewService.selectMyCrewList(loginId));
    }

    @DeleteMapping("/crews/{crewId}/join")
    public ResponseEntity<String> leaveCrew(@PathVariable int crewId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = getToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
        }

        if (!authService.isEmployeeToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("fail");
        }

        String loginId = authService.getLoginId(token);
        int result = crewService.leaveCrew(crewId, loginId);

        return ResponseEntity.ok(result > 0 ? "success" : "fail");
    }

    @GetMapping("/crews/leader")
    public ResponseEntity<List<CrewResponse>> getMyLeaderCrews(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = getToken(authHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String loginId = authService.getLoginId(token);
        List<Crew> crewList = crewService.getLeaderCrews(loginId);

        List<CrewResponse> responseList = crewList.stream()
                .map(CrewResponse::new)
                .toList();

        return ResponseEntity.ok(responseList);
    }
}
