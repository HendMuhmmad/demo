package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.orm.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public UserDto mapUser(User user);

    public User mapUserDto(UserDto user);

}
