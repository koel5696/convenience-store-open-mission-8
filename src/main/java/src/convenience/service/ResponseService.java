package src.convenience.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.promotion.Promotion;
import src.convenience.domain.entity.Product;
import src.convenience.domain.entity.ProductRepository;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.payment.PromotionSuggestionsResponse;
import src.convenience.dto.payment.PromotionSuggestionsResponse.PromotionOption;
import src.convenience.exception.MembershipException;
import src.convenience.exception.PromotionProductException;

@Service
public class ResponseService {

    private final ProductRepository productRepository;
    private final Promotion promotion;

    public ResponseService(ProductRepository productRepository, Promotion promotion) {
        this.productRepository = productRepository;
        this.promotion = promotion;
    }

    @Transactional(readOnly = true)
    public void responses(PayRequest request, Map<Long, Integer> items) {

        validatePromotionSelection(
                request.addMissingPromotion(), items);
        validatePromotionSelection(
                request.insufficientStock(), items);

        validateMembership(request.membership());
    }

    private void validatePromotionSelection(Boolean selection, Map<Long, Integer> items) {
        if (selection != null) {
            return;
        }

        PromotionSuggestionsResponse suggestions = analyze(items);
        if (suggestions.hasSuggestions()) {
            throw new PromotionProductException(suggestions);
        }
    }

    private void validateMembership(Boolean membership) {
        if (membership == null) {
            throw new MembershipException();
        }
    }

    private PromotionSuggestionsResponse analyze(Map<Long, Integer> items) {
        List<PromotionOption> upsells = new ArrayList<>();
        List<PromotionOption> stockIssues = new ArrayList<>();

        items.forEach((productId, quantity) -> {
            Product product = productRepository.getByIdOrThrow(productId);

            if (promotion.isMissingPromotion(product, quantity)) {
                addSuggestion(product, quantity, upsells, stockIssues);
            }
        });

        return new PromotionSuggestionsResponse(upsells, stockIssues);
    }


    private void addSuggestion(Product product, int quantity,
                               List<PromotionOption> upsells, List<PromotionOption> stockIssues) {

        if (product.compareQuantity(quantity + 1)) {
            upsells.add(new PromotionOption(product.getName(), product.getPromotion()));
            return;
        }

        stockIssues.add(new PromotionOption(product.getName(), product.getPromotion()));
    }
}
