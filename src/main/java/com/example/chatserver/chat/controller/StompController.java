package com.example.chatserver.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.chatserver.chat.dto.ChatMessageReqDto;
import com.example.chatserver.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompController {

	private final ChatService chatService;
	private final SimpMessageSendingOperations messageTemplate;

	// 방법1. MessageMapping(수신) 과 SendTo(topic에 메시지 전달) 한꺼번에 처리

	// @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행시 MessageMapping 수신
	// @SendTo("/topic/{roomId}") // 발행 : 해당 roomId에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
	// // DestinationVariable: @MessageMapping 어노테이션으로 정의된 WebSocket Controller 내에서만 사용한다.
	// public String sendMessage(@DestinationVariable Long roomId, String message ){
	// 	System.out.println(message);
	//
	// 	return message;
	// }

	// 방법2. MessageMapping 어노테이션만 활용
	// WHY ? 어노테이션을 쓰면 코드의 유연성이 떨어짐 ! 따라서 직접 토픽에 메시지를ㄹ 전달하겠다 !

	@MessageMapping("/{roomId}")
	public void sendMessage(@DestinationVariable Long roomId, ChatMessageReqDto chatMessageReqDto ){
		System.out.println(chatMessageReqDto.getMessage());

		chatService.saveMessage(roomId, chatMessageReqDto);
		messageTemplate.convertAndSend("/topic/" + roomId, chatMessageReqDto);

	}

}

















