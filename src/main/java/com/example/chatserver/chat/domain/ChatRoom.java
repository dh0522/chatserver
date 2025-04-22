package com.example.chatserver.chat.domain;

import java.util.ArrayList;
import java.util.List;



import com.example.chatserver.common.domain.BaseTimeEntity;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatRoom extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Builder.Default // builder패턴에서 기본값 N으로 깔아주기 위해선 필요함!
	private String isGroupChat = "N";

				// 외래키 이름
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
	private List<ChatParticipant> chatParticipants = new ArrayList<>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();

}



