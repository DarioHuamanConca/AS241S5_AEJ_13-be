package com.example.demo.web.api;

import com.example.demo.domain.model.ChatLog;
import com.example.demo.service.AIService;
import com.example.demo.web.dto.ChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Chat API", description = "CRUD de consultas a IA (Llama/TTS)")
public class ChatController {

    private final AIService aiService;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @PostMapping("/llama")
    @Operation(summary = "✅ CREATE - Enviar consulta a Llama/Qwen y guardar en BD")
    public Mono<String> askLlama(@RequestBody ChatRequest request) {
        return aiService.askLlama(request);
    }

    @PostMapping(value = "/tts", produces = "audio/mpeg")
    @Operation(summary = "✅ CREATE - Generar audio con TTS y guardar en BD")
    public Mono<ResponseEntity<byte[]>> generateAudio(@RequestBody ChatRequest request) {
        return aiService.generateAudio(request)
                .map(audioBytes -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(audioBytes));
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    @GetMapping("/logs")
    @Operation(summary = "✅ READ - Listar todas las consultas activas")
    public Flux<ChatLog> getAllLogs() {
        return aiService.getAllLogs();
    }

    @GetMapping("/logs/{id}")
    @Operation(summary = "✅ READ - Obtener una consulta por ID")
    public Mono<ChatLog> getLogById(@PathVariable Long id) {
        return aiService.getLogById(id);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @PutMapping("/logs/{id}")
    @Operation(summary = "✅ UPDATE - Re-ejecutar una consulta con nuevo prompt usando IA")
    public Mono<String> updateLog(@PathVariable Long id, @RequestBody ChatRequest request) {
        return aiService.updateLog(id, request);
    }

    // ── DELETE (lógico) ───────────────────────────────────────────────────────
    @DeleteMapping("/logs/{id}")
    @Operation(summary = "✅ DELETE - Borrado lógico (marca como inactiva)")
    public Mono<ResponseEntity<Void>> deleteLog(@PathVariable Long id) {
        return aiService.deleteLog(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
