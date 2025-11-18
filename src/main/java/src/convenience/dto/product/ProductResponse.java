package src.convenience.dto.product;

import src.convenience.domain.entity.product.Product;

public record ProductResponse(Long id, String name, int price, int quantity, String promotion) {
    public ProductResponse(Product product) {
        this(product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getPromotion());
    }
}