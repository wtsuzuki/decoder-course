package com.ead.course.validation;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {

  @Autowired
  @Qualifier("defaultValidator")
  private Validator validator;

  final AuthUserClient authUserClient;

  public CourseValidator(AuthUserClient authUserClient) {
    this.authUserClient = authUserClient;
  }


  @Override
  public boolean supports(Class<?> clazz) {
    return false;
  }

  @Override
  public void validate(Object target, Errors errors) {
    CourseDto courseDto = (CourseDto) target;
    validator.validate(courseDto, errors);
    if(!errors.hasErrors()) {
      validateUserInstructor(courseDto.userInstructor(), errors);
    }
  }

  private void validateUserInstructor(UUID userInstructor, Errors errors) {
    ResponseEntity<UserDto> responseUserInstructor;
    try {
      responseUserInstructor = authUserClient.getOneUserById(userInstructor);
      if(responseUserInstructor.getBody().userType().equals(UserType.STUDENT)) {
        errors.rejectValue("userInstructor", "UserInstructorError", "User must be INSTRUCTOR ou ADMIN");
      }
    } catch (HttpStatusCodeException e) {
      if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found");
      }
    }
  }
}
