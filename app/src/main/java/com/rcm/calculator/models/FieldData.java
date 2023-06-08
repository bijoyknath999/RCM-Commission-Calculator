package com.rcm.calculator.models;

import java.io.Serializable;

public class FieldData implements Serializable {

    private String UserPV;
    public double UserPercentage;

    public FieldData() {
    }

    public String getUserPV() {
        return UserPV;
    }

    public void setUserPV(String userPV) {
        UserPV = userPV;
    }

    public double getUserPercentage() {
        return UserPercentage;
    }

    public void setUserPercentage(double userPercentage) {
        UserPercentage = userPercentage;
    }
}
