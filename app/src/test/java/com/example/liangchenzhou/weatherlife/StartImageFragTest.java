package com.example.liangchenzhou.weatherlife;

import android.content.Context;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Junit test for StartImageFrag
 */
public class StartImageFragTest {
    @Mock
    Context context;

    //test if the function can successfully identify the weather condition
    @Test
    public void weatherConditionCheck(){
        int condition = 123456;
        StartImageFrag startImageFrag = new StartImageFrag();
        String result = startImageFrag.identifyWeather(condition);
        assertEquals("NG", result);
    }
}
