package com.bjpowernode.crm.settings.domain;
/*
   关于字符串中变现的日期和时间
   我们在市场上常用的有两种方式
     第一种：年月日
             yyyy-MM-dd 10位字符串

     第二种：年月日 时分秒 19位字符串
            yyyy-MM-dd HH:mm:ss
 */

/*
   关于登录：
      验证用户名和密码
        select * from tbl_user where loginAct=? and loginPwd=?;
      执行上面的sql语句的返回值为一个user对象

      如果user=null ，说明账号密码错误

      如果user对象不为null，只能说明账号密码正确，需要汲取验证其他字段的信息

      expireTime：验证失效时间
      lockState：验证锁定状态
      allowIps：验证浏览器端的IP地址是否有效
 */

public class User {
    private String id;           //编号，主键
    private String loginAct;    //登录账号
    private String name;        //用户的真实姓名
    private String loginPwd;    //登录密码
    private String email;       //邮箱
    private String expireTime;  //失效时间，19位字符串
    private String lockState;   //锁定时间，0表示锁定 1表示启用
    private String deptno;      //部门编号
    private String allowIps;    //允许访问的ip地址
    private String createTime;  //这条记录的创建时间  19位
    private String createBy;    //记录的创建人
    private String editTime;    //记录的修改时间 19位
    private String editBy;      //记录的修改人

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginAct() {
        return loginAct;
    }

    public void setLoginAct(String loginAct) {
        this.loginAct = loginAct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getAllowIps() {
        return allowIps;
    }

    public void setAllowIps(String allowIps) {
        this.allowIps = allowIps;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }
}
