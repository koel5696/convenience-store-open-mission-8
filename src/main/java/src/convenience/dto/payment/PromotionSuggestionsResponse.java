package src.convenience.dto.payment;

import java.util.List;

public record PromotionSuggestionsResponse(
        List<PromotionOption> upsells,     // "추가 담기" 필요 상품 목록
        List<PromotionOption> stockIssues  // "재고부족" 상품 목록
) {

    public record PromotionOption(
            String productName,
            String promotion
    ) {}

    public boolean hasSuggestions() {
        return !upsells.isEmpty() || !stockIssues.isEmpty();
    }
}
