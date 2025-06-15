package com.example.chatserver.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final ChatParticipantRepository chatParticipantRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MemberRepository memberRepository;

	public void saveMessage(Long roomId, ChatMessageDto chatMessageDto){
		// 해당 메시지가 어떤 room 으로 (송신자 + 내용) 으로 전달되는지 저장한다.
		// 1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new EntityNotFoundException("해당 채팅방이 존재하지 않습니다.")
		);

		// 2. 보낸 사람 조회
		Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail()).orElseThrow(
			() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다.")
		);

		// 3. 메시지 저장 - 1) chat 메시지 조립 2) Db에 저장
		ChatMessage chatMessage = ChatMessage.builder()
			.chatRoom(chatRoom)
			.member(sender)
			.content(chatMessageDto.getMessage())
			.build();

		chatMessageRepository.save(chatMessage);

		// 4. 사용자별로 읽음여부 저장 - 1) 해당 chatRoom 참여자 조회 2) 발송자 제외 모든 참여자는 "안읽음" 처리
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

		for (ChatParticipant c : chatParticipants) {
			ReadStatus readStatus = ReadStatus.builder()
				.chatRoom(chatRoom)
				.member(c.getMember())
				.chatMessage(chatMessage)
				.isRead(c.getMember().equals(sender))
				.build();

			readStatusRepository.save(readStatus);
		}
	}

	public void createGroupRoom(String roomName) {
		// 채팅방을 만들면, 만든 사람은 당연히 채팅방 참여자로 추가되어야 함
		// 따라서 chatRoom 만들고 chatparticipant 만들기

		// 1. Security Context Holder 에서 해당 로그인 된 멤버 정보 가져오기
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

		Member member = memberRepository.findByEmail(userEmail).orElseThrow(
			() -> new EntityNotFoundException("member cannot be found")
		);

		// 2. 채팅방 생성
		ChatRoom chatRoom = ChatRoom.builder()
								.name(roomName)
								.isGroupChat("Y")
								.build();

		chatRoomRepository.save(chatRoom);

		// 3. 채팅 참여자로 개설자를 추가하기
		ChatParticipant chatParticipant = ChatParticipant.builder()
													.chatRoom(chatRoom)
													.member(member)
													.build();

		chatParticipantRepository.save(chatParticipant);
	}

	public List<ChatRoomListResDto> getGroupChatRooms(){

		List<ChatRoomListResDto> dtos = new ArrayList<>();
		List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");

		for (ChatRoom c : chatRooms) {
			ChatRoomListResDto dto = ChatRoomListResDto.builder()
																		.roomId(c.getId())
																		.roomName(c.getName())
																		.build();

			dtos.add(dto);
		}

		return dtos;
	}

	public void addParticipantToGroupChat(Long roomId){
		// 채팅방 존재하는지 + 그룹 채팅인지
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new EntityNotFoundException("not found chat room")
		);

		if (chatRoom.getIsGroupChat().equals("N"))
			throw new IllegalAccessError("그룹채팅 방이 아닙니다.");

		// 사용자가 이미 있다면 추가 X
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new EntityNotFoundException("not found member")
		);

		// 이미 채팅방에 참여하고 있는지
		Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);

		// 참여자가 아니라면 추가하기
		if (!participant.isPresent()){
			addParticipantToRoom(chatRoom, member);
		}


	}

	public void addParticipantToRoom(ChatRoom chatRoom, Member member){
		ChatParticipant chatParticipant = ChatParticipant.builder()
														.chatRoom(chatRoom)
														.member(member)
														.build();

		chatParticipantRepository.save(chatParticipant);
	}

	// 특정 Room 에 대한 message 조회
	/// 내가 해당 채팅방의 참여자가 아닌 경우 에러가 발생해야 함.
	public List<ChatMessageDto> getChatHistory(Long roomId){

		// 1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new IllegalArgumentException("해당하는 채팅방이 없습니다.")
		);

		// 2. 해당 채팅방의 유저 조회
		// 1) 로그인한 유저
		Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
			.orElseThrow(
				() -> new IllegalArgumentException("로그인한 유저가 없습니다.")
			);

		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
		boolean check = false;

		for (ChatParticipant c : chatParticipants) {
			if (c.getMember().equals(member)){
				check = true;
			}
		}

		if (!check){
			throw new IllegalArgumentException("본인이 속하지 않는 채팅방입니다.");
		}

		List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);

		return chatMessages.stream()
			.map(c -> ChatMessageDto.builder()
				.message(c.getContent())
				.senderEmail(c.getMember().getEmail())
				.build())
			.toList();

	}


	public boolean isRoomParticipant(String email, Long roomId){

		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new IllegalArgumentException("해당 채팅방이 없습니다.")
		);

		Member member = memberRepository.findByEmail(email).orElseThrow(
			() -> new IllegalArgumentException("no member")
		);

		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

		for (ChatParticipant c : chatParticipants) {
			if (c.getMember().equals(member)){
				return true;
			}
		}

		return false;

	}
}















