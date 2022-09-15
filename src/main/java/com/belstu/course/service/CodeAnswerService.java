package com.belstu.course.service;

import com.belstu.course.dto.taskProgress.CodeAnswerDto;
import com.belstu.course.model.CodeAnswer;
import com.belstu.course.model.TaskProgress;
import com.belstu.course.repository.CodeAnswerRepository;
import com.belstu.course.repository.TaskProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeAnswerService {
    private final CodeAnswerRepository codeAnswerRepository;
    private final TaskProgressRepository taskProgressRepository;

    public CodeAnswerDto getByTaskProgressId(Long taskProgressId) throws Exception {
        CodeAnswer codeAnswer = this.codeAnswerRepository.findByTaskProgressId(taskProgressId).orElseThrow(() ->
                new Exception(String.format("Ответа с кодом с id=%s не существует", taskProgressId)));
        return CodeAnswerDto.builder()
                .id(codeAnswer.id)
                .taskProgressId(taskProgressId)
                .repositoryLink(codeAnswer.getRepositoryLink())
                .teacherComment(codeAnswer.getTeacherComment())
                .build();

    }

    public void create(Long taskProgressId, CodeAnswerDto codeAnswerDto) throws Exception {
        TaskProgress taskProgress = this.taskProgressRepository.findById(taskProgressId).orElseThrow(() ->
                new Exception(String.format("Прогресса с id=%s не существует", codeAnswerDto.getTaskProgressId())));
        codeAnswerRepository.save(CodeAnswer.builder()
                .taskProgress(taskProgress)
                .repositoryLink(codeAnswerDto.getRepositoryLink())
                .build());
    }

    public void edit(CodeAnswerDto codeAnswerDto) throws Exception {
        CodeAnswer codeAnswer = this.codeAnswerRepository.findById(codeAnswerDto.getId()).orElseThrow(() ->
                new Exception(String.format("Ответа с кодом c id=%s не существует", codeAnswerDto.getId())));
        codeAnswer.setRepositoryLink(codeAnswerDto.getRepositoryLink());
        codeAnswer.setTeacherComment(codeAnswerDto.getTeacherComment());
        codeAnswerRepository.save(codeAnswer);
    }
}
