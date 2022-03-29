package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.util.MD5Util;
import com.bjpowernode.crm.util.PrintJson;
import com.bjpowernode.crm.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器");
        String path = request.getServletPath();
        if("/settings/user/login.do".equals(path)){
             login(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到验证登录的操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文转为MD5的秘闻形式
        loginPwd = MD5Util.getMD5(loginPwd);

        //接收浏览器端的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("ip="+ip);

        //未来的业务层开发，统一使用代理类形态的接口对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        try{
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            //如果程序执行到此处，说明业务层没有为controller抛出任何异常
            //表示登录成功,在PrintJson中调用printJsonFlag这个方法，将封装的信息转为json数据发给ajax请求
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();
            //一旦执行了catch块中的信息，说明业务层为我们验证登录失败，为controller抛出了异常
            /*
               {"success": false,"msg":?}
             */
            String msg = e.getMessage();
            /*
               我们在作为controller，需要ajax请求提供多项信息

               可以有两种手段来处理：
                 1）将多项信心打包成一个map，将map解析未json串
                 2）创建一个Vo（类似于实体类，里面定义了你需要传递的属性）

                这个两种手段的使用场景：
                   如果对于展现的信息将来还会大量使用，我们就创建一个Vo类，使用方便
                   如果对于展现的信息只在这个需求中使用，我们使用map就可以了
             */
            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }
    }
}
