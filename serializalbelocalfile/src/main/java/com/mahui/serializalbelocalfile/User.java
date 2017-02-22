package com.mahui.serializalbelocalfile;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/16.
 */

public class User implements Serializable{

    private static final long serialVersionUID = -6961992443652721712L;
    private String name;
    private String sex;

    public User(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
