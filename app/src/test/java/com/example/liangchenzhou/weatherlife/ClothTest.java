package com.example.liangchenzhou.weatherlife;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Junit test for clothFrag
 */
public class ClothTest {
    private ClothFrag clothFrag;
    private StartImageFrag startImageFrag;
    @Before
    public void setup(){
        clothFrag = new ClothFrag();
        startImageFrag = new StartImageFrag();
    }

    //test the find cloth type function
    @Test
    public void testClothTypeFind(){
        double temperature = 35;
        startImageFrag.tempure = temperature;
        String result = clothFrag.findType();
        Assert.assertEquals("3040", result);
    }
}
