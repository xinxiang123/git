package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByIds(String[] ids);

    int deleteByIds(String[] ids);

    List<ActivityRemark> getRemarkListByActivityId(String activityId);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark aRemark);

    int updateRemark(ActivityRemark aRemark);
}
