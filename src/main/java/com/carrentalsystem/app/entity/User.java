package com.carrentalsystem.app.entity;

import com.carrentalsystem.app.helper.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50)
    private String name;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 15)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    public User(String admin, String mail, String admin123, String number, Role role) {
        this.name = admin;
        this.email = mail;
        this.password = admin123;
        this.phone = number;
        this.role = role;
    }
}
