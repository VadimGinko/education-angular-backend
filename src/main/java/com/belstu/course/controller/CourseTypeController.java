package com.belstu.course.controller;

import com.belstu.course.dto.courseType.CourseTypeDto;
import com.belstu.course.service.CourseTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-types/")
public class CourseTypeController {
    private final CourseTypeService courseTypeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CourseTypeDto>> findAllCourseTypes() {
        return new ResponseEntity<>(courseTypeService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("isAuthenticated()")
    public CourseTypeDto findCourseTypeById(@PathVariable Long id) throws Exception {
        return courseTypeService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void createCourseType(@Valid @RequestBody CourseTypeDto courseTypeDto) {
        log.info("Course type with name {} added. {}", courseTypeDto.getName(), LocalDate.now());
        courseTypeService.create(courseTypeDto);
    }

    @PutMapping(value = "{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void updateCourseType(@Valid @RequestBody CourseTypeDto courseTypeDto, @PathVariable Long id) throws Exception {
        log.info("Course type with id {} updated. {}", id, LocalDate.now());
        courseTypeService.edit(id, courseTypeDto);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void removeCourseTypeById(@PathVariable Long id) throws Exception {
        log.info("Course type with id {} deleted. {}", id, LocalDate.now());
        courseTypeService.remove(id);
    }
}
