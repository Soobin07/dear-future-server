package com.dearfuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DearfutureApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입테스트() throws Exception {

        String request = """
                {
                  "email":"test@test.com",
                  "password":"1234",
                  "nickname":"test"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isCreated());
    }
    
    @Test
    void 이메일중복테스트() throws Exception {

        String request = """
                {
                  "email":"duplicate@test.com",
                  "password":"1234",
                  "nickname":"user1"
                }
                """;

        // 첫 번째 가입
        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isCreated());

        // 두 번째 가입 (같은 이메일)
        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isConflict());
    }
    
    @Test
    void 닉네임중복테스트() throws Exception {

        String firstRequest = """
                {
                  "email":"nickname1@test.com",
                  "password":"1234",
                  "nickname":"sameNickname"
                }
                """;

        String secondRequest = """
                {
                  "email":"nickname2@test.com",
                  "password":"1234",
                  "nickname":"sameNickname"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest)
        ).andExpect(status().isConflict());
    }
    
    @Test
    void 로그인성공테스트() throws Exception {

        String signupRequest = """
                {
                  "email":"login@test.com",
                  "password":"1234",
                  "nickname":"loginUser"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest)
        ).andExpect(status().isCreated());

        String loginRequest = """
                {
                  "email":"login@test.com",
                  "password":"1234"
                }
                """;

//        mockMvc.perform(
//                post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loginRequest)
//        ).andExpect(status().isOk());
        
        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists());
    }
    
    @Test
    void 로그인실패테스트_비밀번호틀림() throws Exception {

        String signupRequest = """
                {
                  "email":"loginfail@test.com",
                  "password":"1234",
                  "nickname":"loginFailUser"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest)
        ).andExpect(status().isCreated());

        String loginRequest = """
                {
                  "email":"loginfail@test.com",
                  "password":"wrongPassword"
                }
                """;

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(status().isUnauthorized());
    }
    
    @Test
    void 내정보조회_토큰없음_실패() throws Exception {

        mockMvc.perform(
                get("/api/auth/me")
        ).andExpect(status().isUnauthorized());
    }
    
    @Test
    void 내정보조회_토큰있음_성공() throws Exception {

        String signupRequest = """
                {
                  "email":"me@test.com",
                  "password":"1234",
                  "nickname":"meUser"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest)
        ).andExpect(status().isCreated());

        String loginRequest = """
                {
                  "email":"me@test.com",
                  "password":"1234"
                }
                """;

        String response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andReturn()
        .getResponse()
        .getContentAsString();

        String accessToken = objectMapper
                .readTree(response)
                .get("accessToken")
                .asText();

        mockMvc.perform(
                get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken)
        ).andExpect(status().isOk());
    }
    
    @Test
    void 캡슐생성_토큰없음_실패() throws Exception {

        String request = """
                {
                  "title":"미래의 나에게",
                  "content":"잘 살고 있니?",
                  "openAt":"2026-12-31T00:00:00"
                }
                """;

        mockMvc.perform(
                post("/api/capsules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isUnauthorized());
    }
    
    @Test
    void 캡슐생성_토큰있음_성공() throws Exception {

        String signupRequest = """
                {
                  "email":"capsule@test.com",
                  "password":"1234",
                  "nickname":"capsuleUser"
                }
                """;

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest)
        ).andExpect(status().isCreated());

        String loginRequest = """
                {
                  "email":"capsule@test.com",
                  "password":"1234"
                }
                """;

        String loginResponse = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andReturn()
        .getResponse()
        .getContentAsString();

        String accessToken = objectMapper
                .readTree(loginResponse)
                .get("accessToken")
                .asText();

        String capsuleRequest = """
                {
                  "title":"미래의 나에게",
                  "content":"잘 살고 있니?",
                  "openAt":"2026-12-31T00:00:00"
                }
                """;

        mockMvc.perform(
                post("/api/capsules")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(capsuleRequest)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value("미래의 나에게"))
        .andExpect(jsonPath("$.content").value("잘 살고 있니?"))
        .andExpect(jsonPath("$.openAt").value("2026-12-31T00:00:00"));
    }
}