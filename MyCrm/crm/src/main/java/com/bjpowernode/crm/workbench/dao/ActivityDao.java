package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int saveActivity(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int deleteActivity(String[] ids);

    Activity getActivityById(String id);

    int updateActivity(Activity activity);

    Activity getDetailById(String id);

    List<Activity> getActivityByClueId(String clueId);

    List<Activity> getAListNotLinkedByClueId(Map<String, String> map);

    List<Activity> getAListByName(String name);

}
