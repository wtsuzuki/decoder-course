package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

  final AuthUserClient authUserClient;
  final CourseService courseService;
  final CourseUserService courseUserService;

  public CourseUserController(AuthUserClient authUserClient, CourseService courseService, CourseUserService courseUserService) {
    this.authUserClient = authUserClient;
    this.courseService = courseService;
    this.courseUserService = courseUserService;
  }

  @GetMapping("/course/{courseId}/users")
  public ResponseEntity<Page<UserDto>> getAllUserByCourses(
          @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
          @PathVariable(value = "courseId") UUID courseId
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllCoursesByUser(courseId, pageable));
  }

  @PostMapping("/course/{courseId}/users/subscription")
  public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                             @RequestBody @Valid SubscriptionDto subscriptionDto) {
    ResponseEntity<UserDto> responseUser;
    Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
    if (!courseModelOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
    }
    if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.userId())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
    }
    try {
      responseUser = authUserClient.getOneUserById(subscriptionDto.userId());
      if(responseUser.getBody().userStatus().equals(UserStatus.BLOCKED)) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked.");
      }
    } catch (HttpStatusCodeException e) {
      if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
      }
    }
    var courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseModel(subscriptionDto.userId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
  }
}
