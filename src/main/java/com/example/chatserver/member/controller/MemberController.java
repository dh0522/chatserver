package com.example.chatserver.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/create")
	public ResponseEntity<?>  memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto){

		Member member = memberService.create(memberSaveReqDto);

		return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);

	}

	// 로그인을 해서, 아이디 비번이 맞으면 토큰을 생성해주기
	@PostMapping("/doLogin")
	public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto){
		// email, pw 검증
		Member member = memberService.login(memberLoginReqDto);

		// 일치할 경우, access 토큰 발행
		String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

		// 사용자에게 JSON 형태로 데이터를 주고 싶음! -> HashMap !!
		Map<String, Object> loginInfo = new HashMap<>();
		loginInfo.put("id", member.getId());
		loginInfo.put("token", jwtToken);

		return new ResponseEntity<>(loginInfo, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<?> memberList(){

		List<MemberListResDto> dtos = memberService.findAll();

		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}



}


















