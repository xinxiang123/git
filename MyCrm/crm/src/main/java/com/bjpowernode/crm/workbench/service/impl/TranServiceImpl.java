package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.dao.ClueDao;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    @Override
    public List<Clue> getClueListByFullName(String fullname) {
        List<Clue> clueList = clueDao.getClueListByFullName(fullname);
        return clueList;
    }

    @Override
    public boolean saveTran(Tran tran, String customerName) {
        /*
          这里主要执行的操作有：

          2）判断客户是否存在，存在就查出其id存到tran中的customerId
             不存在就重新创建一个新的客户，再将其id存到customerId中
          3）保存交易
          4）创建一条交易历史
         */
        boolean flag = true;


        //2）判断客户是否存在，存在就查出其id存到tran中的customerId
        //   不存在就重新创建一个新的客户，再将其id存到customerId中
        Customer cs = customerDao.getCustomerByName(customerName);
        if(cs == null){
            cs = new Customer();
            //能放的信息就往里面放，放不了的留给用户自己到客户中补全
            cs.setId(UUIDUtil.getUUID());
            cs.setOwner(tran.getOwner());
            cs.setName(customerName);
            cs.setCreateBy(tran.getCreateBy());
            cs.setCreateTime(tran.getCreateTime());
            cs.setContactSummary(tran.getContactSummary());
            cs.setNextContactTime(tran.getNextContactTime());
            cs.setDescription(tran.getDescription());
            //保存用户信息到用户表中
            int saveCustomerCount = customerDao.save(cs);
            if(saveCustomerCount != 1){
                System.out.println("**************客户信息保存失败**************");
                flag = false;
            }
        }
        tran.setCustomerId(cs.getId());

        //3）保存交易
        int saveTranCount = tranDao.save(tran);
        if(saveTranCount != 1){
            System.out.println("***********交易保存失败*************");
            flag = false;
        }
        //4）创建一条交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setCreateBy(tran.getCreateBy());

        int saveHistoryCount = tranHistoryDao.save(tranHistory);
        if(saveHistoryCount != 1){
            System.out.println("***********交易历史保存失败*************");
            flag = false;
        }

        return flag;
    }

    @Override
    public PagenationVo<Tran> pageList(Map<String, Object> map) {
        System.out.println("skipCount====="+map.get("skipCount")+" pageSize======="+map.get("pageSize"));
        int total = tranDao.getTotalByCondition(map);
        List<Tran> dataList = tranDao.getTranList(map);
        PagenationVo<Tran> vo  = new PagenationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public Tran getTranDetail(String id) {

        Tran tran = tranDao.getTranDetail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistoryByTranId(String tranId) {

        List<TranHistory> tranHistoryList = tranHistoryDao.getHistoryByTranId(tranId);
        return tranHistoryList;
    }

    @Override
    public boolean changeStage(Tran tran) {
        boolean flag = true;

        //改变交易阶段
        int changeCount = tranDao.changeSatge(tran);
        if (changeCount != 1){
            flag = false;
        }

        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(tran.getStage());
        th.setCreateBy(tran.getEditBy());
        th.setCreateTime(tran.getEditTime());
        th.setExpectedDate(tran.getExpectedDate());
        th.setTranId(tran.getId());
        th.setMoney(tran.getMoney());

        //添加交易历史
        int saveThCount = tranHistoryDao.save(th);
        if(saveThCount != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();
        // 取得dataList
         List<Map<String,Object>> dataList = tranDao.getCharts();
        //将total和dataList保存到map中

        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);
        //返回map
        return map;
    }

}
