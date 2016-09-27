package com.example.liangchenzhou.weatherlife;

import android.*;
import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;

import com.google.android.gms.maps.MapFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import entity.MsgChat;

/**
 * The activity that be used for store all the fragments, handle the navigation drawer and actionbar etc.
 */
public class StartImageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LoginFrag.OnFragmentInteractionListener, RegisterFrag.OnFragmentInteractionListener, StartImageFrag.OnFragmentInteractionListener,
        SettingFrag.OnFragmentInteractionListener, ChatFrag.OnFragmentInteractionListener, MapsFrag.OnFragmentInteractionListener,
        ClothFrag.OnFragmentInteractionListener, UserProfileFrag.OnFragmentInteractionListener, LoginFrag.Refresh, View.OnClickListener {

    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef, referenceMsg;
    public static ArrayList<MsgChat> arrayMsgPass;
    private static final int REQUEST_ALLOW_LOCATION = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences preferences;
    private NavigationView navigationView;
    private TextView headText,nick;
    private static Boolean stateLocationAccess = false;

    public static Context cont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_image);
        cont = this.getApplicationContext();
        fireDatabase = FirebaseDatabase.getInstance();
        myRef = fireDatabase.getReference();
        referenceMsg = myRef.child("Chats");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.left_drawer_list);
        navigationView.setNavigationItemSelectedListener(this);

        arrayMsgPass = new ArrayList<>();
        View headView = navigationView.getHeaderView(0);
        headText = (TextView) headView.findViewById(R.id.userEmailDis);
        nick = (TextView) headView.findViewById(R.id.nickNameDis);
        headText.setOnClickListener(this);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = getApplicationContext().getSharedPreferences("shareUser", MODE_PRIVATE);

        Boolean state = this.appFirstCheckUser();
        if (state == false) {
            this.getLoginItemText().setTitle("Sign in");
        } else {
            this.getLoginItemText().setTitle("Sign off");
            getHead(preferences.getString("userName", ""));

        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ALLOW_LOCATION);

        } else {
            if (getIntent().getExtras() != null) {
                Bundle extras = getIntent().getExtras();
//            String url = extras.getString("uriString");
//            String urlName = extras.getString("uriName");

                Fragment fragment;
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new ChatFrag();
                setTitle("Chat & Ask");
                fragment.setArguments(extras);
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
            } else {
                this.backToHome();
            }
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stateLocationAccess = true) {
            if (getIntent().getExtras() != null) {
                Bundle extras = getIntent().getExtras();

                Fragment fragment;
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new ChatFrag();
                setTitle("Chat & Ask");
                fragment.setArguments(extras);
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
            } else {
                this.backToHome();
            }
        }
    }


    // back to the home fragment (StartImageFrag) method
    private void backToHome() {
        Fragment fragment;
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new StartImageFrag();
        fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        setTitle("Home");
    }

    //first check user login state
    private Boolean appFirstCheckUser() {
        if (preferences.getBoolean("logStates", false) == true) {
            return true;
        }
        return false;
    }

    // logout method
    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
        this.removeHead();
        this.backToHome();

    }

    //set the head for user
    public void getHead(String eM){
        headText.setText(eM);
        nick.setText("Hi, Welcome back");
    }

    //remove head of user of navigationView
    public void removeHead(){
        headText.setText("User Profile");
        nick.setText("Hi, User");
    }

    // get the object of "login" menuitem
    public MenuItem getLoginItemText() {
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_signin);
        return menuItem;
    }

    //inflate the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_topmenu, menu);
        return true;
    }

    // Actionbar item select method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.navCamera:
                if (this.appFirstCheckUser() == true) {
                    Intent intent = new Intent(StartImageActivity.this, Photos.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.navMap:
                if (this.appFirstCheckUser() == true) {
                    fragment = new MapsFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.navSetting:
                if (this.appFirstCheckUser() == true) {
                    fragment = new SettingFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.navHome:
                fragment = new StartImageFrag();
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //set title for Actionbar
    public void setTitle(String title) {
        this.getSupportActionBar().setTitle(title);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //navigation item selected method
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment;
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new StartImageFrag();
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                setTitle(item.getTitle());
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(navigationView);
                return true;
            case R.id.nav_signin:
                String titleLogin = this.getLoginItemText().getTitle().toString();
                if (titleLogin.equals("Sign in")) {
                    fragment = new LoginFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                    setTitle(item.getTitle());
                    Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                } else if (titleLogin.equals("Sign off")) {
                    this.logout();
                    this.getLoginItemText().setTitle("Sign in");
                    this.backToHome();
                }
                drawerLayout.closeDrawer(navigationView);

                break;
            case R.id.nav_location:
                if (this.appFirstCheckUser() == true) {
                    setTitle(item.getTitle());
                    Intent intent = new Intent(StartImageActivity.this, Photos.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(navigationView);

                break;
            case R.id.nav_chat:
                if (this.appFirstCheckUser() == true) {
                    setTitle(item.getTitle());
                    fragment = new ChatFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(navigationView);

                break;
            case R.id.nav_travel:
                if (this.appFirstCheckUser() == true) {
                    setTitle(item.getTitle());
                    fragment = new MapsFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(navigationView);

                break;
            case R.id.nav_cloth:
                if (this.appFirstCheckUser() == true) {
                    setTitle(item.getTitle());
                    fragment = new ClothFrag();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(navigationView);

                break;
            default:
                break;
        }
        return false;
    }

    //the override method change the title of "login" for implementing the interface of login fragment
    @Override
    public void refreshActivity() {
        // finish();
        this.getLoginItemText().setTitle("Sign off");
        this.getHead(preferences.getString("userName", ""));
    }

    //require location access permission for app
    public void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using your location, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StartImageActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ALLOW_LOCATION);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),
                                    "Permission Denied, you can reopen app or change permission in system setting.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //get the result of request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ALLOW_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                stateLocationAccess = true;
                Toast.makeText(getApplicationContext(),
                        "Permission Allow.", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestLocationPermissions();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Permission Denied, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.userEmailDis){
            if (this.appFirstCheckUser() == true) {
                Fragment fragment;
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new UserProfileFrag();
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
            } else {
                Toast.makeText(getApplicationContext(), "Please sign in firstly!", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(navigationView);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        referenceMsg.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();
                MsgChat msgChat = new MsgChat(data.get("content"), data.get("msgDate"), data.get("senderName"), data.get("imageName"));
                arrayMsgPass.add(msgChat);


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
}