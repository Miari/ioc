package com.boroday.ioc.entity;

import java.util.Objects;

public class Bean {
    private String id;
    private Object value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) { //for test purposes
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bean bean = (Bean) o;
        return id.equals(bean.id) &&
                value.getClass().equals(bean.value.getClass());
    }

    @Override
    public int hashCode() { //for test purposes
        return Objects.hash(id, value);
    }
}
