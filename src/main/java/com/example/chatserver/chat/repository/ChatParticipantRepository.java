package com.example.chatserver.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

	List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

	Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
