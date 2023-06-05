package com.example.heart_field.dto.consultant;

import com.example.heart_field.annotation.Password;
import lombok.Data;

@Data
public class PasswordDto{
    @Password
    String password;
}