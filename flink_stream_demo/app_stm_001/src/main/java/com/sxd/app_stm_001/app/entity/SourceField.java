package com.sxd.app_stm_001.app.entity;

import java.io.Serializable;

public class SourceField implements Serializable {

    private String fieldId;

    private String dsId;

    private String name;

    private String dataType;

    private Short filedType;

    private String comment;

    private Short sort;

    private Short status;

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Short getFiledType() {
        return filedType;
    }

    public void setFiledType(Short filedType) {
        this.filedType = filedType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}