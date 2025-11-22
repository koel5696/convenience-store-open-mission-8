package src.convenience.dto.payment;

import java.util.List;

public record PromotionSuggestionsResponse(
        List<PromotionOption> missingPromotionItems,
        List<PromotionOption> stockIssues
) {

    public record PromotionOption(
            String productName,
            String promotion
    ) {
    }
}
