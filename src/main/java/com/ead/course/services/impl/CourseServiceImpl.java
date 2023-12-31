package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
  final CourseRepository courseRepository;
  final ModuleRepository moduleRepository;
  final LessonRepository lessonRepository;
  final CourseUserRepository courseUserRepository;

  public CourseServiceImpl(CourseRepository courseRepository, ModuleRepository moduleRepository, LessonRepository lessonRepository, CourseUserRepository courseUserRepository) {
    this.courseRepository = courseRepository;
    this.moduleRepository = moduleRepository;
    this.lessonRepository = lessonRepository;
    this.courseUserRepository = courseUserRepository;
  }

  @Transactional
  @Override
  public void delete(CourseModel courseModel) {
    List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
    if (!moduleModelList.isEmpty()) {
      moduleModelList.forEach(module -> {
        List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
        if (!lessonModelList.isEmpty()) {
          lessonRepository.deleteAll(lessonModelList);
        }
      });
      moduleRepository.deleteAll(moduleModelList);
    }
    List<CourseUserModel> courseUserModelList = courseUserRepository.findAllCourseUserIntoCourse(courseModel.getCourseId());
    if(!courseUserModelList.isEmpty()) {
      courseUserRepository.deleteAll(courseUserModelList);
    }
    courseRepository.delete(courseModel);
  }

  @Override
  public CourseModel save(CourseModel courseModel) {
    return courseRepository.save(courseModel);
  }

  @Override
  public Optional<CourseModel> findById(UUID courseId) {
    return courseRepository.findById(courseId);
  }

  @Override
  public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
    return courseRepository.findAll(spec, pageable);
  }

}
