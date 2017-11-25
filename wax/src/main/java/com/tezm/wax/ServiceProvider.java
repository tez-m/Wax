package com.tezm.wax;

public interface ServiceProvider<T>
{
    T make();
}
