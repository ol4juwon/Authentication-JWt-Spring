package com.ol4juwon.blog.api;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.Instant;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ol4juwon.blog.domain.dto.AuthRequest;
import com.ol4juwon.blog.domain.dto.CreateUserRequest;
import com.ol4juwon.blog.domain.dto.UserView;
import com.ol4juwon.blog.domain.mapper.UserViewMapper;
import com.ol4juwon.blog.domain.model.User;
import com.ol4juwon.blog.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name="Authentication")
@RestController
@RequestMapping(path = "api/public")
@RequiredArgsConstructor
public class AuthApi {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserViewMapper userViewMapper;
    private final UserService userService;
    
    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid AuthRequest request) {
      try {
        var authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
  
        var user = (User) authentication.getPrincipal();
  
        var now = Instant.now();
        var expiry = 36000L;
  
        var scope =
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(" "));
  
        var claims =
            JwtClaimsSet.builder()
                .issuer("ol4juwon.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(format("%s,%s", user.getId(), user.getUsername()))
                .claim("roles", scope)
                .build();
  
        var token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(userViewMapper.toUserView(user));
      } catch (BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    }
    @PostMapping("register")
    public UserView register(@RequestBody @Valid CreateUserRequest request) {
        System.out.println(request.toString());
      return userService.create(request);
    }
}
