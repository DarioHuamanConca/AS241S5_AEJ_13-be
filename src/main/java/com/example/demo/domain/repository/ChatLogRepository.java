package com.example.demo.domain.repository;

import com.example.demo.domain.model.ChatLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogRepository extends ReactiveCrudRepository<ChatLog, Long> {
}
