package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;

import com.bjpowernode.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {

        List<String> nameList = customerDao.getCustomerName(name);
        return nameList;
    }
}
