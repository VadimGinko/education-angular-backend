package com.belstu.course.controller;

import com.belstu.course.dto.taskProgress.CodeAnswerDto;
import com.belstu.course.dto.taskProgress.TaskProgressDto;
import com.belstu.course.jwt.JwtUser;
import com.belstu.course.model.enums.ProgressStatus;
import com.belstu.course.service.CodeAnswerService;
import com.belstu.course.service.TaskProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task-progress")
public class TaskProgressController {
    private final TaskProgressService taskProgressService;
    private final CodeAnswerService codeAnswerService;

    @PutMapping
    @PreAuthorize("hasAnyAuthority('USER', 'TEACHER')")
    public void updateTaskProgress(@Valid @RequestBody TaskProgressDto taskProgressDto, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        taskProgressDto.setUserId(user.id());
        taskProgressService.changeStatus(taskProgressDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'TEACHER')")
    public void updateTaskProgressByProgressId(@PathVariable Long id, @Valid @RequestBody TaskProgressDto taskProgressDto) throws Exception {
        taskProgressDto.setId(id);
        taskProgressService.changeStatusByProgressId(taskProgressDto);
    }

    @GetMapping("/{courseId}/p")
    public Double getPercent(@PathVariable(required = false) Long courseId, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        List<TaskProgressDto> progress = taskProgressService.getByCourseId(courseId, user.id());
        double count = (double) progress.stream()
                .filter(Objects::nonNull)
                .filter(i -> i.getStatus() == ProgressStatus.COMPLETE)
                .count();
        return count / progress.stream()
                .filter(Objects::nonNull).count();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TaskProgressDto> getTaskProgressByFilters(@RequestParam(required = false) Long taskId,
                                                          @RequestParam(required = false) Long courseId, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        if (courseId != null)
            return taskProgressService.getByCourseId2(courseId, user.id());
        else if (taskId != null)
            return List.of(taskProgressService.getByUserIdAndTaskId(user.id(), taskId));
        else
            return List.of();
    }

    @GetMapping("{id}/code")
    @PreAuthorize("isAuthenticated()")
    public CodeAnswerDto getCodeAnswerByProgressId(@PathVariable Long id) throws Exception {
        return codeAnswerService.getByTaskProgressId(id);
    }

    @PostMapping("/{id}/code")
    @PreAuthorize("hasAnyAuthority('USER')")
    public void addCodeAnswer(@PathVariable Long id, @Valid @RequestBody CodeAnswerDto codeAnswerDto) throws Exception {
        codeAnswerService.create(id, codeAnswerDto);
    }
}
