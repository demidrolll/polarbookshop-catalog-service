package com.polarbookshop.catalogservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {

  @Bean
  fun filterChain(http: HttpSecurity): SecurityFilterChain =
    http
      .authorizeHttpRequests {
        it.requestMatchers(HttpMethod.GET, "/", "/books/**").permitAll()
          .requestMatchers("/actuator/**").permitAll()
          .anyRequest().hasRole("employee")
      }
      .oauth2ResourceServer {
        it.jwt(Customizer.withDefaults())
      }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .csrf {
        it.disable()
      }
      .build()

  @Bean
  fun jwtAuthenticationConverter(): JwtAuthenticationConverter =
    JwtGrantedAuthoritiesConverter().let { granted ->
      granted.setAuthorityPrefix("ROLE_")
      granted.setAuthoritiesClaimName("roles")

      JwtAuthenticationConverter().also { converter ->
        converter.setJwtGrantedAuthoritiesConverter(granted)
      }
    }
}
