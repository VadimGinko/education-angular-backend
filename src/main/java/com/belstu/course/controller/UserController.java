package com.belstu.course.controller;

import com.belstu.course.dto.LoginRequestDto;
import com.belstu.course.dto.LoginResponseDto;
import com.belstu.course.dto.RegistrationRequestDto;
import com.belstu.course.dto.ReviewDto;
import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.dto.subscription.SubscriptionDto;
import com.belstu.course.dto.user.ChangeUserStatusDto;
import com.belstu.course.dto.user.RoleRequestDto;
import com.belstu.course.dto.user.UserDto;
import com.belstu.course.jwt.JwtTokenProvider;
import com.belstu.course.jwt.JwtUser;
import com.belstu.course.model.User;
import com.belstu.course.model.enums.CourseStatus;
import com.belstu.course.model.enums.UserStatus;
import com.belstu.course.service.CourseService;
import com.belstu.course.service.SubscriptionService;
import com.belstu.course.service.TaskProgressService;
import com.belstu.course.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final CourseService courseService;
    private final TaskProgressService taskProgressService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                  HttpServletResponse response) throws Exception {
        String email = loginRequestDto.email();
        User user = userService.getByEmail(email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequestDto.password()));

        if (user.getStatus().equals(UserStatus.IN_ACTIVE)) {
            throw new Exception("Ваш аккаунт заблокирован");
        }
        if (user.getStatus().equals(UserStatus.EMAIL_NOT_CONFIRMED)) {
            throw new Exception("Почта не подтверждена");
        }

        response.addCookie(jwtTokenProvider.getCookieWithAccessToken(user));

        log.info("User with email {} login. {}", email, LocalDate.now());
        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                new RoleRequestDto(user.getRole().getName()),
                user.getFirstName(),
                user.getLastName(),
                user.getDescription(),
                user.getLink());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register")
    public void register(@Valid @RequestBody RegistrationRequestDto user) {
        userService.isUserRegistered(user.email());
        userService.register(user);
        log.info("User with email {} successfully registered. {}", user.email(), LocalDate.now());
    }

    @GetMapping(value = "/register/confirm")
    public void confirm(@RequestParam("token") String token, HttpServletResponse response) throws Exception {
        userService.confirmToken(token);
        response.sendRedirect("http://172.16.0.221:8071/login?confirmed=true");
    }

    @GetMapping(value = "/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public UserDto getUserById(@PathVariable Long id) throws Exception {
        return userService.getById(id);
    }

    @PutMapping(value = "/status")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void changeUserStatus(@Valid @RequestBody ChangeUserStatusDto userDto) throws Exception {
        userService.changeStatus(userDto);
        log.info("User {} status changed on {}. {}", userDto.email(), userDto.status(), LocalDate.now());
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public UserDto getUserByEmail(@RequestParam String email) throws Exception {
        return this.userService.getDtoByEmail(email);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public UserDto updateUserInfo(@Valid @RequestBody UserDto user, @PathVariable Long id) throws Exception {
        UserDto userFromDb = userService.editInfo(id, user);
        log.info("User with id {} successfully edited. {}", id, LocalDate.now());
        return userFromDb;
    }

    @PutMapping(value = "/{id}/password")
    @PreAuthorize("isAuthenticated()")
    public UserDto updatePassword(@Valid @RequestBody String password, @PathVariable Long id) throws Exception {
        UserDto userFromDb = userService.editPassword(id, password);
        log.info("User password with id {} successfully edited. {}", id, LocalDate.now());
        return userFromDb;
    }

    @GetMapping(value = "/{id}/courses")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<CourseDto> getTeacherCourses(@PathVariable Long id) {
        return courseService.getTeacherCourses(id);
    }

    @GetMapping(value = "/courses/{id}/review")
    public List<ReviewDto> getTasksForReview(@PathVariable Long id, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        return taskProgressService.getTaskToReview(id, user.id());
    }

    @GetMapping(value = "/subscribed-courses")
    @PreAuthorize("hasAnyAuthority('USER')")
    public List<CourseDto> getSubscribedCourses(Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        return subscriptionService.getSubscribedCourses(user.id());
    }

    @GetMapping("/unsubscribed-courses")
    @PreAuthorize("hasAnyAuthority('USER')")
    public List<CourseDto> getUnSubscribedCoursesByFilters(
            @RequestParam CourseStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            Authentication authentication) {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        return courseService.findByFilters(user.id(), status, name, type);
    }

    @PostMapping(value = "/subscriptions")
    @PreAuthorize("hasAnyAuthority('USER')")
    public void subscribe(@Valid @RequestBody CourseDto courseDto, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        subscriptionService.create(user.id(), courseDto.getId());
        log.info("User {} subscribe to course {}. {}", user.email(), courseDto.getName(), LocalDate.now());
    }

    @PutMapping(value = "/subscriptions")
    @PreAuthorize("hasAnyAuthority('USER', 'TEACHER')")
    public void updateSubscription(@Valid @RequestBody SubscriptionDto subscriptionDto, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        subscriptionService.update(user.id(), subscriptionDto);
    }

    @GetMapping(value = "/subscriptions")
    @PreAuthorize("hasAnyAuthority('USER')")
    public SubscriptionDto getSubscriptionByCourseId(@RequestParam Long courseId, Authentication authentication) throws Exception {
        JwtUser user = (JwtUser) authentication.getPrincipal();
        return subscriptionService.getByCourseAndUserId(user.id(), courseId);
    }
}
