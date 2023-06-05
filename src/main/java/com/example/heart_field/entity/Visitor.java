package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.heart_field.dto.VisitorPcychDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Visitor {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private Byte isDisabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String username;
    private String name;
    private String phone;
    private String emergencyName;
    private String emergencyPhone;
    private String avatar;
    private String direction;
    private String puzzle;
    private String history;
    private String question;
    private Byte gender;//0女1男2未知
    private String openId;


    public VisitorPcychDTO convertToVisitorPcychDTO() {
        List<Integer> questions = Arrays.stream(this.question.split(", ")).map(Integer::parseInt).collect(Collectors.toList());
        return VisitorPcychDTO.builder()
                .id(this.id)
                .direction(this.direction)
                .puzzle(this.puzzle)
                .history(this.history)
                .question(questions)
                .build();
    }
}