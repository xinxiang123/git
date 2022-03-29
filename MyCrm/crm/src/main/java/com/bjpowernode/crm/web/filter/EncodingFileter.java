package com.bjpowernode.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFileter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入过滤字符编码的过滤器");
        //过滤post请求中文参数乱码
        request.setCharacterEncoding("UTF-8");

        //过滤响应流响应中文乱码问题
        response.setContentType("text/html;charset=utf-8");

        //请求放行
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
