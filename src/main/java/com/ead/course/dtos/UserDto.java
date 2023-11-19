package com.ead.course.dtos;

import com.ead.course.enums.UserStatus;
import com.ead.course.enums.UserType;

import java.util.UUID;

public record UserDto(
  UUID userId,
  String username,
  String email,
  String fullName,
  UserStatus userStatus,
  UserType userType,
  String phoneNumber,
  String cpf,
  String imageUrl
) {
}
