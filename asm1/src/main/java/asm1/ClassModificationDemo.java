package asm1;

import com.example.Inject;
import com.example.Module;
import com.test.other.Hello;
import java.util.ArrayList;

@Module
public class ClassModificationDemo
{
    private int version;
    @Inject private Hello hello1;
    private ArrayList<String> __lst;

    public int getVersion()
    {
        System.out.println(hello1);
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return "ClassCreationDemo: " + version;
    }
}
