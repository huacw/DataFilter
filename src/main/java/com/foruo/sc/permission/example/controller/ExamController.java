package com.foruo.sc.permission.example.controller;

import com.foruo.sc.permission.example.entity.ExamEntity;
import com.foruo.sc.permission.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExamController {

    @Autowired
    ExamService examService;

    @RequestMapping(value = "getExam/{id}",method = {RequestMethod.GET,RequestMethod.POST})
    public ExamEntity getExam(@PathVariable Integer id){
        return examService.getExam(id);
    }

    @RequestMapping(value = "getExamList",method = {RequestMethod.GET,RequestMethod.POST})
    public List<ExamEntity> getExamList(){
        return examService.getExamList();
    }

    @RequestMapping(value = "getExamList2",method = {RequestMethod.GET,RequestMethod.POST})
    public List<ExamEntity> getExamList2(){
        return examService.getExamList2();
    }

}
