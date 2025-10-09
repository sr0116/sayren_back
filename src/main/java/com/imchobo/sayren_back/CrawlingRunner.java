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
        File flagFile = new File("crawling_done.flag"); // 플래그 파일

        // 이미 실행된 적 있다면 스킵
        if (flagFile.exists()) {
            System.out.println("이미 크롤링이 완료되어 있습니다. 건너뜁니다.");
            return;
        }

        System.out.println("크롤링 시작!");
        crawlingService.crawlCategory("https://www.lge.co.kr/category/stan-by-me");
        System.out.println("크롤링 완료!");

        // 플래그 파일 생성 (한 번만 실행되도록)
        flagFile.createNewFile();
    }
}
