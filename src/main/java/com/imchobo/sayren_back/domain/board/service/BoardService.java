package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.product.dto.ProductCreateRequestDTO;

public interface BoardService {

    void registerProductBoard(ProductCreateRequestDTO dto);
    void deleteProductBoard(Long productId);
}
