package com.ead.course.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_COURSE_USERS")
public class CourseUserModel implements Serializable {
  private static final long serialVersionID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private CourseModel course;
  @Column(nullable = false)
  private UUID userId;

}
