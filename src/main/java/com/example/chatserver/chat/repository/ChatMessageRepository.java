package com.example.chatserver.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
}
