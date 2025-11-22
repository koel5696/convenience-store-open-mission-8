package src.convenience.service.paymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;
import src.convenience.domain.entity.promotion.Promotion;
import src.convenience.domain.entity.promotion.PromotionRepository;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.payment.PromotionSuggestionsResponse;
import src.convenience.dto.payment.PromotionSuggestionsResponse.PromotionOption;
import src.convenience.exception.MembershipException;
import src.convenience.exception.PromotionProductException;

@Service
public class PaymentFlowChecker {
    private static final int MISSED_GIFT_QUANTITY = 1;

    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public PaymentFlowChecker(ProductRepository productRepository, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    @Transactional(readOnly = true)
    public void checkFlow(PayRequest request, Map<Long, Integer> items) {
        PromotionSuggestionsResponse suggestions = analyzePromotion(items);

        validateMissingPromotion(request.missingPromotion(), suggestions.missingPromotionItems());
        validateStockIssue(request.insufficientStock(), suggestions.stockIssues());
        validateMembership(request.membership());
    }

    private void validateMissingPromotion(Boolean userSelection, List<PromotionOption> missingPromotionItems) {
        if (!missingPromotionItems.isEmpty() && userSelection == null) {
            throw new PromotionProductException(new PromotionSuggestionsResponse(missingPromotionItems, List.of()));
        }
    }

    private void validateStockIssue(Boolean userSelection, List<PromotionOption> stockIssues) {
        if (!stockIssues.isEmpty() && userSelection == null) {
            throw new PromotionProductException(new PromotionSuggestionsResponse(List.of(), stockIssues));
        }
    }

    private void validateMembership(Boolean membership) {
        if (membership == null) {
            throw new MembershipException();
        }
    }

    private PromotionSuggestionsResponse analyzePromotion(Map<Long, Integer> items) {
        List<PromotionOption> missingPromotionItems = new ArrayList<>();
        List<PromotionOption> stockIssues = new ArrayList<>();

        items.forEach((productId, quantity) -> {
            Product product = productRepository.getByIdOrThrow(productId);
            Promotion promotion = promotionRepository.promotionCreate(product.getPromotion());

            if (promotion.isMissingPromotion(quantity)) {
                addSuggestion(product, quantity, missingPromotionItems, stockIssues);
            }
        });

        return new PromotionSuggestionsResponse(missingPromotionItems, stockIssues);
    }

    private void addSuggestion(Product product, int quantity,
                               List<PromotionOption> missingPromotionItems, List<PromotionOption> stockIssues) {

        if (product.compareQuantity(quantity + MISSED_GIFT_QUANTITY)) {
            missingPromotionItems.add(new PromotionOption(product.getName(), product.getPromotion()));
            return;
        }

        stockIssues.add(new PromotionOption(product.getName(), product.getPromotion()));
    }
}
