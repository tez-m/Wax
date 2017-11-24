package com.tezhm.wax.injection;

/**
 * POJO for injection parameters of a classes field.
 */
public class InjectionField
{
    public String name;
    public String type;
    public String factory;

    /**
     *
     * @param name      "test"
     * @param type      "com/tezhm/example/myapplication/TestInject"
     * @param factory   "com/tezhm/generated/factory/TestFactory"
     */
    public InjectionField(String name, String type, String factory)
    {
        this.name = name;
        this.type = type;
        this.factory = factory;
    }
}
