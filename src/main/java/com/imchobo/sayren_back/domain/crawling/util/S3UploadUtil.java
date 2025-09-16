package com.imchobo.sayren_back.domain.crawling.util;

import com.imchobo.sayren_back.domain.attach.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class S3UploadUtil {
  @Autowired
  private AttachRepository attachRepository;
  private final S3Client s3Client;
  private final String bucket;

  public S3UploadUtil(
          @Value("${cloud.aws.credentials.accessKey}") String accessKey,
          @Value("${cloud.aws.credentials.secretKey}") String secretKey,
          @Value("${cloud.aws.region.static}") String region,
          @Value("${cloud.aws.s3.bucket}") String bucket
  ) {
    this.bucket = bucket;
    this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    )
            )
            .build();
  }

  public String upload(String imageUrl) throws Exception {
    LocalDate today = LocalDate.now(); // 오늘 날짜
    String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    UUID uuid = UUID.randomUUID();

    String s3Key = datePath + "/" + uuid + ".webp";

    s3Client.putObject(
            PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType("image/webp")   // MIME 타입 지정
                    .build(),
            RequestBody.fromBytes(ImageUtil.convertUrlToWebpBytes(imageUrl))   // 메모리에서 바로 업로드
    );

    return s3Key;
  }


  public String getFullUrl(String s3Key) {
    return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + s3Key;
  }

}
