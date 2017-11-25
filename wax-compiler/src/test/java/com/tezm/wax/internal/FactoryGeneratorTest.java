package com.tezm.wax.internal;

import org.junit.Before;
import org.junit.Test;

public class FactoryGeneratorTest
{
    private FactoryGenerator testClass;

    @Before
    public void init()
    {
        this.testClass = new FactoryGenerator("");
    }

    @Test
    public void createFactoryInstantiateNoArgs() throws Exception
    {
        NoArgs test = new NoArgs();

    }

    @Test
    public void createFactoryInstantiateWithArgs() throws Exception
    {

    }

    @Test
    public void createFactoryCallsProvider() throws Exception
    {

    }


}

class NoArgs
{
}
