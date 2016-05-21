package com.tezhm.wax.internal;

public class FieldTuple
{
    public String fieldName;
    public String fieldType;
    public String factoryName;

    /**
     *
     * @param fieldName     "test"
     * @param fieldType     "com/example/tez_desktop/myapplication/TestInject"
     * @param factoryName   "com/tezhm/generated/factory/TestFactory"
     */
    public FieldTuple(String fieldName, String fieldType, String factoryName)
    {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.factoryName = factoryName;
    }
}
