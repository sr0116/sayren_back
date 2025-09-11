package com.kiylab.croling.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3UploadTest {
  @Autowired
  private S3Upload s3Upload;

  @Test
  void testUploadFromUrl() throws Exception {
    // 🔗 테스트할 이미지 URL (jpg, png 등 아무거나)
    String testImageUrl = "https://www.lge.co.kr/kr/usp_dcr/air-conditioner/2025/25_Tower1/02.smart-air-conditioning/25_Tower1_9s_Smart-air_Cover.jpg";

    // 업로드 실행
    s3Upload.upload(testImageUrl);

    // 콘솔에 업로드 성공 여부 출력
    System.out.println("✅ 업로드 테스트 완료: " + testImageUrl);
    // 실제로는 AWS S3 콘솔에서 확인하거나, 업로드 메서드가 URL 리턴하도록 수정하면 더 깔끔함
  }
}
