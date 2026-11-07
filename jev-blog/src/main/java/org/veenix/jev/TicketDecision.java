package org.veenix.jev;

import com.fasterxml.jackson.databind.JsonNode;

public record TicketDecision(
        String category,
        double categoryConfidence,
        int urgencyLevel,
        double angerProbability
) {

    public static TicketDecision from(JsonNode response) {
        JsonNode answers = response.get("answers");

        JsonNode category = answers.get("category");
        String chosen = category.get("choice").asText();
        double categoryConfidence = category.get("confidence").asDouble();

        int urgencyLevel = answers.get("urgency").get("score").asInt();
        double anger = answers.get("is_angry").get("noul").asDouble();

        return new TicketDecision(chosen, categoryConfidence, urgencyLevel, anger);
    }
}