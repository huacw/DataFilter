package com.foruo.sc.permission.example.dao;

import com.foruo.sc.permission.example.annotation.PermissionAop;
import com.foruo.sc.permission.example.entity.ExamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ExamDao {

    @Select("select id,username,region_cd as regionCd from sys_exam where id = #{id} ")
    @ResultType(ExamEntity.class)
    @PermissionAop(field = "comId")
    ExamEntity getExam(Integer id);

    /**
     * 有注解
     */
    @Select("select id,username,region_cd as regionCd from sys_exam where 1=1 ")
    @ResultType(ExamEntity.class)
    @PermissionAop(value = "测试有value",field = "comId")
    List<ExamEntity> getExamList();

    /**
     * 无注解
     */
    @Select("select id,username,region_cd as regionCd from sys_exam where 1=1 ")
    @ResultType(ExamEntity.class)
    List<ExamEntity> getExamList2();
}
