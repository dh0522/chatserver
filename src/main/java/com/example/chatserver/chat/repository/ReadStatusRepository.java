package com.example.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatserver.chat.domain.ReadStatus;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
}
