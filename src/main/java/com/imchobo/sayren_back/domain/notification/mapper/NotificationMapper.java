package com.imchobo.sayren_back.domain.notification.mapper;

import com.imchobo.sayren_back.domain.notification.dto.NotificationCreateDTO;
import com.imchobo.sayren_back.domain.notification.dto.NotificationResponseDTO;
import com.imchobo.sayren_back.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


  // DTO + Member → Entity 변환 (알림 생성용)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", ignore = true)
  Notification toEntity(NotificationCreateDTO dto);

  @Mapping(source = "id", target = "notificationId")
  NotificationResponseDTO toDTO(Notification entity);

  // DTO 리스트 변환
  List<NotificationResponseDTO> toDTOList(List<Notification> entities);


}
