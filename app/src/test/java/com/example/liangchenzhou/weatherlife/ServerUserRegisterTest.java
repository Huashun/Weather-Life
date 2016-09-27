package com.example.liangchenzhou.weatherlife;

import android.content.SharedPreferences;
import android.widget.EditText;



import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import org.mockito.Mock;

import entity.User;

/**
 * JUnit test for the register user functions
 */
public class ServerUserRegisterTest {

    private String passwordOne;
    private String passwordTwo;
    private RegisterFrag registerFrag = new RegisterFrag();
    private EditText userName, pwd;


    public ServerUserRegisterTest(){
        passwordOne = "123";
        passwordTwo = "123qwe";
    }
    @Test
    public void checkInputPasswordsSame(){
        Boolean checkState = registerFrag.checkInputPasswords(passwordOne, passwordTwo);
        assertEquals(false, checkState);
    }

    //test if email already exist, the return value is correct or not
    @Test
    public void checkUserExistonServer(){
        ArrayList<String> arrayLists = new ArrayList<>();
        String emailRegest = "88@qq.com";
        arrayLists.add("23@sina.com");
        arrayLists.add("wer@gmail.com");
        arrayLists.add("88@qq.com");
        //only change the arrayList from private to public for this unit test
        registerFrag.arrayList = arrayLists;
        Boolean checkUser = registerFrag.checkEmailExist(emailRegest);
        //false means that there already is a existed user on server!
        assertEquals(false, checkUser);
    }

    //test no users on server, if the method return correct
    @Test
    public void noUseronServerBefore(){
        ArrayList<String> arrayLists = new ArrayList<>();
        String emailRegest = "88@qq.com";
        //only change the arrayList from private to public for this unit test
        registerFrag.arrayList = arrayLists;
        Boolean checkUser = registerFrag.checkEmailExist(emailRegest);
        //false means that there already is a existed user on server!
        assertEquals(true, checkUser);
    }
}
