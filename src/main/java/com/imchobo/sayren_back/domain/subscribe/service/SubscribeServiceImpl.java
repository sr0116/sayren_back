package com.imchobo.sayren_back.domain.subscribe.service;



import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 생성에 필요
public class SubscribeServiceImpl implements SubscribeService {

  // DB 접근
  private final SubscribeRepository subscribeRepository;
  // 맵스트럭 사용해서 엔티티 디티오 변환
  private final SubscribeMapper subscribeMapper;


  // 구독 신청
  @Override
  public SubscribeResponseDTO create(SubscribeRequestDTO dto) {
// 엔티티 변환
    Subscribe entity = subscribeMapper.toEntity(dto);

    // 기본 상태값 (결제 대기 상태)
    entity.setStatus(SubscribeStatus.PENDING_PAYMENT); // 결제 대기 _ 구독 신청 상태

    // baseEntity
    entity.setRegDate(LocalDateTime.now());
    entity.setModDate(LocalDateTime.now());

    // db 저장
  Subscribe saved = subscribeRepository.save(entity);
    return  subscribeMapper.toResponseDTO(saved);
  }
// 구독 단건 조회
  @Override
  public SubscribeResponseDTO getById(Long subscribeId) {
    Subscribe entity = subscribeRepository.findById(subscribeId).orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));

    return subscribeMapper.toResponseDTO(entity);
  }

// 구독 전체 조회
  @Override
  public List<SubscribeResponseDTO> getAll() {
    List<Subscribe> entities = subscribeRepository.findAll();
    return subscribeMapper.toResponseDTOList(entities);
  }

// 구독 마이페이지 조회
  @Override
  public List<SubscribeSummaryDTO> getSummaryList() {
    List<Subscribe> entities = subscribeRepository.findAll();
    return subscribeMapper.toSummaryDTOList(entities);
  }
// 구독 상태 변경
  @Override
  public void updateStatus(Long subscribeId, SubscribeStatus status) {
    Subscribe entity = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));
    entity.setStatus(status);
    entity.setModDate(LocalDateTime.now());

    subscribeRepository.save(entity);

  }
}
