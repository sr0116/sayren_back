package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachRequestDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDTO {
  private Long productId;
  private Long categoryId;
}
