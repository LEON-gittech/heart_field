package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.HelpDTO;
import com.example.heart_field.entity.Help;

import java.util.List;


/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:21 AM
 */

public interface HelpService extends IService<Help> {
    List<HelpDTO> queryRecords(String searchValue, int pageSize, int pageNum, String fromDate, String toDate);

    Integer addHelp(Integer chatId) throws Exception;
}