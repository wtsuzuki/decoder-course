package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validation.CourseValidator;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {
  final CourseService courseService;
  final CourseValidator courseValidator;

  public CourseController(CourseService courseService, CourseValidator courseValidator) {
    this.courseService = courseService;
    this.courseValidator = courseValidator;
  }

  @PostMapping
  public ResponseEntity<Object> saveCourse(@RequestBody CourseDto courseDto, Errors errors) {
    log.debug("POST saveCourse courseDto received {} ", courseDto.toString());
    courseValidator.validate(courseDto, errors);
    if(errors.hasErrors()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
    }
    var courseModel = new CourseModel();
    BeanUtils.copyProperties(courseDto, courseModel);
    courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
    courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
    log.debug("POST saveCourse courseModel saved {} ", courseModel.toString());
    log.info("Course saved successfully courseId {} ", courseModel.getCourseId());
    return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(courseModel));
  }

  @DeleteMapping("/{courseId}")
  public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {
    Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
    if (courseModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
    }
    courseService.delete(courseModelOptional.get());
    return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
  }

  @PutMapping("/{courseId}")
  public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId,
                                             @RequestBody @Valid CourseDto courseDto) {
    Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
    if (courseModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
    }
    var courseModel = courseModelOptional.get();
    BeanUtils.copyProperties(courseDto, courseModel);
    courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
    return ResponseEntity.status(HttpStatus.OK).body(courseService.save(courseModel));
  }

  @GetMapping
  public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                         @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                         @RequestParam(required = false) UUID userId) {
    if (userId != null) {
      return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec), pageable));
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll(spec, pageable));
    }
  }

  @GetMapping("/{courseId}")
  public ResponseEntity<Object> getOneCourses(@PathVariable(value = "courseId") UUID courseId) {
    Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
    return courseModelOptional.
            <ResponseEntity<Object>>map(courseModel -> ResponseEntity.status(HttpStatus.OK).body(courseModel))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found."));
  }

}
