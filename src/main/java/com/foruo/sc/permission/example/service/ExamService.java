package com.foruo.sc.permission.example.service;

import com.foruo.sc.permission.example.dao.ExamDao;
import com.foruo.sc.permission.example.entity.ExamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    @Autowired
    ExamDao examDao;

    public ExamEntity getExam(Integer id){
        return examDao.getExam(id);
    }

    public List<ExamEntity> getExamList(){
        return examDao.getExamList();
    }

    public List<ExamEntity> getExamList2(){
        return examDao.getExamList2();
    }

}
