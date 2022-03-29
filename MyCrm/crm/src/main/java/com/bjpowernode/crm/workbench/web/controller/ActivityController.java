package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.PrintJson;
import com.bjpowernode.crm.util.ServiceFactory;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进入市场活动控制器");

        String path = req.getServletPath();

        if("/workbench/activity/getUserList.do".equals(path)){
             getUserList(req,resp);
        }else if("/workbench/activity/saveActivity.do".equals(path)){
             saveActivity(req,resp);
        }else if("/workbench/activity/pageList.do".equals(path)){
             pageList(req,resp);
        }else if("/workbench/activity/deleteActivity.do".equals(path)){
            deleteActivity(req,resp);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(req,resp);
        }else if("/workbench/activity/updateActivity.do".equals(path)){
            updateActivity(req,resp);
        }else if("/workbench/activity/detail.do".equals(path)){
            getDetail(req,resp);
        }else if("/workbench/activity/getRemarkListByActivityId.do".equals(path)){
            getRemarkListByActivityId(req,resp);
        }else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(req,resp);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(req,resp);
        }else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(req,resp);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行修改备注的操作");
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        User user = (User)request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editFlag = "1";
        ActivityRemark aRemark = new ActivityRemark();
        aRemark.setId(id);
        aRemark.setNoteContent(noteContent);
        aRemark.setEditTime(editTime);
        aRemark.setEditBy(editBy);
        aRemark.setEditFlag(editFlag);

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.updateRemark(aRemark);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("aRemark",aRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行保存备注信息的操作");

        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        User user = (User)request.getSession().getAttribute("user");
        String createBy = user.getName();
        String editFlag = "0";

        ActivityRemark aRemark = new ActivityRemark();
        aRemark.setId(id);
        aRemark.setCreateTime(createTime);
        aRemark.setCreateBy(createBy);
        aRemark.setActivityId(activityId);
        aRemark.setNoteContent(noteContent);
        aRemark.setEditFlag(editFlag);

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(aRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("aRemark",aRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始删除备注操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByActivityId(HttpServletRequest requset, HttpServletResponse response) {
        System.out.println("进入加载备注信息列表操作");

        String activityId = requset.getParameter("activityId");

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> activityRemarkList = as.getRemarkListByActivityId(activityId);

        PrintJson.printJsonObj(response,activityRemarkList);
    }

    private void getDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到跳转到详细信息也的界面");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = as.getDetail(id);

        request.setAttribute("activity",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入修改操作市场活动操作");
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String cost = request.getParameter("cost");
        String name = request.getParameter("name");
        String endDate = request.getParameter("endDate");
        String startDate = request.getParameter("startDate");
        String description = request.getParameter("description");

        //修改人：当前登录的用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        //修改时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();

        ActivityService as  = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = new Activity();

        activity.setOwner(owner);
        activity.setId(id);
        activity.setStartDate(startDate);
        activity.setCost(cost);
        activity.setName(name);
        activity.setDescription(description);
        activity.setEndDate(endDate);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        boolean flag = as.updateActivity(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest requset, HttpServletResponse response) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");

        String id = requset.getParameter("id");
        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        /*
           总结：
              controller调用service的方法，返回值是什么，就要从service层去什么

              前端需要的，向业务层去要（这里要 userList activity）

              在控制层考虑将取得的信息封装为什么传给前端

              由于以上两项信息复用率不改，我们选择使用map打包这两项信息
         */
        Map<String,Object> map = as.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteActivity(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("执行到删除市场活动信息的操作");
        String[] ids = req.getParameterValues("id");

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteActivity(ids);

        PrintJson.printJsonFlag(resp,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询，分页查询）");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);

        /*
            mysql数据库中的分页查询
              select * from student limit 5,3
            5表示查询结果中前面略过的记录数，3表示当前结果中展现的记录数

            即在结果中略过前面5条记录，展现第6到8条记录
         */
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
           前端要：市场活动信息列表，查询的总条数

           业务层拿到两项信息之后，如何做返回？
           1）使用map
           2）使用vo

           将来分页查询，每个模块都有分页，所以我们选择使用一个通用的Vo，操作起来比较方便
           定义一个通用的Vo类
           以后这个T中传的是哪个对象，这个就是哪个对象的Vo
           属性List集合中也就是该对象的List集合
           public class PagenationalVo<T>{
              private int total;
              private List<T> dataList;
           }
           PagenationalVo<Activity> vo = new Pagenational<>();
           vo.setTatal(total);
           vo.setDateList(data); //这里的data就是Activity对象
         */
        PagenationVo<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);
    }

    private void getUserList(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("取得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();
        PrintJson.printJsonObj(resp,userList);
    }

    private void saveActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动的添加操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录的用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        boolean flag = as.saveActivity(activity);
        PrintJson.printJsonFlag(response,flag);
    }

}
