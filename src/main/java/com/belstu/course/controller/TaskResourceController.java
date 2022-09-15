package com.belstu.course.controller;

import com.belstu.course.service.TaskResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/task-links")
public class TaskResourceController {
    private final TaskResourceService taskResourceService;

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void removeTaskLink(@PathVariable Long id) {
        log.info("Task Link with id {} deleted. {}", id, LocalDate.now());
        taskResourceService.remove(id);
    }
}
