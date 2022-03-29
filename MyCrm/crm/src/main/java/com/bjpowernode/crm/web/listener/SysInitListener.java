package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.util.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/*
    监听什么域对象就实现什么域对象的接口，这里监听的是上下文域对象实现ServletContextListener
 */
public class SysInitListener implements ServletContextListener {

    /*
        该方法是用来监听上下文域对象的方法，当服务器启动后，上下文域对象创建
        对象创建完毕后，立马执行该方法

        event：该参数能够取得监听的对象
               监听的是什么对象，就可以通过该参数取得什么对象
               这里取得的是上下文域对象
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext application = sce.getServletContext();
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
          这里应该管业务层要7个List

          可以打包成一个map集合
          业务层应该这样保存数据
             map.put("appellationList",dvList)
             map.put("clueStateList".dvList)
             ....
         */
        Map<String, List<DicValue>> map = ds.getAll();
        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for(String key:set){
            application.setAttribute(key,map.get(key));
        }
        System.out.println("上下文域对象创建结束");

        /*
          数据字典处理完毕后，处理Stage2Possibility.properties文件

          步骤：
          1）解析改文件，将该属性配置文件中的键值对处理成java中的键值对关系（map）
              Map<String，String> pMap 第一个String表示阶段，第二个String表示可能性

          2）将阶段和可能性的键值对保存在pMap中，

          3）将pMap保存到服务器缓存中（application）中

          处理properties文件与两种方式，第一种使用java.util中的Properties类解析，一般针对没有中文的
          第二种使用ResourceBundle类解析既可以解析中文，也可以解析不含中文的，最常用
         */
        Map<String,String> pMap = new HashMap<>();
        //解析文件
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        /*
           枚举类，用在值一般都是固定的不会经常发生变动的，如月份，星期。。。
         */
        Enumeration<String> e = rb.getKeys();
        while (e.hasMoreElements()){
            String key = e.nextElement();
            String value = rb.getString(key);
            pMap.put(key,value);
        }
        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
