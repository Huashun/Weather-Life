package com.example.liangchenzhou.weatherlife;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * JUnit Test for validate email format function
 */
public class EmailValidateTest {
    private String emailInput;
    private RegisterFrag registerFrag = new RegisterFrag();

    //test if isEmailValid function return correct
    @Test
    public void validateEmail (){
        emailInput = "349308uuh@yahoo.com";
        Boolean isValid = registerFrag.isEmailValid(emailInput);
        assertEquals(true, isValid);
    }
}
