package com.imchobo.sayren_back.repository.product;

import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@SpringBootTest
// @DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testProduct () {

        // 1. 상품 생성
        Product product = Product.builder()
                .name("상품 테스트 정수기")
                .price(2000000)
                .description("상품 상세 설명")
                .modelName("TEST-001")
                .productCategory("정수기")
                .build();

        // 2. 태그 추가
        ProductTag tag1 = ProductTag.builder()
                .tagName("사용면적")
                .tagValue("35㎡")
                .product(product)
                .build();

        ProductTag tag2 = ProductTag.builder()
                .tagName("가습량")
                .tagValue("500cc")
                .product(product)
                .build();

        product.getTags().add(tag1);
        product.getTags().add(tag2);

        // 3. 재고 추가
        ProductStock stock = ProductStock.builder()
                .stock(10)
                .product(product)
                .build();

        product.getStocks().add(stock);

        // 4. 저장 (CascadeType.ALL 때문에 태그/재고도 함께 저장)
        productRepository.save(product);

        // 5. 다시 조회
        Product found = productRepository.findById(product.getProductId())
                .orElseThrow();

        System.out.println("상품명: " + found.getName());
        System.out.println("가격: " + found.getPrice());

        List<ProductTag> foundTags = found.getTags();
        foundTags.forEach(t ->
                System.out.println("태그: " + t.getTagName() + " = " + t.getTagValue())
        );

        List<ProductStock> foundStocks = found.getStocks();
        foundStocks.forEach(s ->
                System.out.println("재고: " + s.getStock())
        );
    }
}
