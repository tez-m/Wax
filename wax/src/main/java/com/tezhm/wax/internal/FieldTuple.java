package com.tezhm.wax.internal;

public class FieldTuple
{
    public String fieldName;
    public String fieldType;
    public String factoryName;

    public FieldTuple(String fieldName, String fieldType, String factoryName)
    {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.factoryName = factoryName;
    }
}
