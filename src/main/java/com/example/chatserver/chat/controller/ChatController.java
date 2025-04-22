package com.example.chatserver.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	// 그룹 채팅방 개설
	@PostMapping("/room/group/create")
	public ResponseEntity<?> createGroupRoom(@RequestParam String roomName){
		// 왜 id 가 아닌가 ? -> 방을 개설할 때, Id는 자동생성 + auto increment -> 따라서 방 제목만 있으면 돼~
		chatService.createGroupRoom(roomName);

		return ResponseEntity.ok().build();
	}

	// 그룹 채팅 목록 조회
	@GetMapping("/room/group/list")
	public ResponseEntity<?> getGroupChatRooms() {
		List<ChatRoomListResDto> chatRooms = chatService.getGroupChatRooms();


		return new ResponseEntity<>(chatRooms, HttpStatus.OK);
	}

	// 그룹채팅에 참여자 추가
	@PostMapping("/room/group/{roomId}/join")
	public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId){
		chatService.addParticipantToGroupChat(roomId);
		return ResponseEntity.ok().build();
	}
}
