package com.thinkitdevit.demospringjwt.controller;

import com.thinkitdevit.demospringjwt.model.ERole;
import com.thinkitdevit.demospringjwt.model.Role;
import com.thinkitdevit.demospringjwt.model.User;
import com.thinkitdevit.demospringjwt.payload.request.LoginRequest;
import com.thinkitdevit.demospringjwt.payload.request.SignupRequest;
import com.thinkitdevit.demospringjwt.payload.response.JwtResponse;
import com.thinkitdevit.demospringjwt.payload.response.MessageResponse;
import com.thinkitdevit.demospringjwt.repository.RoleRepository;
import com.thinkitdevit.demospringjwt.repository.UserRepository;
import com.thinkitdevit.demospringjwt.security.jwt.JwtUtils;
import com.thinkitdevit.demospringjwt.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {


        private final AuthenticationManager authenticationManager;


        private final UserRepository userRepository;


        private final RoleRepository roleRepository;


        private final PasswordEncoder passwordEncoder;


        private final JwtUtils jwtUtils;

        public AuthController(AuthenticationManager authenticationManager,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtils jwtUtils) {
            this.authenticationManager = authenticationManager;
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtUtils = jwtUtils;
        }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authentication(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(jwtToken , userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest){

        if(userRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username already exists"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email already exists"));
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        if(strRoles == null){
            strRoles = Set.of(ERole.ROLE_USER.getLabel());
        } else if(strRoles.isEmpty()){
            strRoles.add(ERole.ROLE_USER.getLabel());
        }

        Set<Role> roles = strRoles.stream().map(role -> retrieveRole(role))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered"));
    }

    private Role retrieveRole(String userRole){
        return roleRepository.findByName(ERole.retrieveByLabel(userRole))
                .orElseThrow(() -> new RuntimeException("Error: Role "+userRole+" not found"));
    }

}