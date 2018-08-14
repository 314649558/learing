package com.sxd.app_stm_002.app.entity;

import java.io.Serializable;

public class AppCep implements Serializable {

    private String cepId;

    private String appId;

    private String filedName;

    private String expression;

    public String getCepId() {
        return cepId;
    }

    public void setCepId(String cepId) {
        this.cepId = cepId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}