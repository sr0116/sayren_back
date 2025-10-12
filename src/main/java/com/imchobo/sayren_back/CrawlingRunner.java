package com.imchobo.sayren_back;

import com.imchobo.sayren_back.domain.crawling.service.CrawlingService;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class CrawlingRunner implements CommandLineRunner {

    private final CrawlingService crawlingService;

    private final ProductRepository productRepository;

    private final ProductTagRepository productTagRepository;


    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("크롤링 시작!");
            crawlingService.crawlCategory("https://www.lge.co.kr/category/air-conditioners");
            System.out.println("크롤링 완료!");
        } catch (Exception e) {
            System.err.println("크롤링 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
