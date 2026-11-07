package org.veenix.jev;

public class TicketRouter {

    // Tune these to your tolerance for mistakes.
    private static final double CATEGORY_CONFIDENCE = 0.85;
    private static final int URGENCY_ESCALATE = 2;      // top level of our 0,1,2 rubric
    private static final double ANGER_ESCALATE = 0.70;

    public Routing route(TicketDecision d) {

        // Not confident about the category? A human decides.
        if (d.categoryConfidence() < CATEGORY_CONFIDENCE) {
            return Routing.humanReview("Low category confidence: " + d.categoryConfidence());
        }

        // Confident, top-urgency, and the customer is angry: fast-track to a senior agent.
        if (d.urgencyLevel() >= URGENCY_ESCALATE && d.angerProbability() >= ANGER_ESCALATE) {
            return Routing.escalate(d.category());
        }

        // Confident and routine: straight into the normal queue for its category.
        return Routing.queue(d.category());
    }
}