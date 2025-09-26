package com.imchobo.sayren_back.domain.order.OrderPlan.service;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanRequestDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.OrderPlan.mapper.OrderPlanMapper;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.order.OrderPlan.repository.OrderPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
//    - 요금제 등록/수정/삭제/조회 비즈니스 로직 처리
public class OrderPlanServiceImpl implements OrderPlanService {

  private final OrderPlanRepository orderPlanRepository;
  private final OrderPlanMapper orderPlanMapper;
  private final OrderItemRepository orderItemRepository;

  /**
   * 요금제 등록
   * - 같은 type + month 조합이 이미 존재하면 예외 발생
   * - 없을 경우 새로 저장
   */
  @Override
  public OrderPlanResponseDTO create(OrderPlanRequestDTO dto) {
    // DTO → Entity 변환
    OrderPlan entity = orderPlanMapper.toEntity(dto);

    // 중복 체크 (type + month)
    boolean exists = orderPlanRepository.existsByTypeAndMonth(entity.getType(), entity.getMonth());
    if (exists) {
      throw new IllegalArgumentException(
        "이미 존재하는 요금제입니다: type=" + entity.getType() + ", month=" + entity.getMonth()
      );
    }

    // 저장 후 DTO 반환
    return orderPlanMapper.toResponseDTO(orderPlanRepository.save(entity));
  }

  /**
   * 요금제 수정
   * - id 기준으로 기존 요금제 조회 후 type, month 변경
   */
  @Override
  public OrderPlanResponseDTO update(Long id, OrderPlanRequestDTO dto) {
    OrderPlan plan = orderPlanRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("요금제 없음: id=" + id));

    // 중복 체크 (수정하려는 type + month가 이미 존재하는지)
    boolean exists = orderPlanRepository.existsByTypeAndMonth(dto.getType(), dto.getMonth());
    if (exists && !(plan.getType().equals(dto.getType()) && plan.getMonth().equals(dto.getMonth()))) {
      throw new IllegalArgumentException(
        "이미 존재하는 요금제입니다: type=" + dto.getType() + ", month=" + dto.getMonth()
      );
    }

    // 값 업데이트
    plan.setType(dto.getType());
    plan.setMonth(dto.getMonth());

    return orderPlanMapper.toResponseDTO(orderPlanRepository.save(plan));
  }

  /**
   * 요금제 삭제
   * 존재 여부 확인 후 삭제 바로삭제하는게아님
   */
  @Override
  public void delete(Long id) {
    // 요금제 존재 여부 확인
    OrderPlan plan = orderPlanRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("요금제 없음: id=" + id));

    // 주문 아이템에서 참조 중인지 확인
    boolean inUse = orderItemRepository.existsByOrderPlanId(id);
    if (inUse) {
      // 주문에서 사용 중이면 삭제 불가
      throw new IllegalStateException("해당 요금제를 사용하는 주문이 존재하여 삭제할 수 없습니다.");
    }

    // 참조가 없을 때만 삭제 가능
    orderPlanRepository.deleteById(id);
  }

  /**
   * 단일 요금제 조회
   */
  @Override
  public OrderPlanResponseDTO getById(Long id) {
    return orderPlanRepository.findById(id)
      .map(orderPlanMapper::toResponseDTO)
      .orElseThrow(() -> new EntityNotFoundException("요금제 없음: id=" + id));
  }

  /**
   * 전체 요금제 목록 조회
   */
  @Override
  public List<OrderPlanResponseDTO> getAll() {
    return orderPlanMapper.toResponseDTOs(orderPlanRepository.findAll());
  }
}
