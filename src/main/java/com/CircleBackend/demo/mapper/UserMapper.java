package com.CircleBackend.demo.mapper;

import com.CircleBackend.demo.dto.UserWalletResDto;
import com.CircleBackend.demo.entities.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserWalletResDto userWalletResDto);

    UserWalletResDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserWalletResDto userWalletResDto, @MappingTarget User user);
}