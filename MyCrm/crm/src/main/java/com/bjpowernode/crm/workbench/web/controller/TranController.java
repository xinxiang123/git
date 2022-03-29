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
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易控制器");
        String path = request.getServletPath();

        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if("/workbench/transaction/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/transaction/getClueListByFullName.do".equals(path)){
            getClueListByFullName(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/saveTran.do".equals(path)){
            saveTran(request,response);
        }else if("/workbench/transaction/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/transaction/detail.do".equals(path)){
            getTranDetail(request,response);
        }else if("/workbench/transaction/getTranHistoryList.do".equals(path)){
            getHistoryListByTranId(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(path)){
            getChats(request,response);
        }
    }

    private void getChats(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易阶段统计图表的数据");

        TranService ts = (TranService)ServiceFactory.getService(new TranServiceImpl());

        /*
           业务层为我们返回 total,dataList
           通过map打包以上两项进行返回
         */
        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response,map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行修改阶段的操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        System.out.println("stage=============="+stage);
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);

        Map<String,String> pMap = (Map)this.getServletContext().getAttribute("pMap");
        tran.setPossibility(pMap.get(stage));
        System.out.println("possibilityu==========="+pMap.get(stage));
        TranService ts = (TranService)ServiceFactory.getService(new TranServiceImpl());

        boolean flage = ts.changeStage(tran);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flage);
        map.put("tran",tran);
        PrintJson.printJsonObj(response,map);
    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> tranHistoryList  = ts.getHistoryByTranId(tranId);

        ServletContext application = this.getServletContext();
        Map<String,String> map = (Map)application.getAttribute("pMap");
        //遍历交易历史，获取阶段，得到可能性
        for(TranHistory th : tranHistoryList){
           String stage = th.getStage();
           String possibility = map.get(stage);
           th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void getTranDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran tran = ts.getTranDetail(id);
        /*
          处理可能性
          阶段 stage
          阶段和可能性之间的对应关系pMap

         */
        String stage = tran.getStage();

        //获取全局域中的pMap
        ServletContext application = this.getServletContext();
        Map<String,String> map = (Map)application.getAttribute("pMap");
        String possibility = map.get(stage);
        request.setAttribute("tran",tran);
        request.setAttribute("possibility",possibility);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        int pageSize = Integer.valueOf(request.getParameter("pageSize"));
        int pageNo = Integer.valueOf(request.getParameter("pageNo"));

        int skipCount = (pageNo - 1)*pageSize;
        TranService tranService = (TranService)ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        PagenationVo<Tran> vo = tranService.pageList(map);

        PrintJson.printJsonObj(response,vo);
    }

    private void saveTran(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("保存交易，并创建交易历史，如果客户不存在，新建客户");

        User user = (User)request.getSession().getAttribute("user");
        String id = UUIDUtil.getUUID();

        //owner这里是name，我们要的事id,等会要到user表中取
        String owner = request.getParameter("owner");


        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        //从前端获取到的客户名称，如果客户存在，查出其id保存到tran表中，如果不存在，新建客户，保存期id到突然表中
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");


        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");


        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();

        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.saveTran(tran,customerName);
        if(flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得客户名称列表");

        String name = request.getParameter("name");

        CustomerService customerService = (CustomerService)ServiceFactory.getService(new CustomerServiceImpl());

        List<String> nameList = customerService.getCustomerName(name);

        PrintJson.printJsonObj(response,nameList);
    }

    private void getClueListByFullName(HttpServletRequest request, HttpServletResponse response) {

        String fullname = request.getParameter("fullname");

        TranService cs = (TranService)ServiceFactory.getService(new TranServiceImpl());

        List<Clue> clueList = cs.getClueListByFullName(fullname);

        PrintJson.printJsonObj(response,clueList);
    }


    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("通过市场活动名获取市场活动列表操作");

        String name = request.getParameter("name");

        ActivityService ac = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = ac.getActivityListByName(name);

        PrintJson.printJsonObj(response,activityList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
