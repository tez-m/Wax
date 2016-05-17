package asm1;

import com.test.other.Hello;

/**
 * Created by Tez-Desktop on 5/15/2016.
 */
public class Factory
{
    public static Hello make()
    {
        return new Hello();
    }
}
