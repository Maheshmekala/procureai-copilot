package com.procureai.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "app", "ProcureAI Copilot",
            "version", "2.0.0",
            "ai", "Groq Llama 3.3 70B",
            "features", List.of("Multi-Agent RAG", "SSE Streaming", "Tool Calling", "Risk Analysis", "Compliance")
        );
    }
}
