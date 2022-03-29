package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean saveActivity(Activity activity);

    PagenationVo<Activity> pageList(Map<String, Object> map);

    boolean deleteActivity(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean updateActivity(Activity activity);

    Activity getDetail(String id);

    List<ActivityRemark> getRemarkListByActivityId(String activityId);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark aRemark);

    boolean updateRemark(ActivityRemark aRemark);

    List<Activity> getActivityListByName(String name);

}
