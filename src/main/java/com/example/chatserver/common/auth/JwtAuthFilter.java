package com.example.chatserver.common.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // 싱글톤 객체로 만들기 !
public class JwtAuthFilter extends GenericFilter {

	@Value("${jwt.secretKey}")
	private String secretKey;

	// 사용자가 token 들고 웹에 들어오면 토큰을 까서 그 토큰이 우리서버에서 만든 토큰인지 검증하는 코드!
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;

		String token = httpServletRequest.getHeader("Authorization");
		try {
			if (token != null){
				if (!token.substring(0,7).equals("Bearer ")){
					throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
				}

				String jwtToken = token.substring(7);

				// 토큰 검증 및 claims 추출 -> 여기서 오류가 발생한다면 try/catch 문으로 exception 발생
				Claims claims = Jwts.parserBuilder()
									.setSigningKey(secretKey)
									.build()
									.parseClaimsJws(jwtToken) // 사용자가 보낸 토큰과, 내가 사용자 정보에 맞추어서 토큰을 새로 만들어봄
									.getBody();


				// Authentication 객체 생성
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
				UserDetails userDetails = new User(claims.getSubject(), "", authorities);
				Authentication authentication =
					new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());


				// 즉 authentication 에는 email과 role이 들어가있음!

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			chain.doFilter(request, response);

		}catch (Exception e){
			e.printStackTrace();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType("application/json");
			httpServletResponse.getWriter().write("invalid token");
		}


		// request에서 토큰을 꺼내봄
		// 1. 정상이면 doFilter로 돌아감 -> 즉, filterChain 으로 돌아간다.
		// 2. 정상이 아니면, 에러를 반환해준다.
	}


}








