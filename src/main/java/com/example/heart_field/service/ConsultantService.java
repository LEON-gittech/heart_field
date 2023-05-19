package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.dto.CommentDto;
import com.example.heart_field.dto.SupervisorBinding;
import com.example.heart_field.entity.Consultant;

import java.util.List;

public interface ConsultantService extends IService<Consultant> {
    public void resetDailyProperties();
    public List<Integer> getWorkArrangement(Consultant consultant);
    public List<SupervisorBinding> getSupervisorBindings(Consultant consultant);
    public List<CommentDto> getCommentDto(Integer consultantId, Integer page, Integer pageSize);
}
