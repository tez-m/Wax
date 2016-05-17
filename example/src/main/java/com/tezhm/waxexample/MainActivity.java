package com.tezhm.waxexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.tez_desktop.waxexample.R;
import com.tezhm.wax.annotation.Inject;

public class MainActivity extends AppCompatActivity
{
    @Inject
    TestInject injectable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("**************** " + injectable + " *******************");
    }
}
