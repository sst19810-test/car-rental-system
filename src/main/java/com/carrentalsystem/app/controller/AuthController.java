package com.carrentalsystem.app.controller;

import com.carrentalsystem.app.dto.UserLoginDTO;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.helper.Role;
import com.carrentalsystem.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final UserService userService;

    // GET - Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // POST - Handle registration
    @PostMapping("/register")

    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            System.out.println("Validation errors occurred during registration.");
            return "register"; // Shows the form with validation messages
        }

        if (userService.isEmailExists(user.getEmail())) {
            model.addAttribute("emailExists", true);
            return "register"; // Show error message on same page instead of redirecting
        }

        user.setRole(Role.USER);
        userService.registerUser(user);  // Should handle password encoding
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/auth/login";
    }




    @GetMapping("/login")
    public String showUserLoginForm(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "login"; // templates/login.html
    }

    @GetMapping("/admin/login")
    public String showAdminLoginForm(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "admin/login"; // templates/admin/login.html
    }



}
