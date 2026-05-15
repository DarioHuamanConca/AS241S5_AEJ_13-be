package com.example.demo.service;

import com.example.demo.domain.model.ChatLog;
import com.example.demo.domain.repository.ChatLogRepository;
import com.example.demo.web.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final WebClient llamaWebClient;
    private final WebClient ttsWebClient;
    private final ChatLogRepository chatLogRepository;

    // ── CREATE ──────────────────────────────────────────────────────────────
    public Mono<String> askLlama(ChatRequest request) {
        Map<String, Object> body = Map.of(
                "messages", List.of(Map.of("role", "user", "content", request.getPrompt())),
                "web_access", false
        );

        return llamaWebClient.post()
                .uri("")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String content = (String) response.get("result");
                    if (content == null) {
                        content = "Unexpected API Response: " + response;
                    }
                    return saveLog("LLAMA", request.getPrompt(), content);
                });
    }

    public Mono<byte[]> generateAudio(ChatRequest request) {
        Map<String, Object> body = Map.of(
                "model", "tts-1",
                "input", request.getPrompt(),
                "instructions", "Speak in a clear, neutral tone.",
                "voice", "alloy"
        );

        return ttsWebClient.post()
                .uri("")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(audioBytes -> {
                    String base64Audio = java.util.Base64.getEncoder().encodeToString(audioBytes);
                    String dataUri = "data:audio/mpeg;base64," + base64Audio;
                    return saveLog("TTS", request.getPrompt(), dataUri)
                            .thenReturn(audioBytes);
                });
    }

    // ── READ ─────────────────────────────────────────────────────────────────
    public Flux<ChatLog> getAllLogs() {
        return chatLogRepository.findAll()
                .filter(log -> Boolean.TRUE.equals(log.getActive()));
    }

    public Mono<ChatLog> getLogById(Long id) {
        return chatLogRepository.findById(id)
                .filter(log -> Boolean.TRUE.equals(log.getActive()))
                .switchIfEmpty(Mono.error(new RuntimeException("Log not found or deleted: " + id)));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public Mono<String> updateLog(Long id, ChatRequest request) {
        Map<String, Object> body = Map.of(
                "messages", List.of(Map.of("role", "user", "content", request.getPrompt())),
                "web_access", false
        );

        return chatLogRepository.findById(id)
                .filter(log -> Boolean.TRUE.equals(log.getActive()))
                .switchIfEmpty(Mono.error(new RuntimeException("Log not found: " + id)))
                .flatMap(existingLog ->
                        llamaWebClient.post()
                                .uri("")
                                .bodyValue(body)
                                .retrieve()
                                .bodyToMono(Map.class)
                                .flatMap(response -> {
                                    String content = (String) response.get("result");
                                    if (content == null) content = "Unexpected: " + response;

                                    existingLog.setPrompt(request.getPrompt());
                                    existingLog.setResponse(content);
                                    existingLog.setUpdatedAt(LocalDateTime.now());

                                    return chatLogRepository.save(existingLog)
                                            .thenReturn(content);
                                })
                );
    }

    // ── DELETE (lógico) ───────────────────────────────────────────────────────
    public Mono<Void> deleteLog(Long id) {
        return chatLogRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Log not found: " + id)))
                .flatMap(log -> {
                    log.setActive(false);
                    log.setUpdatedAt(LocalDateTime.now());
                    return chatLogRepository.save(log).then();
                });
    }

    // ── UTIL ──────────────────────────────────────────────────────────────────
    private Mono<String> saveLog(String provider, String prompt, String response) {
        String safeResponse = response != null ? response : "No response";
        ChatLog log = ChatLog.builder()
                .provider(provider)
                .prompt(prompt)
                .response(safeResponse)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return chatLogRepository.save(log)
                .thenReturn(safeResponse);
    }
}
