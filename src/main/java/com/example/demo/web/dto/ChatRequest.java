package com.example.demo.web.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String prompt;
    private String model; // Optional: specify model
}
