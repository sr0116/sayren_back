package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductStockRepositoryTest {
  @Autowired
  private ProductStockRepository productStockRepository;

  // 서비스에서 재고 추가 해줘서 ProductStockRepository 사용 안함

//  @Test
//  public void addstock(){
//    for(long i = 1; i <= 5; i++ ){
//      productStockRepository.save(ProductStock.builder().product(Product.builder().id(i).build()).stock((int)(Math.random() * 100)).build());
//    }
//  }
}