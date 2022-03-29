package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean saveClue(Clue c);
    PagenationVo<Clue> pageList(Map<String, Object> map);

    Clue getDetail(String id);

    List<Activity> getActivityByClueId(String clueId);

    boolean undbund(String id);

    List<Activity> getAListNotLinkedByClueId(Map<String, String> map);

    boolean bundClueAndActivity(String clueId, String[] aids);

    List<Activity> getAListByName(String name);

    boolean convert(String clueId, Tran t, String createBy);

}
