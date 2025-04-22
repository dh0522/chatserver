package com.example.chatserver.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public Member create(MemberSaveReqDto memberSaveReqDto){

		// 이미 가입되어 있는 이메일 검증
		if( memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent() ){
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}

		Member newMember = Member.builder()
					.name(memberSaveReqDto.getName())
					.email(memberSaveReqDto.getEmail())
					.password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
					.build();

		Member member = memberRepository.save(newMember);

		return member;
	}

	public Member login(MemberLoginReqDto memberLoginReqDto){

		Member member = memberRepository.findByEmail(memberLoginReqDto.getEmail()).orElseThrow(
			() -> new EntityNotFoundException("존재하지 않는 이메일입니다.")
		);

		// DB에 있는 비번과 회원이 지금 입력한 비번이 동일한지 확인해줌
		// 암호화하지 않아도 passwordEncoder.matches가 암호화해서 두개의 비밀번호가 동일한지 확인해준다.
		if (!passwordEncoder.matches(memberLoginReqDto.getPassword(), member.getPassword())){
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}



		return member;
	}

	public List<MemberListResDto> findAll(){
		List<Member> members = memberRepository.findAll();

		List<MemberListResDto> memberListResDtos = new ArrayList<>();
		for (Member member: members){
			MemberListResDto dto = new MemberListResDto();
			dto.setId(member.getId());
			dto.setEmail(member.getEmail());
			dto.setName(member.getName());

			memberListResDtos.add(dto);
		}

		return memberListResDtos;
	}
}















