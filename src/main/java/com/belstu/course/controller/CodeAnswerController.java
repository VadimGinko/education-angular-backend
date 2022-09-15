package com.belstu.course.controller;

import com.belstu.course.dto.taskProgress.CodeAnswerDto;
import com.belstu.course.service.CodeAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task-progress")
public class CodeAnswerController {
    private final CodeAnswerService codeAnswerService;

    @PutMapping("/code")
    @PreAuthorize("hasAnyAuthority('USER', 'TEACHER')")
    public void editCodeAnswer(@Valid @RequestBody CodeAnswerDto codeAnswerDto) throws Exception {
        codeAnswerService.edit(codeAnswerDto);
    }
}
