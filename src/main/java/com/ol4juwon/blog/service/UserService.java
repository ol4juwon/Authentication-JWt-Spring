package com.ol4juwon.blog.service;

import java.util.List;
import static java.lang.String.format;
import javax.validation.ValidationException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ol4juwon.blog.domain.dto.CreateUserRequest;
import com.ol4juwon.blog.domain.dto.Page;
import com.ol4juwon.blog.domain.dto.SearchUsersQuery;
import com.ol4juwon.blog.domain.dto.UpdateUserRequest;
import com.ol4juwon.blog.domain.dto.UserView;
import com.ol4juwon.blog.domain.mapper.UserEditMapper;
import com.ol4juwon.blog.domain.mapper.UserViewMapper;
import com.ol4juwon.blog.domain.model.User;
import com.ol4juwon.blog.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final UserEditMapper userEditMapper;
    @Autowired
    private final UserViewMapper userViewMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserView create(CreateUserRequest request) {
        if (userRepo.findByUsername(request.username()).isPresent()) {
            throw new ValidationException("Username exists!");
        }
        if (!request.password().equals(request.rePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        var user = userEditMapper.create(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        user = userRepo.save(user);

        return userViewMapper.toUserView(user);
    }

    @Transactional
    public UserView update(ObjectId id, UpdateUserRequest request) {
        var user = userRepo.getById(id);
        userEditMapper.update(request, user);

        user = userRepo.save(user);

        return userViewMapper.toUserView(user);
    }

    @Transactional
    public UserView upsert(CreateUserRequest request) {
        var optionalUser = userRepo.findByUsername(request.username());

        if (optionalUser.isEmpty()) {
            return create(request);
        } else {
            UpdateUserRequest updateUserRequest = new UpdateUserRequest(request.fullName(), request.authorities());
            return update(optionalUser.get().getId(), updateUserRequest);
        }
    }

    @Transactional
    public UserView delete(ObjectId id) {
        var user = userRepo.getById(id);

        user.setUsername(
                user.getUsername().replace("@", String.format("_%s@", user.getId().toString())));
        user.setEnabled(false);
        user = userRepo.save(user);

        return userViewMapper.toUserView(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                format("User with username - %s, not found", username)));
    }

    public boolean usernameExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public UserView getUser(ObjectId id) {
        return userViewMapper.toUserView(userRepo.getById(id));
    }

    public List<UserView> searchUsers(Page page, SearchUsersQuery query) {
        List<User> users = userRepo.searchUsers(page, query);
        return userViewMapper.toUserView(users);
    }
}
