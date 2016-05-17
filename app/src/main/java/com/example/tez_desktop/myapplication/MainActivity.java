package com.example.tez_desktop.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    Injectable injectable = new Injectable();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        System.out.println("**************** " + injectable.test + " *******************");
    }
}
