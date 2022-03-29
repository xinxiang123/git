package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PagenationVo;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    List<Clue> getClueListByFullName(String fullname);

    boolean saveTran(Tran tran, String customerName);

    PagenationVo<Tran> pageList(Map<String, Object> map);

    Tran getTranDetail(String id);

    List<TranHistory> getHistoryByTranId(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}
