package springproject.iam.v1.service.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.mapper.struct.AuthMapper;
import springproject.iam.v1.model.dto.auth.CredentialResponse;
import springproject.iam.v1.model.dto.auth.RegisterRequest;
import springproject.iam.v1.model.dto.role.RoleResponse;
import springproject.iam.v1.model.dto.user.UserResponse;
import springproject.iam.v1.service.user.AbstractUserService;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FieldDefaults(level = AccessLevel.PRIVATE)
class NimbusNimbusJwtAuthServiceTest {
  @Autowired AuthMapper authMapper;
  @Autowired NimbusJwtAuthService nimbusJwtAuthService;

  @MockBean AbstractUserService jpaUserService;

  RegisterRequest registerRequest;
  UserResponse userResponse;
  RoleResponse adminRoleResponse;

  @BeforeAll
  void beforeAll() {
    registerRequest = new RegisterRequest("hoalx1", "hoalx1", new HashSet<>(List.of(1L)));
    adminRoleResponse = new RoleResponse(1L, "Admin", "Admin", null);
    userResponse =
        new UserResponse(
            1L,
            registerRequest.getUsername(),
            registerRequest.getPassword(),
            new HashSet<>(List.of(adminRoleResponse)),
            null);
  }

  @Test
  @Order(1)
  void signUp_givenLegal_whenSignUp_thenSuccess() {
    when(jpaUserService.save(authMapper.asUserCreation(registerRequest))).thenReturn(userResponse);
    CredentialResponse returnCredential = nimbusJwtAuthService.signUp(registerRequest);
    Assertions.assertThat(returnCredential.getAccessToken()).isNotEmpty();
    Assertions.assertThat(returnCredential.getRefreshToken()).isNotEmpty();
  }

  @Test
  @Order(2)
  void signUp_givenUsernameAlreadyExisted_whenSignUp_thenThrowError() {
    registerRequest.setUsername("hoalx2");
    when(jpaUserService.save(authMapper.asUserCreation(registerRequest)))
        .thenThrow(ServiceException.class);
    assertThrows(ServiceException.class, () -> nimbusJwtAuthService.signUp(registerRequest));
  }

  @Test
  @Order(3)
  void signUp_givenLegal_whenSignUp_thenThrowInternalServerError() {
    registerRequest.setUsername("hoalx1");
    when(jpaUserService.save(authMapper.asUserCreation(registerRequest)))
        .thenThrow(RuntimeException.class);
    ServiceException exception =
        assertThrows(ServiceException.class, () -> nimbusJwtAuthService.signUp(registerRequest));
    Assertions.assertThat(exception.getFailed()).isEqualByComparingTo(Failed.SIGN_UP);
  }
}
