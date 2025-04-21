package com.example.chatserver.chat.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// 실시간 서버에서 문제가 되는게 connection 이 많아져 서버관리 X -> 철저하게 test 해봐야 해!
// spring과 stomp는 기본적으로 세션관리를 자동(내부적)으로 처리
// 연결/해제 이벤트를 기록, 연결된 세션 수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성
// => 로그, 디버깅 목적
@Component
public class StompEventListener {

	private final Set<String> sessions = ConcurrentHashMap.newKeySet();

	@EventListener
	public void connectHandle(SessionConnectEvent event){
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		sessions.add(accessor.getSessionId());

		System.out.println("connect session Id "+ accessor.getSessionId());
		System.out.println("total session: "+ sessions.size());
	}

	@EventListener
	public void disconnectHandle(SessionDisconnectEvent event){
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		sessions.remove(accessor.getSessionId());
		System.out.println("disconnect session Id "+ accessor.getSessionId());
		System.out.println("total session: "+ sessions.size());
	}


}

