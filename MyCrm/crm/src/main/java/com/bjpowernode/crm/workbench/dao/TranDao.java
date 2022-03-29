package com.bjpowernode.crm.workbench.dao;


import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    List<Tran> getTranList(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    Tran getTranDetail(String id);

    int changeSatge(Tran tran);

    int getTotal();

    List<Map<String, Object>> getCharts();
}
