package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.util.DateTimeUtil;
import com.bjpowernode.crm.util.SqlSessionUtil;
import com.bjpowernode.crm.util.UUIDUtil;
import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao carDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao =  SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关表：
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    //市场活动相关表
    private  ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);

    @Override
    public boolean saveClue(Clue c) {

        boolean flag = false;
        int count = clueDao.saveClue(c);
        if(count == 1){
            flag = true;
        }
        return flag;
    }

    @Override
    public PagenationVo<Clue> pageList(Map<String, Object> map) {

        int total = clueDao.getTotalByCondition(map);
        List<Clue> dataList = clueDao.getClueListByCondition(map);
        PagenationVo<Clue> vo  = new PagenationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public Clue getDetail(String id) {

        Clue clue = clueDao.getClueById(id);
        return clue;
    }

    @Override
    public List<Activity> getActivityByClueId(String clueId) {
       List<Activity> activityList = activityDao.getActivityByClueId(clueId);
        return activityList;
    }

    @Override
    public boolean undbund(String id) {

        boolean flag = true;

        int count = carDao.unbund(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getAListNotLinkedByClueId(Map<String, String> map) {
        List<Activity> activityList = activityDao.getAListNotLinkedByClueId(map);
        return activityList;
    }

    @Override
    public boolean bundClueAndActivity(String clueId, String[] aids) {
        boolean flag = true;
        for(String aid : aids){
            //每取得一个aid就增加一个关联关系
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(aid);

            //添加关系表中的记录
            int count = carDao.bundClueAndActivity(car);
            if(count != 1){
                flag = false;
            }
            System.out.println("clueId++++++++++++++"+clueId+",aid=++++++"+aid);
        }
        return flag;
    }

    @Override
    public List<Activity> getAListByName(String name) {

        List<Activity> activityList = activityDao.getAListByName(name);
        return activityList;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {

        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;
        //通过线索id获取线索对象（线索对象中封装了线索的信息）
        Clue clue = clueDao.getById(clueId);

        //（1）通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在）
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        //（2）如果customer为null，说明以前没有这个客户，需要新建一个
        if(customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            //添加客户：
            int saveCustomerCount = customerDao.save(customer);
            if(saveCustomerCount != 1){
                flag = false;
            }
        }

        //经过第二步处理后，客户的信息我们已经拥有了，将来在处理其他表的时候，我们要用到客户信息的时候
        //（3）通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(clue.getSource());
        con.setOwner(clue.getOwner());
        con.setNextContactTime(clue.getNextContactTime());
        con.setMphone(clue.getMphone());
        con.setJob(clue.getJob());
        con.setFullname(clue.getFullname());
        con.setEmail(clue.getEmail());
        con.setDescription(customer.getDescription());
        /*
              经过第二步处理后，客户的信息我们已经拥有了，将来在处理其他表的时候，
              我们要用到客户信息的时候，直接从客户信息里面取
         */
        con.setCustomerId(customer.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setAddress(clue.getAddress());
        con.setAppellation(clue.getAppellation());

        //添加联系人
        int saveCustomerCount = contactsDao.save(con);
        if(saveCustomerCount != 1){
            flag = false;
        }

        /*
            经过第三步处理后，联系人信息我们已经拥有了，将来处理其他表的时候，
            如果要使用到联系人的相关信息时直接get获取
         */

        //(4)将线索备注转换到客户备注以及联系人备注中
        //查询出与线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByCluId(clueId);

        for(ClueRemark clueRemark : clueRemarkList){
            //取出每一条线索的备注信息（主要转换到客户备注和联系人备注的就是这个备注信息）
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(createBy);
            customerRemark.setEditFlag("0");
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            int saveCustomerRemarkCount = customerRemarkDao.save(customerRemark);

            if(saveCustomerRemarkCount != 1){
                flag = false;
            }
            //创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            int saveContactsRemarkCount = contactsRemarkDao.save(contactsRemark);
            if(saveContactsRemarkCount != 1){
                flag = false;
            }
        }

        //（5）“线市场活动”的关系转换到“联系和市场活动”的关系
        //首先查询出与该线索关联的市场活动，查询与市场活动弄的关联关系列表
        System.out.println("=============“线市场活动”的关系转换到“联系和市场活动”的关系================");
        List<ClueActivityRelation> clueActivityRelationList = carDao.getListByClueId(clueId);

        //遍历出每一条与市场活动关联的关系记录
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            //从每一条遍历出来的关系中取出关联的市场活动Id
            String activityId = clueActivityRelation.getActivityId();

            //创建 联系人与市场活动的关联关系对象，让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(con.getId());
            contactsActivityRelation.setActivityId(activityId);

            int saveContactsActivityRelationCount = contactsActivityRelationDao.save(contactsActivityRelation);
            if(saveContactsActivityRelationCount != 1){
                flag = false;
            }
        }

        //(6)如果有创建交易需求，创建一条交易
        if(t != null){
                /*
                 tran对象在controller中已经封装好啦一些信息,这些信息可以根据客户需求来完善，
                 现在我们上能添加的信息都给添加上去
                 下面利用以上创建的对象继续完善一些没有被封装的信息
                  t.setId(id);
                  t.setMoney(money);
                  t.setName(name);
                  t.setExpectedDate(exceptedDate);
                  t.setStage(stage);
                  t.setActivityId(activityId);
                  t.setCreateBy(createBy);
                  t.setCreateTime(createTime);
                 */
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setNextContactTime(clue.getNextContactTime());
            t.setDescription(clue.getDescription());
            t.setContactsId(con.getId());
            t.setCustomerId(customer.getId());
            t.setContactSummary(clue.getContactSummary());

            //添加交易
            int saveTranCount = tranDao.save(t);
            if(saveTranCount != 1){
                flag = false;
            }

            //（7）如果创建了交易，则创建一条该交易下的交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());

            //添加交易历史
            int saveTranHistoryCount = tranHistoryDao.save(th);
            if(saveTranHistoryCount != 1){
                flag = false;
            }
        }

        //（8）删除线索备注
        for(ClueRemark clueRemark : clueRemarkList){
            int deleteClueRemarkCount = clueRemarkDao.delete(clueRemark);
            if(deleteClueRemarkCount != 1){
                flag = false;
            }
        }

        //(9)删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList) {
            int deleteClueActivityRelationCount = carDao.delete(clueActivityRelation);
            if (deleteClueActivityRelationCount != 1) {
                flag = false;
            }
        }

        //（10）删除线索表
        int deleteClueCount = clueDao.delete(clue);
        if(deleteClueCount != 1){
            flag = false;
        }

        return flag;
    }
}
