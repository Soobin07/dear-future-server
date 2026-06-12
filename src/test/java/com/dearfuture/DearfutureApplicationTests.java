package com.dearfuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void 회원가입테스트() throws Exception {

		String request = """
				{
				  "email":"test@test.com",
				  "password":"1234",
				  "nickname":"test"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(request))
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
		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isCreated());

		// 두 번째 가입 (같은 이메일)
		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isConflict());
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

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(firstRequest))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(secondRequest))
				.andExpect(status().isConflict());
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

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

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

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists());
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

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"loginfail@test.com",
				  "password":"wrongPassword"
				}
				""";

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void 내정보조회_토큰없음_실패() throws Exception {

		mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
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

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"me@test.com",
				  "password":"1234"
				}
				""";

		String response = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn().getResponse()
				.getContentAsString();

		String accessToken = objectMapper.readTree(response).get("accessToken").asText();

		mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk());
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

		mockMvc.perform(post("/api/capsules").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isUnauthorized());
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

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsule@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn().getResponse()
				.getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		String capsuleRequest = """
				{
				  "title":"미래의 나에게",
				  "content":"잘 살고 있니?",
				  "openAt":"2026-12-31T00:00:00"
				}
				""";

		mockMvc.perform(post("/api/capsules").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(capsuleRequest)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.title").value("미래의 나에게"))
				.andExpect(jsonPath("$.content").value("잘 살고 있니?"))
				.andExpect(jsonPath("$.openAt").value("2026-12-31T00:00:00"));
	}

	@Test
	void 캡슐목록조회_토큰없음_실패() throws Exception {

		mockMvc.perform(get("/api/capsules")).andExpect(status().isUnauthorized());
	}

	@Test
	void 캡슐목록조회_토큰있음_성공() throws Exception {

		String signupRequest = """
				{
				  "email":"capsulelist@test.com",
				  "password":"1234",
				  "nickname":"capsuleListUser"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsulelist@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		String capsuleRequest = """
				{
				  "title":"목록 조회용 캡슐",
				  "content":"목록에 나와야 함",
				  "openAt":"2026-12-31T00:00:00"
				}
				""";

		mockMvc.perform(post("/api/capsules").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(capsuleRequest)).andExpect(status().isCreated());

		mockMvc.perform(get("/api/capsules").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].title").value("목록 조회용 캡슐"))
				.andExpect(jsonPath("$[0].openAt").value("2026-12-31T00:00:00"));
	}

	@Test
	void 캡슐단건조회_토큰없음_실패() throws Exception {

		mockMvc.perform(get("/api/capsules/1")).andExpect(status().isUnauthorized());
	}

	@Test
	void 캡슐단건조회_본인캡슐_성공() throws Exception {

		String signupRequest = """
				{
				  "email":"capsuledetail@test.com",
				  "password":"1234",
				  "nickname":"capsuleDetailUser"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsuledetail@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		String capsuleRequest = """
				{
				  "title":"단건 조회용 캡슐",
				  "content":"단건 조회 내용",
				  "openAt":"2025-12-31T00:00:00"
				}
				""";

		String capsuleResponse = mockMvc
				.perform(post("/api/capsules").header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON).content(capsuleRequest))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		System.out.println("capsuleResponse = " + capsuleResponse);

		Long capsuleId = objectMapper.readTree(capsuleResponse).get("id").asLong();

		System.out.println("capsuleId = " + capsuleId);
		System.out.println("GET 실행");

		try {
			mockMvc.perform(get("/api/capsules/" + capsuleId).header("Authorization", "Bearer " + accessToken))
					.andDo(print()).andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	void 캡슐단건조회_존재하지않음_실패() throws Exception {

		String signupRequest = """
				{
				  "email":"capsulenotfound@test.com",
				  "password":"1234",
				  "nickname":"capsuleNotFoundUser"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsulenotfound@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		mockMvc.perform(get("/api/capsules/999999").header("Authorization", "Bearer " + accessToken)).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void 캡슐수정_토큰없음_실패() throws Exception {

		String updateRequest = """
				{
				  "title":"수정된 제목",
				  "content":"수정된 내용",
				  "openAt":"2027-01-01T00:00:00"
				}
				""";

		mockMvc.perform(put("/api/capsules/1").contentType(MediaType.APPLICATION_JSON).content(updateRequest))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void 캡슐수정_본인캡슐_성공() throws Exception {

		String signupRequest = """
				{
				  "email":"capsuleupdate@test.com",
				  "password":"1234",
				  "nickname":"capsuleUpdateUser"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsuleupdate@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		String createRequest = """
				{
				  "title":"원래 제목",
				  "content":"원래 내용",
				  "openAt":"2026-12-31T00:00:00"
				}
				""";

		String createResponse = mockMvc
				.perform(post("/api/capsules").header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON).content(createRequest))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		Long capsuleId = objectMapper.readTree(createResponse).get("id").asLong();

		String updateRequest = """
				{
				  "title":"수정된 제목",
				  "content":"수정된 내용",
				  "openAt":"2027-01-01T00:00:00"
				}
				""";

		mockMvc.perform(put("/api/capsules/" + capsuleId).header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(updateRequest)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(capsuleId)).andExpect(jsonPath("$.title").value("수정된 제목"))
				.andExpect(jsonPath("$.content").value("수정된 내용"))
				.andExpect(jsonPath("$.openAt").value("2027-01-01T00:00:00"))
				.andExpect(jsonPath("$.updatedAt").exists());
	}

	@Test
	void 캡슐수정_존재하지않음_실패() throws Exception {

		String signupRequest = """
				{
				  "email":"capsuleupdatenotfound@test.com",
				  "password":"1234",
				  "nickname":"capsuleUpdateNotFoundUser"
				}
				""";

		mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
				.andExpect(status().isCreated());

		String loginRequest = """
				{
				  "email":"capsuleupdatenotfound@test.com",
				  "password":"1234"
				}
				""";

		String loginResponse = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		String accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();

		String updateRequest = """
				{
				  "title":"수정된 제목",
				  "content":"수정된 내용",
				  "openAt":"2027-01-01T00:00:00"
				}
				""";

		mockMvc.perform(put("/api/capsules/999999").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(updateRequest)).andExpect(status().isNotFound());
	}
	
	@Test
	void 캡슐삭제_토큰없음_실패() throws Exception {

	    mockMvc.perform(
	            delete("/api/capsules/1")
	    ).andExpect(status().isUnauthorized());
	}
	
	@Test
	void 캡슐삭제_본인캡슐_성공() throws Exception {

	    String signupRequest = """
	            {
	              "email":"capsuledelete@test.com",
	              "password":"1234",
	              "nickname":"capsuleDeleteUser"
	            }
	            """;

	    mockMvc.perform(post("/api/users/signup")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(signupRequest))
	            .andExpect(status().isCreated());

	    String loginRequest = """
	            {
	              "email":"capsuledelete@test.com",
	              "password":"1234"
	            }
	            """;

	    String loginResponse = mockMvc.perform(post("/api/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginRequest))
	            .andExpect(status().isOk())
	            .andReturn()
	            .getResponse()
	            .getContentAsString();

	    String accessToken = objectMapper
	            .readTree(loginResponse)
	            .get("accessToken")
	            .asText();

	    String createRequest = """
	            {
	              "title":"삭제할 캡슐",
	              "content":"삭제용 내용",
	              "openAt":"2026-12-31T00:00:00"
	            }
	            """;

	    String createResponse = mockMvc.perform(post("/api/capsules")
	            .header("Authorization", "Bearer " + accessToken)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(createRequest))
	            .andExpect(status().isCreated())
	            .andReturn()
	            .getResponse()
	            .getContentAsString();

	    Long capsuleId = objectMapper
	            .readTree(createResponse)
	            .get("id")
	            .asLong();

	    mockMvc.perform(delete("/api/capsules/" + capsuleId)
	            .header("Authorization", "Bearer " + accessToken))
	            .andExpect(status().isNoContent());

	    mockMvc.perform(get("/api/capsules/" + capsuleId)
	            .header("Authorization", "Bearer " + accessToken))
	            .andExpect(status().isNotFound());
	}
	
	@Test
	void 캡슐삭제_존재하지않음_실패() throws Exception {

	    String signupRequest = """
	            {
	              "email":"capsuledeletenotfound@test.com",
	              "password":"1234",
	              "nickname":"capsuleDeleteNotFoundUser"
	            }
	            """;

	    mockMvc.perform(post("/api/users/signup")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(signupRequest))
	            .andExpect(status().isCreated());

	    String loginRequest = """
	            {
	              "email":"capsuledeletenotfound@test.com",
	              "password":"1234"
	            }
	            """;

	    String loginResponse = mockMvc.perform(post("/api/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginRequest))
	            .andExpect(status().isOk())
	            .andReturn()
	            .getResponse()
	            .getContentAsString();

	    String accessToken = objectMapper
	            .readTree(loginResponse)
	            .get("accessToken")
	            .asText();

	    mockMvc.perform(delete("/api/capsules/999999")
	            .header("Authorization", "Bearer " + accessToken))
	            .andExpect(status().isNotFound());
	}
	
	@Test
	void 캡슐단건조회_아직열수없음_실패() throws Exception {

	    String signupRequest = """
	            {
	              "email":"capsulelocked@test.com",
	              "password":"1234",
	              "nickname":"capsuleLockedUser"
	            }
	            """;

	    mockMvc.perform(post("/api/users/signup")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(signupRequest))
	            .andExpect(status().isCreated());

	    String loginRequest = """
	            {
	              "email":"capsulelocked@test.com",
	              "password":"1234"
	            }
	            """;

	    String loginResponse = mockMvc.perform(post("/api/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginRequest))
	            .andExpect(status().isOk())
	            .andReturn()
	            .getResponse()
	            .getContentAsString();

	    String accessToken = objectMapper
	            .readTree(loginResponse)
	            .get("accessToken")
	            .asText();

	    String createRequest = """
	            {
	              "title":"아직 열 수 없는 캡슐",
	              "content":"미래에만 볼 수 있는 내용",
	              "openAt":"2026-12-31T00:00:00"
	            }
	            """;

	    String createResponse = mockMvc.perform(post("/api/capsules")
	            .header("Authorization", "Bearer " + accessToken)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(createRequest))
	            .andExpect(status().isCreated())
	            .andReturn()
	            .getResponse()
	            .getContentAsString();

	    Long capsuleId = objectMapper
	            .readTree(createResponse)
	            .get("id")
	            .asLong();

	    mockMvc.perform(get("/api/capsules/" + capsuleId)
	            .header("Authorization", "Bearer " + accessToken))
	            .andExpect(status().isForbidden());
	}
}