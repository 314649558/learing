package com.sxd.app_stm_002.work.cep.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/2.
 */
public class RuleExpressionBean implements Serializable {

    private String filedName;
    private String expression;
    private String ruleType;

    public RuleExpressionBean(String filedName, String expression) {
        this.filedName = filedName;
        this.expression = expression;
    }

    public RuleExpressionBean(String filedName, String expression, String ruleType) {
        this.filedName = filedName;
        this.expression = expression;
        this.ruleType = ruleType;
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

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public String toString() {
        return "RuleExpressionBean{" +
                "filedName='" + filedName + '\'' +
                ", expression='" + expression + '\'' +
                ", ruleType='" + ruleType + '\'' +
                '}';
    }
}
