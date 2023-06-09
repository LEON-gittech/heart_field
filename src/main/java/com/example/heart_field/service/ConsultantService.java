package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.dto.consultant.ConsultantsDto;
import com.example.heart_field.dto.consultant.binding.SupervisorBinding;
import com.example.heart_field.dto.consultant.comment.CommentDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.entity.Consultant;

import java.util.List;

public interface ConsultantService extends IService<Consultant> {
    void resetDailyProperties();
    List<Integer> getWorkArrangement(Consultant consultant);
    List<SupervisorBinding> getSupervisorBindings(Consultant consultant);
    List<CommentDto> getCommentDto(Integer consultantId, Integer page, Integer pageSize, Integer pageNum);
    CommentsDto getCommentsDto(CommentsDto commentsDto ,Integer consultantId, Integer page, Integer pageSize);
    List<Consultant> getConsultantsWrapper(String searchValue, Integer sort, Integer sortType, Integer page, Integer pageSize, ConsultantsDto consultantsDto);
    //ResultInfo createChat(Integer , Integer , Integer );
    //ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
}
