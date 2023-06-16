package com.example.heart_field.dto.consultant;

import com.example.heart_field.annotation.Phone;
import lombok.Data;

@Data
public class PhoneDto {
    @Phone
    private String phone;
}
