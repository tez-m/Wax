package com.tezm.waxexample;

import com.tezm.wax.annotation.Provides;

public class ExampleProvider
{
    @Provides
    public ExampleInterface make()
    {
        return new ExampleInterface()
        {
            @Override
            public String hello()
            {
                return "************** SOO PRETTYYY *******************";
            }
        };
    }
}
