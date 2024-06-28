package com.familylocation.mobiletracker.phoneToolsModule;

public class NumLocatorModel {
    String code;
    String coutryName;

    public NumLocatorModel(String coutryName, String code) {
        this.coutryName = coutryName;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCoutryName() {
        return coutryName;
    }

    public void setCoutryName(String coutryName) {
        this.coutryName = coutryName;
    }
}
