package com.belstu.course.service;

import com.belstu.course.dto.RegistrationRequestDto;
import com.belstu.course.dto.user.ChangeUserStatusDto;
import com.belstu.course.dto.user.UserDto;
import com.belstu.course.exception.RegistrationException;
import com.belstu.course.mapper.UserMapper;
import com.belstu.course.model.ConfirmationToken;
import com.belstu.course.model.Course;
import com.belstu.course.model.User;
import com.belstu.course.model.enums.CourseStatus;
import com.belstu.course.model.enums.UserStatus;
import com.belstu.course.repository.CourseRepository;
import com.belstu.course.repository.RoleRepository;
import com.belstu.course.repository.UserRepository;
import com.belstu.course.service.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final EmailBuilder emailBuilder;
    private final UserMapper userMapper;

    public User getByEmail(String email) throws Exception {
        return this.userRepository.findByEmail(email).orElseThrow(() ->
                new Exception(String.format("Пользователя с почтой %s не существует", email)));
    }

    public UserDto getById(Long id) throws Exception {
        return userMapper.toDto(this.userRepository.findById(id).orElseThrow(() ->
                new Exception(String.format("Пользователя с id=%s не существует", id))));
    }

    public UserDto getDtoByEmail(String email) throws Exception {
        return userMapper.toDto(this.userRepository.findByEmail(email).orElseThrow(() ->
                new Exception(String.format("Пользователя с почтой=%s не существует", email))));
    }

    public void isUserRegistered(String email) {
        if (userRepository.findByEmail(email).isPresent())
            throw new RegistrationException(String.format("Пользователя с почтой %s уже зарегистрирован", email));
    }

    public void register(RegistrationRequestDto userDto) {
        User user = User.builder()
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .role(this.roleRepository.findByName(userDto.role().name()))
                .status(UserStatus.EMAIL_NOT_CONFIRMED)
                .build();

        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        emailSender.send(
                user.getEmail(),
                emailBuilder.buildEmail(
                        userDto.firstName(),
                        "http://172.16.0.221:8070/api/v1/users/register/confirm?token=" + token
                )
        );
    }

    @Transactional
    public void confirmToken(String token) throws Exception {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        User user = getByEmail(confirmationToken.getUser().getEmail());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    public void changeStatus(ChangeUserStatusDto userDto) throws Exception {
        User user = this.userRepository.findByEmail(userDto.email()).orElseThrow(() ->
                new Exception(String.format("Пользователя с email=%s не существует", userDto.email())));
        List<Course> courses = courseRepository.findByPublisherId(user.id);
        if (!courses.isEmpty()) {
            courses.forEach(course -> {
                course.setStatus(CourseStatus.IN_ACTIVE);
                courseRepository.save(course);
            });
        }
        user.setStatus(userDto.status());
        userRepository.save(user);
    }

    public UserDto editInfo(Long id, UserDto userDto) throws Exception {
        User user = this.userRepository.findById(id).orElseThrow(() ->
                new Exception(String.format("Пользователя с id=%s не существует", id)));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLink(userDto.getLink());
        user.setDescription(userDto.getDescription());
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto editPassword(Long id, String password) throws Exception {
        User user = this.userRepository.findById(id).orElseThrow(() ->
                new Exception(String.format("Пользователя с id=%s не существует", id)));
        user.setPassword(passwordEncoder.encode(password));
        return userMapper.toDto(userRepository.save(user));
    }
}
