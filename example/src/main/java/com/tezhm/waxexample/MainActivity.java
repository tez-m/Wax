package com.tezhm.waxexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tezhm.wax.annotation.Inject;

public class MainActivity extends AppCompatActivity
{
    @Inject TestInject injectable1;
    @Inject OtherInject injectable2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("**************** " + injectable1 + " *******************");
        System.out.println("**************** " + injectable2 + " *******************");
    }
}
