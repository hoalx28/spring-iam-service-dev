package springproject.iam.v1.configuration;

import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import springproject.iam.v1.exception.OauthResourceJwtAuthenticationEntryPoint;
import springproject.iam.v1.exception.SecurityJwtAuthenticationEntryPoint;
import springproject.iam.v1.filter.DaoAuthenticationFilter;
import springproject.iam.v1.service.user.JpaUserService;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@CrossOrigin
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSecurityConfigurer {
  @NonFinal
  String[] publicEndpoints = {
    "/api/v1/auth/sign-in",
    "/api/v1/auth/sign-up",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/api/v1/privileges",
    "/api/v1/roles"
  };

  @NonFinal
  @Value("${jwt.access-token.secret}")
  String accessTokenSecret;

  DaoAuthenticationFilter daoAuthenticationFilter;
  JpaUserService jpaUserService;
  PasswordEncoder passwordEncoder;

  @Bean
  @ConditionalOnProperty(
      value = "spring.security.authentication.provider",
      havingValue = "oauth_resource_server",
      matchIfMissing = true)
  public SecurityFilterChain oauthResourceFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.authorizeHttpRequests(
        request ->
            request.requestMatchers(publicEndpoints).permitAll().anyRequest().authenticated());
    httpSecurity.oauth2ResourceServer(
        oauth2 ->
            oauth2
                .jwt(
                    jwtConfigurer ->
                        jwtConfigurer
                            .decoder(jwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new OauthResourceJwtAuthenticationEntryPoint()));
    return httpSecurity.build();
  }

  @Bean
  @ConditionalOnProperty(
      value = "spring.security.authentication.provider",
      havingValue = "spring_security")
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.authorizeHttpRequests(
        request ->
            request.requestMatchers(publicEndpoints).permitAll().anyRequest().authenticated());
    httpSecurity.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    httpSecurity
        .authenticationProvider(securityProvider())
        .addFilterBefore(daoAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    httpSecurity.httpBasic(
        basicConfigurer ->
            basicConfigurer.authenticationEntryPoint(new SecurityJwtAuthenticationEntryPoint()));
    return httpSecurity.build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    SecretKeySpec secretKeySpec = new SecretKeySpec(accessTokenSecret.getBytes(), "HS256");
    return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  public AuthenticationProvider securityProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(jpaUserService.userDetailsService());
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }
}
