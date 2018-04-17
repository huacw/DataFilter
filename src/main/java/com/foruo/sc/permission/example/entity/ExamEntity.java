package com.foruo.sc.permission.example.entity;

/**
 * 实体类
 * @author GaoYuan
 * @date 2018/4/17 下午2:41
 */
public class ExamEntity {

    private Integer id;
    private String username;
    private String regionCd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegionCd() {
        return regionCd;
    }

    public void setRegionCd(String regionCd) {
        this.regionCd = regionCd;
    }

    @Override
    public String toString() {
        return "ExamEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", regionCd='" + regionCd + '\'' +
                '}';
    }
}
