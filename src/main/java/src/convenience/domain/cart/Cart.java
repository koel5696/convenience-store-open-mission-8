package src.convenience.domain.cart;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import src.convenience.exception.BusinessException;
import src.convenience.exception.constants.ErrorCode;

@Component
@SessionScope
public class Cart {
    Map<Long, Integer> items = new HashMap<>();

    public void addItem(Long productId, int quantity) {
        items.put(productId, items.getOrDefault(productId, 0) + quantity);
    }

    public void removeItem(Long productId) {
        if (!items.containsKey(productId)) {
            throw new BusinessException(ErrorCode.NOT_EXIST_IN_CART);
        }
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public Map<Long, Integer> getItems() {
        return new HashMap<>(items);
    }

    public void checkCartEmpty() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public int currentQuantity(Long productId) {
        Integer quantity = items.get(productId);
        if (quantity == null) {
            return 0;
        }
        return quantity;
    }
}
