package com.example.liangchenzhou.weatherlife;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.User;

/**
 * The Fragment for user register
 */
public class RegisterFrag extends Fragment implements View.OnClickListener {

    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef,reference;
    private Button regNow, backNow;
    private EditText email, pwd, confirmPwd;
//    private DatabaseHelper dbHelper;
    private Toolbar toolbarStart;
    public ArrayList<String> arrayList;
    private int idServer;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFrag() {
        // Required empty public constructor
    }


    public static RegisterFrag newInstance(String param1, String param2) {
        RegisterFrag fragment = new RegisterFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        fireDatabase = FirebaseDatabase.getInstance();
        myRef = fireDatabase.getReference();
        reference = myRef.child("UserInform");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        regNow = (Button) view.findViewById(R.id.buttonSubmitReg);
        backNow = (Button) view.findViewById(R.id.regBackMain);
        email = (EditText) view.findViewById(R.id.emailRegNow);
        pwd = (EditText) view.findViewById(R.id.pwdRegNow);
        confirmPwd = (EditText) view.findViewById(R.id.confirmRegNow);
        regNow.setOnClickListener(this);
        backNow.setOnClickListener(this);


        // Inflate the layout for this fragment
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

    //button onclick event in register fragment
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSubmitReg){
            this.attemptRegister();
        } else if (v.getId() == R.id.regBackMain){
            Fragment fragment;
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new StartImageFrag();
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        }
    }

    //get 'confirm inputs' and validate if thay are empty
    public String getInputConfirmValues(){
        String newConfirm = confirmPwd.getText().toString();
        if(!newConfirm.equals("")){
            return newConfirm;
        }
        return "NA";
    }

    //get 'user email' and 'passwords' input and validate them
    public User getInputValues(){
        String newEmail = email.getText().toString();
        String newPassword = pwd.getText().toString();
        if (!newEmail.equals("") && !newPassword.equals("")) {
            return new User(newEmail, newPassword);
        }
        return null;
    }

//    public ArrayList<String> getUsersfromServer(){
//        Query query = reference.orderByChild("userNameEmail");
//        query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
//                arrayList.add(hashMap.get("userNameEmail"));
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return
//    }


    @Override
    public void onStart() {
        super.onStart();
        arrayList = new ArrayList<>();
        idServer = 0;
        Query query = reference.orderByChild("userNameEmail");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                arrayList.add(hashMap.get("userNameEmail"));

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

        Query query2 = reference.orderByChild("userId").limitToLast(1);
        query2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Long> idResult = (HashMap<String, Long>) dataSnapshot.getValue();
                idServer = (int) (long) idResult.get("userId");

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

    // check if email already exists
    public boolean checkEmailExist(String checkEmail){
        if (arrayList.size() > 0){
            for (String serverUser: arrayList){
                if (serverUser.equals(checkEmail)){
                    return false;
                }
            }
            return true;
        }
        return true;

    }

    //check if two times passwords input are same
    public boolean checkInputPasswords(String newPwds, String confirmPwds){
        if ( newPwds.equals(confirmPwds)){
            return true;
        } else {
            return false;
        }
    }

    // attempt register action and validate all the inputs
    public void attemptRegister(){
        try {
            if (this.getInputValues() != null && !this.getInputConfirmValues().equals("NA")) {
                final User regUser = this.getInputValues();
                String confirmP = this.getInputConfirmValues();

                if (isEmailValid(regUser.getUserNameEmail())) {

                    boolean checkEmailExistState = this.checkEmailExist(regUser.getUserNameEmail());
                    boolean checkInputPasswordsState = this.checkInputPasswords(regUser.getPassword(), confirmP);
                    if (checkEmailExistState == true) {
                        if (checkInputPasswordsState == true) {
                            regUser.setUserId(idServer + 1);
                            String newId = String.valueOf(idServer + 1);
                            reference.child(newId).setValue(regUser);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                    Fragment fragment;
                                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragment = new LoginFrag();
                                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            //popup dialog with "Input Passwords not same!"
                            Toast.makeText(getActivity().getApplicationContext(), "Passwords are not same, try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //popup dialog with "Email already exist"
                        Toast.makeText(getActivity().getApplicationContext(), "Email already exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Email address not correct", Toast.LENGTH_SHORT).show();
                }
            } else {
                // validation for empty inputs
                Toast.makeText(getActivity().getApplicationContext(), "Inputs cannot be Empty", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {

        }
    }

    // validate the email address format
    public boolean isEmailValid(String email)
    {
        String regExpn =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." +
                        "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                        "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]" +
                        "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|" +
                        "[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
                        "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
