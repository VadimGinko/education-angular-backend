package com.belstu.course.controller;

import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.dto.task.TaskResourceDto;
import com.belstu.course.mapper.TaskMapper;
import com.belstu.course.model.Task;
import com.belstu.course.model.enums.ResourceType;
import com.belstu.course.model.enums.TaskType;
import com.belstu.course.service.DropboxService;
import com.belstu.course.service.FileService;
import com.belstu.course.service.TaskResourceService;
import com.belstu.course.service.TaskService;
import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskResourceService taskResourceService;
    private final FileService fileService;
    private final TaskMapper taskMapper;
    private final DropboxService dropboxService;

    @Autowired
    ResourceLoader resourceLoader;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void createTask(@Valid @RequestBody TaskDto taskDto) throws Exception {
        Task task = taskService.create(taskDto);
        log.info("Task with id {} added to course with id {}. {}", task.getId(), taskDto.getCourseId(), LocalDate.now());
    }


    @RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> provideUploadInfo(@PathVariable Long id) throws IOException, DbxException {
        var resource = taskResourceService.getById(id);
        var outputStream = dropboxService.getFile("/" + resource.getTask().getId() + "/" + resource.getContent());
        byte[] bytes = outputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getContent() + "\"")
                .body(new InputStreamResource(inputStream));
    }

    @RequestMapping(value = "{taskId}/file", method = RequestMethod.POST)
    public @ResponseBody
    void postFileUpload(@RequestParam("name") String name,
                        @RequestParam("file") MultipartFile file,
                        @PathVariable Long taskId) throws Exception {

        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        String newFileName = fileService.saveFile(name, extension, taskId.toString(), file);
        TaskResourceDto taskResourceDto = new TaskResourceDto();
        taskResourceDto.setName(name);
        taskResourceDto.setContent(newFileName + "." + extension);
        taskResourceDto.setType(ResourceType.FILE);
        taskResourceService.create(taskId, taskResourceDto);
    }

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody
    void postVideoUpload(@RequestParam("name") String fileName,
                         @RequestParam("courseId") Long courseId,
                         @RequestParam("file") MultipartFile file) throws Exception {

        String videoId = fileService.saveVideo(UUID.randomUUID().toString(), file);

        TaskDto taskDto = new TaskDto();
        taskDto.setName(fileName);
        taskDto.setContent(videoId);
        taskDto.setCourseId(courseId);
        taskDto.setType(TaskType.VIDEO);
        taskService.create(taskDto);
    }

    @RequestMapping(value = "/video/{taskId}", method = RequestMethod.PUT)
    public @ResponseBody
    void putVideoUpload(@RequestParam("name") String fileName,
                        @RequestParam("file") MultipartFile file, @PathVariable Long taskId) throws Exception {
        String videoId = fileService.saveVideo(UUID.randomUUID().toString(), file);

        TaskDto taskDto = new TaskDto();
        taskDto.setName(fileName);
        taskDto.setContent(videoId);
        taskService.edit(taskId, taskDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto) throws Exception {
        Task task = taskService.edit(id, taskDto);
        log.info("Task with id {} edited. {}", task.getId(), LocalDate.now());
    }

    @PutMapping("/update-order")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void updateOrder(@RequestBody List<TaskDto> tasks) {
        taskService.editTasksOrder(tasks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public TaskDto getTask(@PathVariable Long id) {
        return taskMapper.toDto(taskService.get(id));
    }

    @PostMapping("/{id}/task-links")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void createTaskLinks(@PathVariable Long id, @Valid @RequestBody TaskResourceDto taskResourceDto) throws Exception {
        log.info("Task link with name {} added. {}", taskResourceDto.getName(), LocalDate.now());
        taskResourceService.create(id, taskResourceDto);
    }
}
