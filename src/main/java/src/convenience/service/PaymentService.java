package src.convenience.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.convenience.domain.cart.Cart;
import src.convenience.domain.entity.Product;
import src.convenience.domain.entity.ProductRepository;
import src.convenience.domain.payment.PaymentProcessor;
import src.convenience.domain.promotion.PromotionResult;
import src.convenience.domain.receiptGenerator.ReceiptGenerator;
import src.convenience.dto.receipt.ReceiptResponse.ReceiptItem;
import src.convenience.dto.payment.PayRequest;
import src.convenience.dto.receipt.ReceiptResponse;

@Service
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentProcessor processor;
    private final Cart cart;

    public PaymentService(ProductRepository productRepository, PaymentProcessor processor, Cart cart) {
        this.productRepository = productRepository;
        this.processor = processor;
        this.cart = cart;
    }

    @Transactional
    public ReceiptResponse pay(PayRequest request) {
        Map<Long, Integer> items = cart.getItems();
        cart.checkCartEmpty();

        processor.validate(request, items); //사용자 중간 입력사항 검증
        List<ReceiptItem> finalItems = createFinalItems(items); // 영수증에 들어갈 아이템 구분
        int membershipDiscount = processor.membershipDiscount(request, finalItems); // 멤버십 적용

        cart.clear();
        return ReceiptGenerator.generateReceipt(finalItems, membershipDiscount);
    }

    private List<ReceiptItem> createFinalItems(Map<Long, Integer> items) {
        List<ReceiptItem> finalItems = new ArrayList<>();

        items.forEach((productId, quantity) -> {
            Product product = productRepository.getByIdOrThrow(productId);
            PromotionResult result = processor.applyPromotion(product, quantity);
            finalItems.add(new ReceiptItem(product.getName(), product.getPromotion(), product.getPrice(),
                    result.paidQuantity(), result.giftQuantity()));
            decreaseStock(product, quantity);
        });

        return finalItems;
    }


    private void decreaseStock(Product product, Integer quantity) {
        product.decreaseQuantity(quantity);
        productRepository.save(product);
    }
}