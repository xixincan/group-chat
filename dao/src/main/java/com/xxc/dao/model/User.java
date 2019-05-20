package com.xxc.dao.model;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "User")
public class User implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * name
     */
    @Column(name = "name")
    private String name;

    /**
     * nickname
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * age
     */
    @Column(name = "age")
    private Short age;

    /**
     * pwd
     */
    @Column(name = "password")
    private String password;

    @Column(name = "updated")
    private Long updated;

    @Column(name = "created")
    private Long created;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取name
     *
     * @return name - name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取nickname
     *
     * @return nickname - nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置nickname
     *
     * @param nickname nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取age
     *
     * @return age - age
     */
    public Short getAge() {
        return age;
    }

    /**
     * 设置age
     *
     * @param age age
     */
    public void setAge(Short age) {
        this.age = age;
    }

    /**
     * 获取pwd
     *
     * @return password - pwd
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置pwd
     *
     * @param password pwd
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return updated
     */
    public Long getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    /**
     * @return created
     */
    public Long getCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(Long created) {
        this.created = created;
    }
}