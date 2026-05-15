package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_logs")
public class ChatLog {
    @Id
    private Long id;
    private String provider;
    private String prompt;
    private String response;
    @Builder.Default
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
