package com.mallsystem.mall.product.config;

import com.mallsystem.mall.product.entity.Product;
import com.mallsystem.mall.product.entity.ProductStatus;
import com.mallsystem.mall.product.repository.ProductRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(0)
public class ProductDataInitializer implements ApplicationRunner {

    private static final List<SeedProduct> PRODUCTS = List.of(
            new SeedProduct("COCA-330ML", "可口可乐 330ml", "经典原味碳酸饮料", "3.50", 100),
            new SeedProduct("ORANGE-500ML", "橙味汽水 500ml", "清爽橙味汽水", "4.00", 80),
            new SeedProduct("WATER-550ML", "矿泉水 550ml", "日常饮用矿泉水", "2.00", 200),
            new SeedProduct("CHIPS-ORIGINAL", "原味薯片", "酥脆原味薯片", "8.50", 60),
            new SeedProduct("CHOCO-CLASSIC", "经典巧克力", "醇厚可可巧克力", "12.00", 50));

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        PRODUCTS.forEach(seed -> productRepository.findByProductCode(seed.productCode())
                .orElseGet(() -> productRepository.save(new Product(
                        seed.productCode(),
                        seed.name(),
                        seed.description(),
                        new BigDecimal(seed.price()),
                        seed.stock(),
                        ProductStatus.ACTIVE))));
    }

    private record SeedProduct(String productCode, String name, String description, String price, int stock) {
    }
}
