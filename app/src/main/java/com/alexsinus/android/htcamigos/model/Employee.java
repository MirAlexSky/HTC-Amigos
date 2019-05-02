package com.alexsinus.android.htcamigos.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private int mId;
    private String mName;
    private String mPhoneNumber;
    private List<String> mSkills = new ArrayList<>();

    public void setName(String name) {
        mName = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public void addSkill(String skill) {
        mSkills.add(skill);
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public List<String> getSkills() {
        return mSkills;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
