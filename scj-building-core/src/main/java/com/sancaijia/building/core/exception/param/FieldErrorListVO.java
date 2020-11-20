package com.sancaijia.building.core.exception.param;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FieldErrorListVO {
    private List<FieldErrorVO> data;

    public FieldErrorListVO() {
    }

    public List<FieldErrorVO> getData() {
        return data;
    }

    public void setData(List<FieldErrorVO> data) {
        this.data = data;
    }

    public List<FieldErrorVO> build(List<FieldError> list) {
        if (null == this.data) {
            this.data = new ArrayList<>();
        }

        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            FieldError fieldError = (FieldError)var2.next();
            this.data.add((new FieldErrorVO()).build(fieldError));
        }

        return this.data;
    }

    public String toString() {
        return "FieldErrorListVO{data=" + this.data + '}';
    }
}
