package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {
    int saveClue(Clue c);
    int getTotalByCondition(Map<String, Object> map);
    List<Clue> getClueListByCondition(Map<String, Object> map);

    Clue getClueById(String id);

    Clue getById(String clueId);

    int delete(Clue clue);

    List<Clue> getClueListByFullName(String fullname);

    List<Clue> getTranList(Map<String, Object> map);
}
