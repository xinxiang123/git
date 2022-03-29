package com.bjpowernode.test;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.MD5Util;
import com.bjpowernode.crm.util.ServiceFactory;
import com.bjpowernode.crm.util.SqlSessionUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test01 {

    @Test
    public void expireTimeTest(){

      //验证失效时间
        //失效时间
        String expireTime = "2021-12-30 22:00:00";
        //当前系统时间
        String currentTime = DateTimeUtil.getSysTime();

        int count = expireTime.compareTo(currentTime);

        System.out.println("比较值:"+count);

    }

    @Test
    public void allowIpTest(){

        //验证允许访问的IP
        //允许访问的ip
        String allowIp = "192.168.1.1,192.168.1.2";
        //访问的浏览器ip
        String ip = "192.168.1.2";

        if(allowIp.contains(ip)) {
            System.out.println("有效ip允许访问");
        }else{
            System.out.println("无效ip，访问受限");
        }
    }

    @Test
    public void MD5PWDTest(){
        String pwd = "123";
        String pwd1 = "132";
        String pwd2 = "132cxx";
        pwd = MD5Util.getMD5(pwd);
        pwd1 = MD5Util.getMD5(pwd1);
        pwd2 = MD5Util.getMD5(pwd2);
        System.out.println("pwd="+pwd);
        System.out.println("pwd1="+pwd1);
        System.out.println("pwd2="+pwd2);
    }
    @Test
    public void selectDicType(){
        DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
        System.out.println(dicTypeDao);
        List<DicType> dicTypeList = dicTypeDao.getTypeList();
        System.out.println(dicTypeList);
        for (DicType dicType : dicTypeList){
            System.out.println(dicType.getCode());
        }
    }

    @Test
     public  void sum(){

        int sum = 0;
        for(int i=1;i<=100;i++){
            sum+=i;
        }
        System.out.println(sum);
    }

    @Test
    public void testList(){

        List<Integer> lists = new ArrayList();
        lists.add(10);
        lists.add(20);
        lists.add(30);
        for(Integer list :lists ){
            System.out.println(list);
        }
    }

    @Test
    public void testMap(){

        Map<Integer,String> map = new HashMap<>();
        map.put(1,"zhansan");
        map.put(2,"lisi");
        for(Integer key:map.keySet()){
            System.out.println(key);
            System.out.println(map.get(key));
        }
    }
}
