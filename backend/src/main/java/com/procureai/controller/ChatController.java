package com.procureai.controller;

import com.procureai.ai.AgentOrchestrator;
import com.procureai.ai.GroqClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final AgentOrchestrator orchestrator;
    private final GroqClient groqClient;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ChatController(AgentOrchestrator orchestrator, GroqClient groqClient) {
        this.orchestrator = orchestrator;
        this.groqClient = groqClient;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        List<Map<String, String>> trace = orchestrator.processMessage(message);

        String finalResponse = "";
        for (Map<String, String> step : trace) {
            if (step.get("type").equals("response")) {
                finalResponse = step.get("content");
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("response", finalResponse);
        result.put("trace", trace);
        return result;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(30000L);

        executor.execute(() -> {
            try {
                List<Map<String, String>> trace = orchestrator.processMessage(message);

                for (Map<String, String> step : trace) {
                    emitter.send(SseEmitter.event()
                        .name("step")
                        .data(step));
                    Thread.sleep(100);
                }

                String finalResponse = "";
                for (Map<String, String> step : trace) {
                    if (step.get("type").equals("response")) {
                        finalResponse = step.get("content");
                    }
                }

                // Stream the response word by word
                String[] words = finalResponse.split(" ");
                for (String word : words) {
                    Map<String, String> token = new LinkedHashMap<>();
                    token.put("type", "token");
                    token.put("content", word + " ");
                    emitter.send(SseEmitter.event().name("token").data(token));
                    Thread.sleep(50);
                }

                emitter.send(SseEmitter.event().name("done").data(Map.of("type", "done")));
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error")
                        .data(Map.of("type", "error", "content", e.getMessage())));
                    emitter.complete();
                } catch (Exception ex) {}
            }
        });

        return emitter;
    }

    @GetMapping("/quick")
    public Map<String, Object> quickChat(@RequestParam String message) {
        return chat(Map.of("message", message));
    }
}
