package org.veenix.jev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

import static java.lang.IO.println;

public class TicketTriage {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static void main(String[] args) throws Exception {
        String ticket = """
                Hi so I ordered the deluxe espresso machine TWO WEEKS ago (order BC-48291) \
                and tracking said delivered but there is NOTHING. I paid 379 euros!! \
                I want a refund or replacement ASAP.""";

        ObjectNode body = MAPPER.createObjectNode();
        body.put("model", "jev-latest");
        body.put("state", ticket);

        ObjectNode questions = body.putObject("questions");

        questions.set("category", choice(
                "Which category best fits this support ticket",
                Map.of(
                        "delivery", "Shipping, tracking, or non-delivery problems",
                        "billing", "Charges, refunds, or payment issues",
                        "return", "Returning or exchanging an item",
                        "product_defect", "A faulty or damaged product",
                        "account", "Login, profile, or account access",
                        "other", "Anything that fits none of the above")));

        questions.set("urgency", score(
                "How urgently this ticket needs attention",
                List.of(
                        "Routine, no time pressure",
                        "Should be handled soon",
                        "Drop everything, highly time-sensitive")));

        questions.set("is_angry", noul(
                "The customer is expressing anger or frustration"));

        println("EXECUTING...");

        JevClient jev = new JevClient(System.getenv("TYPESAFE_API_KEY"));
        var result = jev.evaluate(body);

        var ticketDecision = TicketDecision.from(result);
        println(ticketDecision);

        var router = new TicketRouter();
        var routing = router.route(ticketDecision);
        println(routing);
    }

    private static ObjectNode choice(String instructions, Map<String, String> criteria) {
        ObjectNode q = MAPPER.createObjectNode();
        q.put("type", "choice");
        q.put("instructions", instructions);
        ObjectNode c = q.putObject("criteria");
        criteria.forEach(c::put);
        return q;
    }

    private static ObjectNode score(String instructions, List<String> levels) {
        ObjectNode q = MAPPER.createObjectNode();
        q.put("type", "score");
        q.put("instructions", instructions);
        ArrayNode c = q.putArray("criteria");
        levels.forEach(c::add);
        return q;
    }

    private static ObjectNode noul(String instructions) {
        ObjectNode q = MAPPER.createObjectNode();
        q.put("type", "noul");
        q.put("instructions", instructions);
        return q;
    }
}