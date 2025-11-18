package src.convenience.exception;

import src.convenience.dto.payment.PromotionSuggestionsResponse;

public class PromotionProductException extends RuntimeException {

    private final PromotionSuggestionsResponse suggestions;

    public PromotionProductException(PromotionSuggestionsResponse suggestions) {
        super("프로모션 상품 이슈로 추가 선택이 필요합니다.");
        this.suggestions = suggestions;
    }

    public PromotionSuggestionsResponse getSuggestions() {
        return suggestions;
    }
}