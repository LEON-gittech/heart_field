package com.example.heart_field.dto.consultant;

import com.example.heart_field.dto.consultant.binding.SupervisorBinding;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantDto{
    private List<ExpertiseTag> expertiseTag;
    private String consultantAvatar;
    private String id;
    private String briefIntroduction;
    private String consultantName;
    private Integer consultState;
    private Integer helpCount;
    private Integer consultTotalCount;
    private List<SupervisorBinding> supervisorBindings;
    private List<Integer> workArrangement;
    private Integer consultTotalTime;
    private Integer averageRank;
    private String phone;
}