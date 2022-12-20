package com.ol4juwon.blog.api;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ol4juwon.blog.domain.dto.CreateUserRequest;
import com.ol4juwon.blog.domain.dto.UserView;
import com.ol4juwon.blog.domain.model.Role;
import com.ol4juwon.blog.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name="UserAdmin")
@RestController
@RequestMapping(path ="api/admin/user")
@RolesAllowed(Role.USER_ADMIN)
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @PostMapping
    public UserView create(@RequestBody @Valid CreateUserRequest request){
        System.out.println("hellooooooo");
        return userService.create(request);
    }
    
}
