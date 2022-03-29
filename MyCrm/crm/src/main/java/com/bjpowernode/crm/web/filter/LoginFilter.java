package com.bjpowernode.crm.web.filter;


import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        User user = (User)request.getSession().getAttribute("user");

        String path = request.getServletPath();

        //不应该被拦截的资源应该自动放行
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            chain.doFilter(request,response);
        }else{
            //其余的资源都需要拦截验证
            //如果session中得到的user不为null，说明登录过
            if(user!=null){
                chain.doFilter(req,resp);
            }else{
            /*
               复习重定向和转发的路径怎么写

               在实际项目开发中，对于路径的使用，不论操作的是前端还是后端，应该一律使用绝对路径
                  转发的路径写法：
                      使用的是一种特殊的绝对路径的使用用方式，这种绝对路径前面不加 /项目名 ,
                      这种路径也称之为内部路径
                      如：/login.jsp

                 重定向中路径的写法：
                      使用的是传统的绝对路径的写法，前面必须以 /项目名 开头，后面跟的是具体的资源路径
                      /crm/login.jsp
             */
                //没有登录过，则重定向到登录页面  request.getContextPath()动态获取了  /项目名
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }

    @Override
    public void destroy() {

    }
}
