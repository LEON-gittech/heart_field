package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.dto.binding.SupervisorBinding;
import com.example.heart_field.dto.consultant.ConsultantsDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.impl.ConsultantServiceImpl;

import java.util.List;

public interface ConsultantService extends IService<Consultant> {
    void resetDailyProperties();
    List<Integer> getWorkArrangement(Consultant consultant);
    List<SupervisorBinding> getSupervisorBindings(Consultant consultant);
    ConsultantServiceImpl.Comments getCommentDto(Integer consultantId, Integer page, Integer pageSize);
    CommentsDto getCommentsDto(CommentsDto commentsDto ,Integer consultantId, Integer page, Integer pageSize);
    List<Consultant> getConsultants(String searchValue, Integer sort, Integer sortType, Integer page, Integer pageSize, ConsultantsDto consultantsDto);
    //ResultInfo createChat(Integer , Integer , Integer );
    //ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
}
