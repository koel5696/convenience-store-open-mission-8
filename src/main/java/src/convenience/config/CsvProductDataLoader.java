package src.convenience.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;

@Component
public class CsvProductDataLoader implements ApplicationRunner {

    private static final String CSV_PATH = "/products.csv";
    private static final String CSV_DELIMITER = ",";

    private final ProductRepository productRepository;

    public CsvProductDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (productRepository.count() > 0) {
            return;
        }

        try (BufferedReader reader = createReader()) {
            List<Product> products = reader.lines()
                    .skip(1) // 헤더 스킵
                    .map(this::toProduct)
                    .toList();

            productRepository.saveAll(products);
        }
    }

    private BufferedReader createReader() {
        InputStream inputStream = getClass().getResourceAsStream(CSV_PATH);
        if (inputStream == null) {
            throw new IllegalStateException("CSV 파일을 찾을 수 없습니다. path=" + CSV_PATH);
        }

        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private Product toProduct(String line) {
        String[] tokens = line.split(CSV_DELIMITER);

        String name = tokens[0];
        int price = Integer.parseInt(tokens[1]);
        int quantity = Integer.parseInt(tokens[2]);
        String promotion = tokens[3];

        return new Product(name, price, quantity, promotion);
    }
}
