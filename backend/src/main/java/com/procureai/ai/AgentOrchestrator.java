package com.procureai.ai;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AgentOrchestrator {
    private final GroqClient groqClient;
    private final ToolExecutor toolExecutor;

    public AgentOrchestrator(GroqClient groqClient, ToolExecutor toolExecutor) {
        this.groqClient = groqClient;
        this.toolExecutor = toolExecutor;
    }

    public List<Map<String, String>> processMessage(String userMessage) {
        List<Map<String, String>> trace = new ArrayList<>();

        // Step 1: Classify intent (Supervisor Agent)
        trace.add(createStep("thought", "Classifier: Analyzing user intent..."));
        String intent = classifyIntent(userMessage);
        trace.add(createStep("thought", "Classifier: Detected intent — " + intent));

        // Step 2: Choose the right system prompt (Specialist Agent)
        String systemPrompt = getSystemPrompt(intent);
        trace.add(createStep("thought", intent + " Agent: Initializing with domain expertise..."));

        // Step 3: First LLM call — LLM reads tools and decides what to call
        trace.add(createStep("tool_thought", intent + " Agent: Deciding which tools to use..."));
        String firstResponse = groqClient.chat(
            systemPrompt + "\n\n" + toolExecutor.getToolDefinitions(),
            userMessage, 0.3, false
        );

        // Step 4: Check if LLM wants to call tools (handle multiple tool calls)
        String finalResponse = firstResponse;
        if (firstResponse.contains("TOOL_CALL:")) {
            StringBuilder allResults = new StringBuilder();
            String[] lines = firstResponse.split("\n");
            for (String line : lines) {
                if (line.contains("TOOL_CALL:")) {
                    String toolName = line.replace("TOOL_CALL:", "").trim()
                        .replace("()", "").trim();
                    trace.add(createStep("tool_call", "Calling: " + toolName + "()"));

                    String toolResult = toolExecutor.execute(toolName, "");
                    allResults.append("--- ").append(toolName).append(" ---\n");
                    allResults.append(toolResult).append("\n");
                    trace.add(createStep("tool_result", toolResult.length() > 200 ?
                        toolResult.substring(0, 200) + "..." : toolResult));
                }
            }

            // Step 5: Second LLM call — generate final response with ALL tool data
            trace.add(createStep("thought", intent + " Agent: Analyzing tool results and formulating response..."));
            String analysisPrompt = systemPrompt + "\n\nHere is the data from all tools:\n" + allResults.toString() +
                "\n\nProvide a clear, professional summary to the user based on this data.";
            finalResponse = groqClient.chat(analysisPrompt, userMessage, 0.7, false);
        }

        trace.add(createStep("response", finalResponse));
        return trace;
    }

    private String classifyIntent(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("risk") || lower.contains("risky") || lower.contains("high risk")) return "Risk Analysis";
        if (lower.contains("spend") || lower.contains("top") || lower.contains("money") ||
            lower.contains("highest") || lower.contains("total")) return "Spend Analysis";
        if (lower.contains("contract") || lower.contains("expir") || lower.contains("compliance")) return "Compliance";
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) return "General";
        if (lower.contains("help") || lower.contains("what can you")) return "General";
        return "Spend Analysis";
    }

    private String getSystemPrompt(String intent) {
        return switch (intent) {
            case "Risk Analysis" ->
                "You are a Risk Analysis Specialist AI. Your role is to analyze supplier risk data and provide " +
                "clear insights about which suppliers need attention. Be concise and actionable. " +
                "Use the available tools to get real data before answering.";
            case "Compliance" ->
                "You are a Compliance Specialist AI. Your role is to monitor contract compliance, " +
                "track expirations, and ensure procurement activities follow regulations. " +
                "Use the available tools to get real contract data.";
            case "General" ->
                "You are a helpful Procurement AI Assistant. Greet the user warmly and explain what you can do. " +
                "You can analyze supplier spend, assess risks, and check contract compliance. " +
                "Keep it friendly and professional.";
            default ->
                "You are a Spend Analysis Specialist AI. Your role is to analyze procurement spend data " +
                "and provide actionable insights about spending patterns, top suppliers, and category breakdowns. " +
                "Use the available tools to get real data before answering.";
        };
    }

    private Map<String, String> createStep(String type, String content) {
        Map<String, String> step = new LinkedHashMap<>();
        step.put("type", type);
        step.put("content", content);
        step.put("timestamp", new Date().toString());
        return step;
    }
}
