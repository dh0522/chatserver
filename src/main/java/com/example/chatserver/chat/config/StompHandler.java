package com.example.chatserver.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.service.ChatService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component // 인증 작엄: 토큰을 꺼내서 우리가 만들어준 토큰이 맞는지 검증해준다.
public class StompHandler implements ChannelInterceptor  {

	private final ChatService chatService;
	private final ChatRoomRepository chatRoomRepository;

	@Value("${jwt.secretKey}")
	private String secretKey;



	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		// accessor 안에서 토큰 꺼낼 수 있다
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


		if (StompCommand.CONNECT == accessor.getCommand()){
			System.out.println("connect 요청 시 토큰 유효성 검증 ");
			String bearerToken = accessor.getFirstNativeHeader("Authorization");
			String token = bearerToken.substring(7);


			// token 검증
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

			System.out.println("토큰 검증 완료");
		}


		// 요청을 보낸 user 가 해당 채팅방의 참여자인지 여부 확인
		if (StompCommand.SUBSCRIBE == accessor.getCommand()){
			System.out.println("subscribe 검증 ");

			String bearerToken = accessor.getFirstNativeHeader("Authorization");
			String token = bearerToken.substring(7);

			Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();

			String email = claims.getSubject();
			String roomId = accessor.getDestination().split("/")[2];

			if(!chatService.isRoomParticipant(email, Long.parseLong(roomId))){
				throw new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다.");
			}

		}
		return message;
	}


}
