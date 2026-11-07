package org.veenix.jev;

public record Routing(String destination, String reason) {

    public static Routing escalate(String category) {
        return new Routing("senior-queue:" + category, "urgent and angry");
    }

    public static Routing queue(String category) {
        return new Routing("queue:" + category, "routine");
    }

    public static Routing humanReview(String reason) {
        return new Routing("human-triage", reason);
    }
}