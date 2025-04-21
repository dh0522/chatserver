// package com.example.chatserver.chat.config;
//
// import java.util.Set;
// import java.util.concurrent.ConcurrentHashMap;
//
// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.TextMessage;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.handler.TextWebSocketHandler;
//
// // connect 로 웹소켓 연결 요칭이 들어왔을 때 이를 처리할 클래스
// @Component
// public class SimpleWebSocketHandler extends TextWebSocketHandler {
// 	// TextWebSocketHandler 를 상속받아서 WebSocketConfig에 핸들러로 등록될 수 있는 객체로 만들었다.
//
// 	// thread safe set = ConcurrentHashMap.newKeySet();
// 	// 연결된 세션 관리ㅣ: 스레드 safe한 set 사용
// 	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
// 	@Override
// 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
// 		//연결이 되면 세션이 주입된다.
// 		sessions.add(session);
// 		System.out.println("Connected: "+ session.getId());
// 	}
//
// 	// 메시지가 들어왔을 경우
// 	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
// 		String payload = message.getPayload();
// 		System.out.println("received message : "+ payload);
// 		for (WebSocketSession s : sessions) {
// 			if (s.isOpen()){
// 				s.sendMessage(new TextMessage(payload));
// 			}
// 		}
// 	}
//
// 	@Override
// 	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
// 		sessions.remove(session);
// 		System.out.println("disconnected!!!!! ");
//
// 	}
//
// }
