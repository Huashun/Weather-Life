package com.example.liangchenzhou.weatherlife;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

import entity.User;

/**
 * The Fragment for user login
 */
public class LoginFrag extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRefs,refer;
    private ArrayList<User> arrayList;
    private User currentUser;
    private Context context;
    private Button registerB, signB;
    private EditText loginEmail, loginPasswords;
    private CheckBox checkRemember, checkAuto;
    private SharedPreferences sharedPreferences;
//    private Toolbar toolbarStart;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFrag() {
        // Required empty public constructor
    }

    public static LoginFrag newInstance(String param1, String param2) {
        LoginFrag fragment = new LoginFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        sharedPreferences = context.getSharedPreferences("shareUser", Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        checkRemember = (CheckBox) view.findViewById(R.id.checkBoxRememberPwd);
        checkAuto = (CheckBox) view.findViewById(R.id.checkBoxAutoLogin);
        checkRemember.setOnCheckedChangeListener(this);
        checkAuto.setOnCheckedChangeListener(this);
        loginEmail = (EditText) view.findViewById(R.id.logEmail);
        loginPasswords = (EditText) view.findViewById(R.id.logPwd);
        registerB = (Button) view.findViewById(R.id.regButton);
        signB = (Button) view.findViewById(R.id.signButton);
        registerB.setOnClickListener(this);
        signB.setOnClickListener(this);

        //check if user select remember passwords or auto login checkbox
        this.loginStateCheck();

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //checkbox change event
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.checkBoxRememberPwd) {
            if (checkRemember.isChecked() == true) {
                sharedPreferences.edit().putBoolean("isChecked_Remember", true).commit();
            } else {
                sharedPreferences.edit().putBoolean("isChecked_Remember", false).commit();
            }
        } else if (buttonView.getId() == R.id.checkBoxAutoLogin) {
            if (checkAuto.isChecked() == true) {
                sharedPreferences.edit().putBoolean("isChecked_Auto", true).commit();
            }
        } else {
            sharedPreferences.edit().putBoolean("isChecked_Auto", false).commit();
        }

    }

    //button onclick event
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.signButton){
            this.checkLogin();

        } else if (v.getId() == R.id.regButton) {
            Fragment fragment;
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new RegisterFrag();
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        }
    }


    //get user inputs and validate them
    public User getInputValues(){
        String loggingEmail = loginEmail.getText().toString();
        String loggingPwd = loginPasswords.getText().toString();
        if ( !loggingEmail.equals("") && !loggingPwd.equals("")){
            return new User(loggingEmail, loggingPwd);
        }
        return null;
    }

    //clear the editText
    public void clearText (){
        loginEmail.setText("");
        loginPasswords.setText("");
    }

    //check if inputs are correct and if login is validated and save user login information
    public void checkLogin(){
        Fragment fragment;
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        User checkingUser = this.getInputValues();
        if (checkingUser != null){
            User checkedU = this.checkUser(checkingUser);
            //User checkedU = dbHelper.getUser(checkingUser);
            if (checkedU != null){
                this.saveUserData(checkedU);
                Toast.makeText(getActivity().getApplicationContext(), "Sign in Successfully", Toast.LENGTH_SHORT).show();
                this.saveLoginState();

                fragment = new StartImageFrag();
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();

                // use interface
                ((Refresh) LoginFrag.this.getActivity()).refreshActivity();

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "UserName or Passwords not correct!", Toast.LENGTH_SHORT).show();
                this.clearText();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "UserName or Passwords cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    //save user login information
    public void saveUserData(User saveUser){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", saveUser.getUserNameEmail());
            editor.putString("pwds", saveUser.getPassword());
            editor.commit();

    }

    //check auto login or remember passwords checkbox and response with correct actions
    public void loginStateCheck(){
        Boolean isCheckRememb = sharedPreferences.getBoolean("isChecked_Remember", false);
        Boolean isCheckAuto = sharedPreferences.getBoolean("isChecked_Auto", false);
        if (isCheckRememb == true) {
            checkRemember.setChecked(true);
            if (isCheckAuto == true) {
                checkAuto.setChecked(true);
                this.getPreviousStoreUser();
                Fragment fragment;
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new StartImageFrag();
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();

                Toast.makeText(getActivity().getApplicationContext(), "Sign in Successfully", Toast.LENGTH_SHORT).show();

            } else {
                this.getPreviousStoreUser();
            }
        } else {
            clearText();
        }
    }

    //get previous stored user information
    public void getPreviousStoreUser(){
        loginEmail.setText(sharedPreferences.getString("userName", ""));
        loginPasswords.setText(sharedPreferences.getString("pwds", ""));
    }

    //save the user login state, even user reopen the application, it keeps user login sate
    public void saveLoginState(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logStates", true);
        editor.commit();
    }

    //interface for StartImageActivity using to refresh the text of "login" menuitem
    interface Refresh{
        public void refreshActivity();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList = new ArrayList<>();
        fireDatabase = FirebaseDatabase.getInstance();
        myRefs = fireDatabase.getReference();
        refer = myRefs.child("UserInform");

        Query query = refer.orderByKey();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> hashUser = (HashMap<String, Object>) dataSnapshot.getValue();
                currentUser = new User((int) (long) hashUser.get("userId"), (String) hashUser.get("userNameEmail"),
                        (String) hashUser.get("password"));
                arrayList.add(currentUser);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //check if userName and passwords are correct on server
    public User checkUser(User inputUser){
        for (User item: arrayList){
            if (item.getUserNameEmail().equals(inputUser.getUserNameEmail()) &&
                    item.getPassword().equals(inputUser.getPassword())){
                return item;
            }
        }
        return null;
    }
}
