package src.convenience.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import src.convenience.domain.entity.product.Product;
import src.convenience.domain.entity.product.ProductRepository;

import java.util.List;

@Component
public class CsvProductDataLoader implements ApplicationRunner {

    private final ProductRepository productRepository;

    public CsvProductDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (productRepository.count() > 0) return; // 중복 방지

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/products.csv")
                ))) {

            List<Product> products = br.lines()
                    .skip(1)
                    .map(line -> line.split(","))
                    .map(tokens -> new Product(
                            tokens[0],
                            Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            tokens.length > 3 && !tokens[3].isBlank() ? tokens[3] : null
                    ))
                    .toList();

            productRepository.saveAll(products);
        }
    }
}