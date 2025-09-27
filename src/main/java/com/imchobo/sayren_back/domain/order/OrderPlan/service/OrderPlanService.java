package com.imchobo.sayren_back.domain.order.OrderPlan.service;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanRequestDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;

import java.util.List;

public interface OrderPlanService {
  OrderPlanResponseDTO create(OrderPlanRequestDTO dto);
  OrderPlanResponseDTO update(Long id, OrderPlanRequestDTO dto);
  void delete(Long id);
  OrderPlanResponseDTO getById(Long id);
  List<OrderPlanResponseDTO> getAll();
}
