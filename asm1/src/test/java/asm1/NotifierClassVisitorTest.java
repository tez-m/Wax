package asm1;

import junit.framework.TestCase;

import org.junit.Test;

public class NotifierClassVisitorTest extends TestCase
{
    @Test
    public void testCounter() throws Exception
    {
        ClassModifierDemo.main(null);


        ClassModificationDemo demo = new ClassModificationDemo();
        demo.getVersion();
        demo.setVersion(1);
        System.out.println(demo);
    }
}
