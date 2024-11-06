package springproject.iam.v1.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import springproject.iam.v1.constant.Failed;
import springproject.iam.v1.exception.GlobalException;
import springproject.iam.v1.exception.ServiceException;
import springproject.iam.v1.response.Response;
import springproject.iam.v1.service.user.AbstractUserService;
import springproject.iam.v1.token.NimbusJWTAuthProvider;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DaoAuthenticationFilter extends OncePerRequestFilter {
  @NonFinal
  @Value("${jwt.access-token.secret}")
  String jwtAccessTokenSecret;

  NimbusJWTAuthProvider authProvider;
  AbstractUserService jpaUserService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (StringUtils.isEmpty(authorizationHeader)) {
        filterChain.doFilter(request, response);
        return;
      }
      String accessToken = authorizationHeader.replace("Bearer ", "");
      SignedJWT verify = authProvider.verify(accessToken, jwtAccessTokenSecret);
      String username = verify.getJWTClaimsSet().getSubject();
      if (StringUtils.isEmpty(username)) {
        throw new ServiceException(Failed.ILL_LEGAL_JWT_TOKEN);
      }
      if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
        UserDetails userDetails = jpaUserService.userDetailsService().loadUserByUsername(username);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
      }
      filterChain.doFilter(request, response);
    } catch (ServiceException e) {
      Failed failed = e.getFailed();
      immediatelyTerminate(response, failed.getCode(), failed.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      GlobalException uncategorized = GlobalException.UNCATEGORIZED_EXCEPTION;
      immediatelyTerminate(response, uncategorized.getCode(), uncategorized.getMessage());
    }
  }

  private void immediatelyTerminate(HttpServletResponse response, int code, String message)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    Response<Object> resp =
        Response.builder()
            .code(code)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    response.getWriter().write(objectMapper.writeValueAsString(resp));
    response.flushBuffer();
  }
}
