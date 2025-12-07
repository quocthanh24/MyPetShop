package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Appointment.CreateAppointmentReq;
import com.thanhluu.tlcn.DTO.request.Appointment.UpdateAppointmentReq;
import com.thanhluu.tlcn.DTO.response.Appointment.AppointmentResp;
import com.thanhluu.tlcn.Entity.AppointmentEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AppointmentMapper {

  @Mapping(source = "employee.fullName", target = "acceptedBy")
  @Mapping(source = "customer", target = "customer", qualifiedByName = "toCustomerInfo")
  AppointmentResp toDTO(AppointmentEntity entity);
  AppointmentEntity toEntity(CreateAppointmentReq request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "appointmentTime", target = "appointmentTime")
  void updateEntityFromRequest(UpdateAppointmentReq request, @MappingTarget AppointmentEntity entity);
}
