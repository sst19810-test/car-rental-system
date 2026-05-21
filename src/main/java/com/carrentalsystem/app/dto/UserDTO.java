package com.carrentalsystem.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
}
