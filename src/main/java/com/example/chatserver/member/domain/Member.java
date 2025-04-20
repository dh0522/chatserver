package com.example.chatserver.member.domain;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // DB와 싱크가 맞게 된다.
@Builder // Builder는 꼭 AllArgs와 함께 쓴다.
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	@Builder.Default //
	private Role role = Role.USER;

}
