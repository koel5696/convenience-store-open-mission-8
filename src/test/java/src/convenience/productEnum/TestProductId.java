package src.convenience.productEnum;

public enum TestProductId {
    COKE(1L),
    CIDER(2L),
    JUICE(3L),
    ENERGY_BAR(9L);

    private final Long id;

    TestProductId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }
}
