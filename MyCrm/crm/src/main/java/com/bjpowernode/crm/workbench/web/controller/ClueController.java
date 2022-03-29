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
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进人线索模块");
        String path  = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(path)){
              getUserList(request,response);
        }else if("/workbench/clue/saveClue.do".equals(path)){
            saveClue(request,response);
        }else if("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            getDetail(request,response);
        }else  if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            gteActivityListByClueId(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/clue/getAListNotLinkedClueId.do".equals(path)){
            getAListNotLinkedClueId(request,response);
        }else if("/workbench/clue/bundClueAndActivity.do".equals(path)){
            bundClueAndActivity(request,response);
        }else if("/workbench/clue/getAListByName.do".equals(path)){
            getAListByName(request,response);
        }else if("/workbench/clue/covert.do".equals(path)){
            covert(request,response);
        }
    }

    private void covert(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String clueId = request.getParameter("clueId");
        Tran t = null;

        User user = (User)request.getSession().getAttribute("user");
        System.out.println("user=========="+user);
        String createBy = user.getName();
        /*
          如果前端点击了为客户创建交易信息，那么会在表单中以post方式提交/workbench/clue/covert.do这个请求
          、如果没有点击创建交易信息，那么则会直接以get方式提交/workbench/clue/covert.do这个请求
         */
        String method = request.getMethod();
        if("POST".equals(method)){
            //以post方式请求，表示点击了为客户创建交易，执行为客户创建交易
            t = new Tran();
            //接收表单中的参数信息
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }

        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImpl());
        /*
          为业务层传递的参数，
          1、必须传递的参数clueId,有了这个clueId之后我们才知道要转换哪条记录
          2、必须传递的参数t,因为在线索转换的过程中，有可能会临时创建一笔交易（业务层接收的t也可能为null）
          3、
         */
        boolean flag = cs.convert(clueId,t,createBy);

        if(flag){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
        //以get方式请求，表示没有点击创建交易，不用执行创建交易的操作

    }

    private void getAListByName(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("name");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<Activity> activityList = cs.getAListByName(name);
        PrintJson.printJsonObj(response,activityList);
    }

    private void bundClueAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行管来呢市场活动操作");

        String clueId = request.getParameter("clueId");
        String[] aids = request.getParameterValues("aid");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bundClueAndActivity(clueId,aids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getAListNotLinkedClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加市场信息关联操作(根据名称模糊差，排除掉已经关联的市场活动列表)");
        String clueId = request.getParameter("clueId");
        String activityName = request.getParameter("activityName");

        Map<String,String> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<Activity> activityList = cs.getAListNotLinkedByClueId(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行解除关联的操作");
        String id = request.getParameter("id");
        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.undbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void gteActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示线索关联的市场活动信息");
        String clueId = request.getParameter("clueId");
        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImpl());
        List<Activity> activityList = cs.getActivityByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void getDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("打开线索信息操作");
        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImpl());
        String id = request.getParameter("id");
        Clue clue = cs.getDetail(id);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String owner = request.getParameter("owner");
        String source = request.getParameter("source");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");

        int pageSize = Integer.valueOf(pageSizeStr);
        int pageNo = Integer.valueOf(pageNoStr);

        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("mphone",mphone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("state",state);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImpl());
        PagenationVo<Clue> vo = cs.pageList(map);
        PrintJson.printJsonObj(response,vo);

    }

    private void saveClue(HttpServletRequest request, HttpServletResponse response) {

      User user = (User)request.getSession().getAttribute("user");
      String id = UUIDUtil.getUUID();
      String fullname = request.getParameter("fullname");
      String appellation = request.getParameter("appellation");
      String owner = request.getParameter("owner");
      String company = request.getParameter("company");
      String job = request.getParameter("job");
      String email = request.getParameter("email");
      String phone = request.getParameter("phone");
      String website = request.getParameter("website");
      String mphone = request.getParameter("mphone");
      String state = request.getParameter("state");
      String source = request.getParameter("source");
      String createBy = user.getName();
      String createTime = DateTimeUtil.getSysTime();
      String description = request.getParameter("description");
      String contactSummary = request.getParameter("contactSummary");
      String nextContactTime = request.getParameter("nextContactTime");
      String address = request.getParameter("address");

      Clue c = new Clue();
      c.setId(id);
      c.setFullname(fullname);
      c.setAppellation(appellation);
      c.setOwner(owner);
      c.setCompany(company);
      c.setJob(job);
      c.setEmail(email);
      c.setPhone(phone);
      c.setMphone(mphone);
      c.setWebsite(website);
      c.setCreateBy(createBy);
      c.setAddress(address);
      c.setState(state);
      c.setSource(source);
      c.setContactSummary(contactSummary);
      c.setCreateTime(createTime);
      c.setDescription(description);
      c.setNextContactTime(nextContactTime);

        ClueService clueService = (ClueService)ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = clueService.saveClue(c);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("线索模块中，取得用户信息列表");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = us.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
