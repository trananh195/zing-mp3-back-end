package com.example.du_an_tn_zingmp_3.controller;
import com.example.du_an_tn_zingmp_3.model.JwtResponse;
import com.example.du_an_tn_zingmp_3.model.Roles;
import com.example.du_an_tn_zingmp_3.model.User;
import com.example.du_an_tn_zingmp_3.service.IRoleService;
import com.example.du_an_tn_zingmp_3.service.IUserService;
import com.example.du_an_tn_zingmp_3.service.impl.JwtService;
import com.example.du_an_tn_zingmp_3.service.impl.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SendMailService sendMailService;


    @GetMapping
    public ResponseEntity<Iterable<User>> findAll(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findById(@PathVariable("id")Long id){

        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity createUser(@RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Iterable<User> users = userService.findAll();
        for (User currentUser : users) {
            if (currentUser.getUserName().equals(user.getUserName())) {
                return new ResponseEntity<>("Username existed",HttpStatus.OK);
            }
        }
        if (!userService.isCorrectConfirmPassword(user)) {
            return new ResponseEntity<>("Input confirm password",HttpStatus.OK);
        }
        if (user.getRoles() != null) {
            Roles role = roleService.findByName("ROLE_ADMIN");
            Set<Roles> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        } else {
            Roles role1 = roleService.findByName("ROLE_USER");
            Set<Roles> roles1 = new HashSet<>();
            roles1.add(role1);
            user.setRoles(roles1);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmedPassword(passwordEncoder.encode(user.getConfirmedPassword()));
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User currentUser = userService.findByUsername(user.getUserName());
        if(currentUser.isEnabled()){
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(jwt, currentUser.getId(), userDetails.getUsername(), userDetails.getAuthorities()));
        } else{
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }
    @PutMapping("/changePassword/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = this.userService.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean check = passwordEncoder.matches(user.getPassword(), userOptional.get().getPassword());
        if(check) {
            userOptional.get().setPassword(passwordEncoder.encode(user.getConfirmedPassword()));
            userService.save(userOptional.get());
            sendMailService.sendEmailUpdatePassword(userOptional.get().getEmail(), user.getConfirmedPassword());
            return new ResponseEntity<>( HttpStatus.OK);
        }else {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = this.userService.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setId(userOptional.get().getId());
//        user.setUserName(userOptional.get().getUserName());
        user.setEnabled(userOptional.get().isEnabled());
        user.setPassword(userOptional.get().getPassword());
        user.setRoles(userOptional.get().getRoles());
        user.setConfirmedPassword(userOptional.get().getConfirmedPassword());

        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @PutMapping("/account_lockout/{email}")
    public ResponseEntity<?> accountLockout(@PathVariable("email")String toEmail){
        User user = userService.findByEmail(toEmail);
        if (user.isEnabled()){
            user.setEnabled(!user.isEnabled());
            userService.save(user);
            sendMailService.sendEmail(toEmail);
        } else {
            user.setEnabled(!user.isEnabled());
            userService.save(user);
            sendMailService.sendEmailOpen(toEmail);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
