package springproject.iam.v1.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.constant.Success;
import springproject.iam.v1.exception.GlobalException;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.model.dto.auth.CredentialResponse;
import springproject.iam.v1.model.dto.auth.RegisterRequest;
import springproject.iam.v1.service.auth.JwtAuthService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthControllerTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean JwtAuthService jwtAuthService;

  RegisterRequest registerRequest;
  CredentialResponse credentialResponse;

  @BeforeAll
  void beforeAll() {
    registerRequest = new RegisterRequest("hoalx1", "hoalx1", new HashSet<>(List.of(1L)));
    credentialResponse =
        new CredentialResponse(
            "accessToken", System.currentTimeMillis(), "refreshToken", System.currentTimeMillis());
  }

  @Test
  @Order(1)
  void signUp_givenLegal_whenSignUp_thenSave() throws Exception {
    when(jwtAuthService.signUp(registerRequest)).thenReturn(credentialResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("code").value(Success.SIGN_UP.getCode()))
        .andExpect(MockMvcResultMatchers.jsonPath("message").value(Success.SIGN_UP.getMessage()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("payload.accessToken")
                .value(credentialResponse.getAccessToken()));
  }

  @Test
  @Order(2)
  void signUp_givenBlankPassword_whenSignUp_thenBadRequest() throws Exception {
    registerRequest.setPassword(null);
    registerRequest.setUsername("hoalx1");
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("code")
                .value(GlobalException.ILL_LEGAL_REQUEST_PAYLOAD.getCode()))
        .andExpect(MockMvcResultMatchers.jsonPath("message").value("password can not be blank"))
        .andExpect(MockMvcResultMatchers.jsonPath("payload").doesNotExist());
  }

  @Test
  @Order(3)
  void signUp_givenBlankUsername_whenSignUp_thenBadRequest() throws Exception {
    registerRequest.setPassword("hoalx1");
    registerRequest.setUsername(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("code")
                .value(GlobalException.ILL_LEGAL_REQUEST_PAYLOAD.getCode()))
        .andExpect(MockMvcResultMatchers.jsonPath("message").value("username can not be blank"))
        .andExpect(MockMvcResultMatchers.jsonPath("payload").doesNotExist());
  }

  @Test
  @Order(4)
  void signUp_givenMissingPayloadOrNotReadable_whenSignUp_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("code")
                .value(GlobalException.REQUEST_BODY_NOT_READABLE.getCode()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("message")
                .value(GlobalException.REQUEST_BODY_NOT_READABLE.getMessage()))
        .andExpect(MockMvcResultMatchers.jsonPath("payload").doesNotExist());
  }

  @Test
  @Order(5)
  void signUp_givenUsernameAlreadyExists_whenSignUp_thenBadRequest() throws Exception {
    registerRequest.setUsername("hoalx1");
    registerRequest.setUsername("hoalx1");
    when(jwtAuthService.signUp(registerRequest))
        .thenThrow(new ServiceException(Failed.ALREADY_EXISTED));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("code").value(Failed.ALREADY_EXISTED.getCode()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("message").value(Failed.ALREADY_EXISTED.getMessage()))
        .andExpect(MockMvcResultMatchers.jsonPath("payload").doesNotExist());
  }
}
