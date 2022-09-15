package com.belstu.course.service;

import com.belstu.course.dto.courseType.CourseTypeDto;
import com.belstu.course.mapper.CourseTypeMapper;
import com.belstu.course.model.CourseType;
import com.belstu.course.repository.CourseTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseTypeService {
    private final CourseTypeMapper courseTypeMapper;
    private final CourseTypeRepository courseTypeRepository;

    public void create(CourseTypeDto courseType) {
        courseTypeRepository.save(
                CourseType.builder()
                        .name(courseType.getName())
                        .build()
        );
    }

    public void edit(Long courseId, CourseTypeDto courseTypeDto) throws Exception {
        var courseType = courseTypeRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Типа курса id=%s нет в системе", courseId)));
        courseType.setName(courseTypeDto.getName());
        courseTypeRepository.save(courseType);
    }

    public void remove(Long courseId) throws Exception {
        if (!courseTypeRepository.existsById(courseId)) {
            throw new Exception(String.format("Типа курса id=%s нет в системе", courseId));
        }
        courseTypeRepository.deleteById(courseId);
    }

    public List<CourseTypeDto> findAll() {
        return courseTypeMapper.toDto(courseTypeRepository.findAll());
    }

    public CourseTypeDto findById(Long courseId) throws Exception {
        return courseTypeMapper.toDto(courseTypeRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Типа курса id=%s нет в системе", courseId))));
    }
}
