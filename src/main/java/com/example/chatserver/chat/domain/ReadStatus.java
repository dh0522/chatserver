package com.example.chatserver.chat.domain;

import jakarta.persistence.Id;

import com.example.chatserver.common.domain.BaseTimeEntity;
import com.example.chatserver.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class ReadStatus extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	// ManyToOne 의 경우에는 FK 설정 때문에 반드시 들어가야 하는 설정이지만
	// OneToMany 의 경우는 필수는 아니다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_message_id", nullable = false)
	private ChatMessage chatMessage;

	@Column(nullable = false)
	private Boolean isRead;
}
