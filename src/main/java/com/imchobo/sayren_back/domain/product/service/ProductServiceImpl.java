package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.attach.repository.BoardAttachRepository;
import com.imchobo.sayren_back.domain.attach.repository.ProductAttachRepository;
import com.imchobo.sayren_back.domain.board.dto.CategoryResponseDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.repository.BoardRepository;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;
import com.imchobo.sayren_back.domain.board.service.CategoryService;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.common.en.CommonStatus;
import com.imchobo.sayren_back.domain.common.util.NextUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.product.dto.*;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.mapper.ProductMapper;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductStockRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import com.imchobo.sayren_back.domain.term.en.TermType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final RedisUtil redisUtil;
  private final ProductStockRepository productStockRepository;
  private final ProductTagRepository productTagRepository;
  private final ProductAttachRepository productAttachRepository;
  private final ProductMapper productMapper;
  private final BoardRepository boardRepository;
  private final NextUtil nextUtil;
  private final CategoryRepository categoryRepository;
  private final BoardAttachRepository boardAttachRepository;
  private final CategoryService categoryService;


  private Long calcDeposit(Long price) {
    // 보증금: 원가의 20%
    return Math.round(price * 0.2);
  }

  private Long calcRentalPrice(Long price, Integer month) {
    if (month == null || month == 0) return price;

    // 원가에서 보증금을 뺀 금액을 개월 수로 나눔
    long deposit = calcDeposit(price);
    long base = Math.round((price - deposit) / (double) month);

    // 개월 수에 따른 장기 계약 혜택 적용
    if (month == 36) {
      return base - 500;   // 36개월 계약 시 월 500원 할인
    } else if (month == 48) {
      return base - 1000;  // 48개월 계약 시 월 1000원 할인
    }

    return base; // 24개월은 그대로
  }


//  @Override
//  @EventListener(ApplicationReadyEvent.class)
//  public void preloadProducts() {
//    List<ProductListResponseDTO> list = getAllProducts(null, null);
//    redisUtil.setObject("PRODUCTS", list);
//  }


  @Override
  @Transactional
  public List<ProductListResponseDTO> getAllProducts() {
    List<ProductListResponseDTO> productList = new ArrayList<>();

    List<Board> boardList = boardRepository.findAll().stream().filter(board -> {
      return (board.getProduct() != null && board.getCategory().getType().equals(CategoryType.PRODUCT) && board.getCategory().getParentCategory() != null);
    }).toList();

    boardList.forEach(board -> {
      Product product = board.getProduct();
      Hibernate.initialize(product);
      List<Attach> attachList = productAttachRepository.findByProductId(product.getId()).stream().filter(Attach::isThumbnail).toList();
      Attach attach = !attachList.isEmpty() ? attachList.getFirst() : null;
      String thumbnailUrl = attach != null ? "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/" + attach.getPath() + "/" + attach.getUuid() : null;
      List<String> tags = productTagRepository.findByProductId(product.getId()).stream().map((t) -> {return t.getTagName() + "#" + t.getTagValue();}).toList();

      productList.add(ProductListResponseDTO.builder()
                      .productId(product.getId())
                      .thumbnailUrl(thumbnailUrl)
                      .tags(tags)
                      .status(board.getStatus())
                      .productName(product.getName())
                      .category(board.getCategory().getName())
                      .modelName(product.getModelName())
                      .price(product.getPrice())
              .build());
    });

    return productList;
  }


  @Override
  public ProductDetailsResponseDTO getProductById(Long id) {
    return productRepository.findById(id)
            .map(p -> new ProductDetailsResponseDTO(
                    p.getId(),
                    // thumbnail
                    productAttachRepository.findByProductIdAndIsThumbnailTrue(p.getId())
                            .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                    + a.getPath() + "/" + a.getUuid())
                            .orElse(null),
                    p.getName(),
                    p.getDescription() != null ? p.getDescription() : "",
                    p.getPrice().intValue(),
                    p.getIsUse(),
                    p.getProductCategory(),
                    p.getModelName(),
                    p.getRegDate(),
                    // stock
                    productStockRepository.findByProductId(p.getId())
                            .map(ProductStock::getStock).orElse(0),
                    // 태그
                    productTagRepository.findByProductId(p.getId()).stream()
                            .map(ProductTag::getTagValue)
                            .toList(),
                    // attach
                    productAttachRepository.findByProductId(p.getId()).stream()
                            .map(a -> ProductAttachResponseDTO.builder()
                                    .attachId(a.getId())
                                    .url("https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                            + a.getPath() + "/" + a.getUuid())
                                    .build()
                            )
                            .toList(),
                    // order
                    p.getOrderItems().stream()
                            .map(item -> item.getOrderPlan().getType().name())
                            .distinct()
                            .toList(),
                    calcDeposit(p.getPrice()),
                    calcRentalPrice(p.getPrice(), 24)
            ))
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
  }

