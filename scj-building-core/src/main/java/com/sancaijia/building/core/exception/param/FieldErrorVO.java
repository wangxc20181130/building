package com.sancaijia.building.core.exception.param;

import org.springframework.validation.FieldError;

public class FieldErrorVO {
    private String name;
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FieldErrorVO build(FieldError fieldError) {
        if (null != fieldError) {
//            this.setName(SerializerUtil.camel2snake(fieldError.getField()));
            this.setName(fieldError.getField());
            this.setMessage(fieldError.getDefaultMessage());
        }

        return this;
    }

    public FieldErrorVO() {
    }

    @Override
    public String toString()  {
        return "FieldErrorVO(name=" + this.getName() + ", message=" + this.getMessage() + ")";
    }
}
