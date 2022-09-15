package com.belstu.course.controller;

import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.jwt.JwtUser;
import com.belstu.course.service.CourseService;
import com.belstu.course.service.SubscriptionService;
import com.belstu.course.service.TaskProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses/")
public class CourseController {
    private final CourseService courseService;
    private final TaskProgressService taskProgressService;
    private final SubscriptionService subscriptionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void createCourse(@Valid @RequestBody CourseDto courseDto, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        log.info("Course with name {} added. {}", courseDto.getName(), LocalDate.now());
        courseService.create(courseDto, user.getUsername());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public void updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDto courseDto) throws Exception {
        log.info("Course with id {} edited. {}", id, LocalDate.now());
        courseService.edit(courseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CourseDto getByCourseId(@PathVariable Long id) throws Exception {
        return courseService.findById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public List<CourseDto> getAllCourses() {
        return courseService.findAll();
    }

    @GetMapping("/teacher-courses")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public List<CourseDto> getTeacherCourses(Authentication authentication) {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        return courseService.getTeacherCourses(user.id());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void removeCourseById(@PathVariable Long id) throws Exception {
        courseService.removeById(id);
    }

    @DeleteMapping("/progress/{courseId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public void removeProgress(@PathVariable Long courseId, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();

        taskProgressService.remove(courseId, user.id());

        subscriptionService.remove(user.id(), courseId);
        subscriptionService.create(user.id(), courseId);
    }
}
