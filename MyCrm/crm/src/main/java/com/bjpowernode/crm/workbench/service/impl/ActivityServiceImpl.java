package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean saveActivity(Activity activity) {

        boolean flag = true;

        int count = activityDao.saveActivity(activity);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PagenationVo<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        //将total和dataList封装到vo中
        PagenationVo<Activity> vo = new PagenationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    @Override
    public boolean deleteActivity(String[] ids) {

        boolean flag = true;
        /*
           市场活动表与市场活动备注表关联，市场活动表中的id是备注表中的外键，所以要删除市场活动表中的数据
           需要先删除与该数据关联的备注表中的数据

           删除步骤：
           1）查询出需要删除的备注的数量
           2）删除备注，返回收到影响的条数（实际的删除数量）
           3）删除市场活动
         */
        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByIds(ids);

        //删除备注，返回受到影响的条数（实际的删除备注数量）
        int count2 = activityRemarkDao.deleteByIds(ids);

        if(count1 != count2){
            flag = false;
        }

        //删除市场活动
        int count3 = activityDao.deleteActivity(ids);
        if(count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap<>();

        //取得userList，这里用到了userDao
        List<User> userList = userDao.getUserList();
        //取得activity
        Activity activity = activityDao.getActivityById(id);
        //将userList和activity打包到map中
        map.put("userList",userList);
        map.put("activity",activity);
        //返回map就可以了
        return map;
    }

    @Override
    public boolean updateActivity(Activity activity) {

        boolean flag = false;
        int count = activityDao.updateActivity(activity);
        if(count == 1)
            flag = true;
        return flag;
    }

    @Override
    public Activity getDetail(String id) {
        /*
          这里不能复用activityDao中的getById的方法，因为所有者owner在getById方法中返回的是一段字符串
          我们需要的是一个名称
         */
        Activity activity = activityDao.getDetailById(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByActivityId(String activityId) {

        List<ActivityRemark> activityRemarkList = activityRemarkDao.getRemarkListByActivityId(activityId);
        return activityRemarkList;
    }

    @Override
    public boolean deleteRemark(String id) {

        boolean flag = false;
        int count = activityRemarkDao.deleteRemark(id);
        if(count==1){
            flag=true;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark aRemark) {

        boolean flag = true;

        int count=activityRemarkDao.saveRemark(aRemark);

        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark aRemark) {
        boolean flag = true;

        int count=activityRemarkDao.updateRemark(aRemark);

        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByName(String name) {
        List<Activity> activityList = activityDao.getAListByName(name);
        return activityList;
    }


}
