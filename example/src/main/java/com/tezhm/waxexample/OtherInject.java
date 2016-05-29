package com.tezhm.waxexample;

import com.tezhm.wax.annotation.Inject;

/**
 * Created by Tez-Desktop on 5/21/2016.
 */
public class OtherInject
{
    @Inject
    private TestInject otherTest;

    public OtherInject()
    {
        System.out.println("******** Other constructor: " + this.otherTest + " **************");
    }

    @Override
    public String toString()
    {
        return "******* OTHER HAS BEEN INJECTED *********";
    }
}
