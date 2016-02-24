package com.adavieslyons.util;

/**
 * Created by Ashley on 24/02/2016.
 */
public class Properties extends java.util.Properties {
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public String getProperty(int property) {
        return getProperty(Integer.toString(property));
    }
}
