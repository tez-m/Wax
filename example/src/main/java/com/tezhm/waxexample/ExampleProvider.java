package com.tezhm.waxexample;

import com.tezhm.wax.annotation.Provides;

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
