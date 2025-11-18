package src.convenience.domain.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "promotion")
    private String promotion;

    protected Product() {
    }

    public Product(String name, int price, int quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public void decreaseQuantity(int quantityToDecrease) {
        checkQuantity(quantityToDecrease);
        this.quantity -= quantityToDecrease;
    }

    public void checkQuantity(int quantityToCheck) {
        if(quantity < quantityToCheck) {
            throw new IllegalArgumentException("재고가 부족합니다. (남은 재고: " + this.quantity + "개)");
        }
    }

    public boolean compareQuantity(int quantityToCompare) {
        return this.quantity >= quantityToCompare;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotion() {
        return promotion;
    }
}