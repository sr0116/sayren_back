package com.imchobo.sayren_back.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;



public class ImageUtil {

  public static byte[] convertUrlToWebpBytes(String imageUrl) throws Exception {
    // 1. 원본 이미지 읽기
    BufferedImage originalImage;
    try (InputStream in = new URL(imageUrl).openStream()) {
      originalImage = ImageIO.read(in);
    }

    if (originalImage == null) {
      throw new IllegalArgumentException("⚠️ 이미지 로드 실패: " + imageUrl);
    }

    // 2. WebP Writer 준비
    Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
    if (!writers.hasNext()) {
      throw new IllegalStateException("⚠️ WebP ImageWriter 없음 - webp-imageio 의존성 확인 필요");
    }
    ImageWriter writer = writers.next();

    // 3. 변환 수행
    byte[] webpBytes;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {

      writer.setOutput(ios);

      ImageWriteParam param = writer.getDefaultWriteParam();
      if (param.canWriteCompressed()) {
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        // ✅ compressionType 먼저 지정
        String[] compressionTypes = param.getCompressionTypes();
        if (compressionTypes != null && compressionTypes.length > 0) {
          param.setCompressionType(compressionTypes[0]); // 보통 "Lossy"
        }

        param.setCompressionQuality(0.9f); // 90% 품질 (100%는 용량 너무 클 수 있음)
      }

      // ✅ 실제 쓰기
      writer.write(null, new IIOImage(originalImage, null, null), param);

      // ✅ flush 해서 버퍼 비우기
      ios.flush();
      writer.dispose();

      webpBytes = baos.toByteArray();
    }

    // ✅ 변환 결과 검증
    if (webpBytes == null || webpBytes.length == 0) {
      throw new RuntimeException("⚠️ WebP 변환 결과가 비어있음: " + imageUrl);
    }

    return webpBytes;
  }
}
