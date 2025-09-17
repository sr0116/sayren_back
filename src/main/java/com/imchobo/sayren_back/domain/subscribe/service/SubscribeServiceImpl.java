package com.imchobo.sayren_back.domain.subscribe.service;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 생성에 필요
public class SubscribeServiceImpl implements SubscribeService {

  // DB 접근
//  private final SubscribeRepository subscribeRepository;
//  // 맵스트럭 사용해서 엔티티 디티오 변환
//  private final SubscribeMapper subscribeMapper;
//
//
//  // 구독 신청 (구독 플랜으로 결제 완료시에만 생성되게 변경)
//  @Override
//  @Transactional
//  public SubscribeResponseDTO create(SubscribeRequestDTO dto) {
//// 엔티티 변환
//    Subscribe entity = subscribeMapper.toEntity(dto); // 이미 앞에서 기본 상태 줌
//    entity.setStatus(SubscribeStatus.PREPARING); // 결제 완료 시에 바로 상태 변경 (결제 전-> 일단 준비중으로)
//    // db 저장
//    Subscribe saved = subscribeRepository.save(entity);
//    return subscribeMapper.toResponseDTO(saved);
//  }
//
//  // 구독 완료 상태로 변경
//
//
//  @Override
//  @Transactional
//  public void activateAfterDelivery(Long subscribeId) {
//    Subscribe entity = subscribeRepository.findById(subscribeId).orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));
//   if(!entity.getStatus().equals(SubscribeStatus.PREPARING)){
//     throw new IllegalStateException("배송 중 상태가 아니므로 활성화할 수 없습니다.");
//   }
//    entity.setStatus(SubscribeStatus.ACTIVE);
//    subscribeRepository.save(entity);
//  }
//
//  // 구독 단건 조회
//  @Override
//  public SubscribeResponseDTO getById(Long subscribeId) {
//    Subscribe entity = subscribeRepository.findById(subscribeId).orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));
//
//    return subscribeMapper.toResponseDTO(entity);
//  }
//
//  // 구독 전체 조회
//  @Override
//  public List<SubscribeResponseDTO> getAll() {
//    List<Subscribe> entities = subscribeRepository.findAll();
//    return subscribeMapper.toResponseDTOList(entities);
//  }
//
//  // 구독 마이페이지 조회
//  @Override
//  public List<SubscribeSummaryDTO> getSummaryList() {
//    List<Subscribe> entities = subscribeRepository.findAll();
//    return subscribeMapper.toSummaryDTOList(entities);
//  }
//
//  // 구독 상태 변경 (공용 메서드)
//  @Override
//  @Transactional
//  public void updateStatus(Long subscribeId, SubscribeStatus status) {
//    Subscribe entity = subscribeRepository.findById(subscribeId)
//            .orElseThrow(() -> new RuntimeException("구독을 찾을 수 없습니다."));
//    entity.setStatus(status);
//
//    subscribeRepository.save(entity);
//
//  }
}
