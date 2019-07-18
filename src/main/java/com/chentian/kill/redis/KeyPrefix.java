package com.chentian.kill.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