//  @Override
//  public List<ProductPendingDTO> getPendingProducts() {
//    return productRepository.findByIsUseFalse()
//            .stream()
//            .map(p -> ProductPendingDTO.builder()
//                    .productId(p.getId())
//                    .productName(p.getName())
//                    .modelName(p.getModelName())
//                    .productCategory(p.getProductCategory())
//                    .isUse(p.getIsUse())
//                    .build())
//            .toList();
//  }
//  // 등록 승인대기 상품
//  @Override
//  public List<ProductPendingDTO> getApprovedProducts() {
//    return productRepository.findByIsUseTrue()
//            .stream()
//            .map(p -> ProductPendingDTO.builder()
//                    .productId(p.getId())
//                    .productName(p.getName())
//                    .modelName(p.getModelName())
//                    .productCategory(p.getProductCategory())
//                    .isUse(p.getIsUse())
//                    .build())
//            .toList();
//  }

  @Override
  public void useProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
    product.setIsUse(true); // 승인 처리
    productRepository.save(product);
  }

  // 등록 승인 대기 상품
  @Override
  public PageResponseDTO<ProductPendingDTO, Product> getPendingProducts(PageRequestDTO pageRequestDTO) {
    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
    Page<Product> result = productRepository.findByIsUseFalse(pageable);

    return PageResponseDTO.of(result, product -> ProductPendingDTO.builder()
            .productId(product.getId())
            .productName(product.getName())
            .modelName(product.getModelName())
            .productCategory(product.getProductCategory())
            .isUse(product.getIsUse())
            .thumbnailUrl(
                    productAttachRepository.findByProductIdAndIsThumbnailTrue(product.getId())
                            .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                    + a.getPath() + "/" + a.getUuid())
                            .orElse(null))
            .build()
    );
  }


  // 삭제상품 목록
  @Override
  public PageResponseDTO<ProductPendingDTO, Product> getApprovedProducts(PageRequestDTO pageRequestDTO) {
    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
    Page<Product> result = productRepository.findByIsUseTrue(pageable);

    return PageResponseDTO.of(result, product -> ProductPendingDTO.builder()
            .productId(product.getId())
            .productName(product.getName())
            .modelName(product.getModelName())
            .productCategory(product.getProductCategory())
            .isUse(product.getIsUse())
            .thumbnailUrl(
                    productAttachRepository.findByProductIdAndIsThumbnailTrue(product.getId())
                            .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                    + a.getPath() + "/" + a.getUuid())
                            .orElse(null))
            .build());
  }

  // 관리자 상품 삭제(비활성화) 페이지네이션
  @Override
  @Transactional
  public void cancelUseProduct(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
    product.setIsUse(false); // false = 비활성화 상태
    productRepository.save(product);
  }

  // 상품 승인대기 상세보기
  @Override
  @Transactional(readOnly = true)
  public ProductDetailsResponseDTO getProductDetailForAdmin(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

    return ProductDetailsResponseDTO.builder()
            .productId(product.getId())
            .thumbnailUrl(
                    productAttachRepository.findByProductIdAndIsThumbnailTrue(product.getId())
                    .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                            + a.getPath() + "/" + a.getUuid())
                    .orElse(null))
            .productName(product.getName())
            .description(product.getDescription() != null ? product.getDescription() : "")
            .price(product.getPrice().intValue())
            .isUse(product.getIsUse())
            .productCategory(product.getProductCategory())
            .modelName(product.getModelName())
            .regDate(product.getRegDate())
            // 재고
            .productStock(productStockRepository.findByProductId(product.getId())
                    .map(ProductStock::getStock)
                    .orElse(0))
            // 태그
            .productTags(productTagRepository.findByProductId(product.getId()).stream()
                    .map(ProductTag::getTagValue)
                    .toList())
            // 첨부파일
            .attachList(productAttachRepository.findByProductId(product.getId()).stream()
                    .map(a -> ProductAttachResponseDTO.builder()
                            .attachId(a.getId())
                            .url("https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                    + a.getPath() + "/" + a.getUuid())
                            .build()
                    )
                    .toList())
            // 주문 플랜 타입
            .planTypes(product.getOrderItems().stream()
                    .map(item -> item.getOrderPlan().getType().name())
                    .distinct()
                    .toList())
            // 보증금, 렌탈가
            .deposit(calcDeposit(product.getPrice()))
            .rentalPrice(calcRentalPrice(product.getPrice(), 24))
            .build();
  }


  // 상품 승인 전 수정
  @Override
  @Transactional
  public void modifyProduct(Long id, ProductModifyRequestDTO dto) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

    // 이름
    if (dto.getProductName() != null && !dto.getProductName().isBlank()) {
      product.setName(dto.getProductName());
    }

    // 설명
    if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
      product.setDescription(dto.getDescription());
    }

    // 가격
    if (dto.getPrice() != null && dto.getPrice() > 0) {
      product.setPrice(dto.getPrice().longValue());
    }

    // 카테고리
    if (dto.getProductCategory() != null && !dto.getProductCategory().isBlank()) {
      product.setProductCategory(dto.getProductCategory());
    }

    // 모델명
    if (dto.getModelName() != null && !dto.getModelName().isBlank()) {
      product.setModelName(dto.getModelName());
    }

    // 판매 여부 (isUse)
    if (dto.getIsUse() != null) {
      product.setIsUse(dto.getIsUse());
    }
  }

  // 큐레이션
  @Override
  public Page<ProductListResponseDTO> getFilteredProducts(ProductListResponseDTO filter, Pageable pageable) {

    String keyword = filter.getProductName();
    String category = filter.getCategory();
    String sort = "latest"; // 기본값

    Page<Product> products = productRepository.searchByFilter(keyword, category, sort, pageable);

    return products.map(productMapper::toListDTO);
  }

  @Override
  public void revalidate(Long id) {
    nextUtil.revalidatePaths(List.of("/api/product/" + id, "/product/" + id, "/rental/" + id));
  }

  @Override
  public void revalidateAll() {
    nextUtil.revalidatePaths(List.of("/api/product", "/product",  "/rental"));
  }


  @Override
  @Transactional
  public void registerProductBoard(ProductCreateRequestDTO dto) {
    log.info("dto값" + dto);

    Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

    log.info("상품 조회성공" + product.getId());

    // 상품 노출 상태 업데이트
    product.setIsUse(true);

    // 카테고리 조회
    Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

    log.info("카테고리 조회성공" + category.getId());

    // 게시글 등록
    Board board = Board.builder()
            .product(product)
            .title(product.getName())
            .content(product.getDescription())
            .category(category)
            .isSecret(false)
            .status(CommonStatus.ACTIVE)
            .build();

    boardRepository.save(board);
  }

  @Override
  @Transactional
  public Long registerProduct(ProductCreateRequestDTO dto) {

    // 상품 저장
    Product product = Product.builder()
            .name(dto.getProductName())
            .description(dto.getDescription())
            .price(dto.getPrice().longValue())
            .isUse(false) // 기본 비노출 상태
            .productCategory(dto.getProductCategory())
            .modelName(dto.getModelName())
            .build();

    productRepository.save(product);

    // 재고 기본값 (9999로 고정)
    ProductStock stock = ProductStock.builder()
            .product(product)
            .stock(dto.getStock()) // ← 이건 지유쨩 버전 유지
            .build();
    productStockRepository.save(stock);

    // 태그 등록
    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      dto.getTags().forEach((tagName, tagValue) -> {
        ProductTag tag = ProductTag.builder()
                .product(product)
                .tagName(tagName)
                .tagValue(tagValue)
                .build();
        productTagRepository.save(tag);
      });
    }

    // 첨부파일 등록 (썸네일 + 일반)
    if (dto.getAttach() != null) {
      Attach thumb = Attach.builder()
              .uuid(dto.getAttach().getUuid())
              .path(dto.getAttach().getPath())
              .isThumbnail(true)
              .build();
      productAttachRepository.save(thumb);
    }

    if (dto.getAttachList() != null && !dto.getAttachList().isEmpty()) {
      dto.getAttachList().forEach(a -> {
        Attach attach = Attach.builder()
                .uuid(a.getUuid())
                .path(a.getPath())
                .isThumbnail(false)
                .build();
        productAttachRepository.save(attach);
      });
    }

    return product.getId();
  }

  @Override
  public List<CategoryResponseDTO> getProductCategories() {
    List<Category> categories = categoryService.findProductCategories();
    return categories.stream()
            .map(c -> new CategoryResponseDTO(
                    c.getId(),
                    c.getParentCategory() != null ? c.getParentCategory().getId() : null,
                    c.getName(),
                    c.getType()))
            .collect(Collectors.toList());
  }

  // 관리자 상품리스트 페이지
  @Override
  @Transactional(readOnly = true)
  public List<ProductListResponseDTO> getAllProductsForAdmin() {
    List<Product> products = productRepository.findAll();

    return products.stream().map(p -> {
      // 썸네일 URL
      String thumbnailUrl = productAttachRepository.findByProductIdAndIsThumbnailTrue(p.getId())
              .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                      + a.getPath() + "/" + a.getUuid())
              .orElse(null);

      // 태그
      List<String> tags = productTagRepository.findByProductId(p.getId())
              .stream()
              .map(tag -> tag.getTagName() + "#" + tag.getTagValue())
              .toList();

      return ProductListResponseDTO.builder()
              .productId(p.getId())
              .thumbnailUrl(thumbnailUrl)
              .tags(tags)
              .status(p.getIsUse() ? CommonStatus.ACTIVE : CommonStatus.DISABLED)// 상태표시 추가
              .productName(p.getName())
              .category(p.getProductCategory())              // 카테고리명 그대로 표시
              .modelName(p.getModelName())
              .price(p.getPrice())
              .build();
    }).toList();
  }
}
