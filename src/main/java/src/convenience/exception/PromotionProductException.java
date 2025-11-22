package src.convenience.exception;

import src.convenience.dto.payment.PromotionSuggestionsResponse;

public class PromotionProductException extends RuntimeException {

    private final PromotionSuggestionsResponse suggestions;

    public PromotionProductException(PromotionSuggestionsResponse suggestions) {
        super("프로모션 관련 사용자 선택 필요");
        this.suggestions = suggestions;
    }

    public PromotionSuggestionsResponse getSuggestions() {
        return suggestions;
    }
}