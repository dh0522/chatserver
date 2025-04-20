package com.example.chatserver.common.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.chatserver.common.auth.JwtAuthFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean // Bean이라는 애노테이션이 붙어있는 메소드에서 return 하는 객체를 싱글톤 객체로 만들겠다.
	public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {

		return httpSecurity
			.cors(cors -> cors.configurationSource(corsConfigurationSource()) ) // 원래 web에서는 같은 도메인이 아니면 통신이 안됨. 하지만 서버와 프론트가 같은 도메인일 수가 없다.
			.csrf(AbstractHttpConfigurer::disable) // csrf 비활성
			.httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화 - 보안 인증 방법 중 하나
			// 특정 URL 패턴에 대해서는 Authentication 객체 요구하지 않겠다. (인증처리 제외)
			.authorizeHttpRequests(auth -> auth.requestMatchers("/member/create", "/member/doLogin", "/connect").permitAll().anyRequest().authenticated()) // 인증처리 하지 않을 사이트
			.sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS )) // 세션방식을 사용하지 않겠다는 의미 ( 우리는 토큰 방식을 사용할 것!! )
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // 특정 URL에 대해서는 jwtAuthFilter에 가서 토큰을 만들어주기
			.build();
	}


	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP 메서드 허용
		configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더값 허용
		configuration.setAllowCredentials(true); // 자격증명 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 url 패턴에 대해 cors 허 설정
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder(){

		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


}

















