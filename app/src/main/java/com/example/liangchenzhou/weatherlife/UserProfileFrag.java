package com.example.liangchenzhou.weatherlife;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import AdapterList.ListSuburbAdatper;
import entity.MsgChat;
import entity.PrefSuburb;
import entity.User;
import entity.WeatherFeather;

/**
 * User profile fragment handle the user information and location preference, rank
 */

public class UserProfileFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SensorEventListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef, reference, referenceMsg, referenceSuburb;
    private SharedPreferences sharedPreferences;
    private EditText email, password;
    private TextView editLink, saveSub, stepsC, photosC;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ListView listSuburb;
    private ListSuburbAdatper adatperS;

    private ArrayList<WeatherFeather> arrayFeather;
    private ArrayList<String> arraySubName;
    private ArrayList<User> arrayUser;
    private ArrayList<MsgChat> arrayMsg;

    private Bitmap bitImageWeather = null;
    private WeatherFeather featherAdd = new WeatherFeather();
    private String temps = "";
    private String condtis = "";
    private String subComparator = "";

    //steps counter variables
    private SensorManager sensorManager;
    private boolean activityRunning;

    public UserProfileFrag() {
        // Required empty public constructor
    }


    public static UserProfileFrag newInstance(String param1, String param2) {
        UserProfileFrag fragment = new UserProfileFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireDatabase = FirebaseDatabase.getInstance();
        myRef = fireDatabase.getReference();
        referenceMsg = myRef.child("Chats");
        reference = myRef.child("UserInform");
        referenceSuburb = myRef.child("UserPreferenceSuburb");
        arrayFeather = new ArrayList<>();
        adatperS = new ListSuburbAdatper(getActivity().getApplicationContext(), arrayFeather);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("shareUser", Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        email = (EditText) view.findViewById(R.id.disEmail);
        password = (EditText) view.findViewById(R.id.disPwd);
        editLink = (TextView) view.findViewById(R.id.disEdit);
        stepsC = (TextView) view.findViewById(R.id.stepCounter);
        photosC = (TextView) view.findViewById(R.id.photosCounter);
        saveSub = (TextView) view.findViewById(R.id.saveSuburb);
        listSuburb = (ListView) view.findViewById(R.id.listViewSuburb);
        listSuburb.setAdapter(adatperS);
        listSuburb.setOnItemClickListener(this);
        editLink.setOnClickListener(this);
        saveSub.setOnClickListener(this);
        photosC.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String uEmail = sharedPreferences.getString("userName", "");
        String uPasswd = sharedPreferences.getString("pwds", "");
        email.setText(uEmail);
        password.setText(uPasswd);
        email.setEnabled(false);
        password.setEnabled(false);

        stepsC.setText("0");


        arrayUser = new ArrayList<>();
        arrayMsg = new ArrayList<>();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                arrayUser.add(new User((int) (long) hashMap.get("userId"), (String) hashMap.get("userNameEmail"),
                        (String) hashMap.get("password")));

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


        Query query = referenceSuburb.orderByKey();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                arrayFeather.add(new WeatherFeather(dataSnapshot.getKey()));
                adatperS.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.disEdit){
            if (editLink.getText().equals("Edit")) {
                editLink.setText("Save");
                password.setEnabled(true);
            } else if (editLink.getText().equals("Save")){
                savePwd(password.getText().toString());
                editLink.setText("Edit");
                password.setEnabled(false);
            }
        } else if (v.getId() == R.id.saveSuburb){
            saveSubforUser();
        } else if(v.getId() == R.id.photosCounter){

        }
    }

    //save suburb on server for user
    public void saveSubforUser(){
        Location location = StartImageFrag.locationUsePreference;
        PrefSuburb prefSuburb = new PrefSuburb(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        referenceSuburb.child(StartImageFrag.suburbPreference).setValue(prefSuburb);
        Toast.makeText(getActivity().getApplicationContext(), "Save successfully", Toast.LENGTH_SHORT).show();
    }

    //save edited passwords to server and sharepreference
    public void savePwd(String newPwd){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pwds", newPwd);
        editor.commit();

        if (getUserId(email.getText().toString()) != null){
            User theUser = getUserId(email.getText().toString());
            theUser.setPassword(newPwd);
            int itemId = theUser.getUserId();
            reference.child(String.valueOf(itemId)).setValue(theUser);
        }
    }

    //get User id from server
    public User getUserId(String emails){
        for(User item: arrayUser){
            if (emails.equals(item.getUserNameEmail())){
                return item;
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countS = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countS != null){
            sensorManager.registerListener(this, countS, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Count Sensor not available!", Toast.LENGTH_SHORT).show();
        }
        int numberPhotos = 0;
        String uNameE = sharedPreferences.getString("userName", "");
        if (StartImageActivity.arrayMsgPass != null) {
            arrayMsg = StartImageActivity.arrayMsgPass;
            for (MsgChat item : arrayMsg) {
                if (uNameE.equals(item.getSenderName()) && !item.getImageName().equals("")) {
                    numberPhotos = numberPhotos + 1;
                }
            }
            photosC.setText(String.valueOf(numberPhotos));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
    }

    //textview item onClick Event
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        refreshItem(arrayFeather.get(position).getSuburbName());
    }

    //refresh the listview Item
    public void refreshItem(String suburb){
        subComparator = suburb;
        String apiQuery = "http://api.openweathermap.org/data/2.5/weather?q=" + suburb + ",melbourne,au&APPID=2d1c00b78d0f6e3d60bb4d36dded4a15";
        new SetupAsyncTask(getActivity().getApplicationContext()).execute(apiQuery);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //AsyncTask for getting weather conditions by suburb
    class SetupAsyncTask extends AsyncTask<String, Void, String> {
//        public String mainWea = "";
//        public int idWeather = 0;

        private Context context;

        SetupAsyncTask(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL requestUrl = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                InputStream inputStream = connection.getInputStream();
                String re = "";
                BufferedReader bufferRead = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder reader = new StringBuilder();
                while ((re = bufferRead.readLine()) != null) {
                    reader.append(re);
                }
                return reader.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String jResult = jsonObject.getString("weather");
                    JSONObject jMain = jsonObject.getJSONObject("main");
                    String currentTem = jMain.getString("temp");
                    double tempDoubleKelvin = Double.parseDouble(currentTem);
                    double tempDoubleCelsius = tempDoubleKelvin - 273.15;
                    DecimalFormat decimalFormat = new DecimalFormat("##");
                    tempDoubleCelsius = Double.parseDouble(decimalFormat.format(tempDoubleCelsius));


                    String jHumidity = jMain.getString("humidity");
                    String jName = jsonObject.getString("name");
                    JSONArray weatherArray = new JSONArray(jResult);
                    JSONObject mainObj = weatherArray.getJSONObject(0);
                    String mainWeather = mainObj.getString("main");
                    String descWeather = mainObj.getString("description");
                    String imageName = mainObj.getString("icon");
                    String imageUrl = "http://openweathermap.org/img/w/" + imageName + ".png";

                    condtis = descWeather;
                    temps = String.valueOf(tempDoubleCelsius) + " ˚C";

                    new AsyncTaskPicWeather(getActivity().getApplicationContext()).execute(imageUrl);

                } catch (Exception e) {

                }
            }
        }

    }

    //AsyncTask for get image of weather
    class AsyncTaskPicWeather extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncTaskPicWeather(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL requestUrl = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmapWeather = BitmapFactory.decodeStream(inputStream);
                return bitmapWeather;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            if (s != null){
                bitImageWeather = s;
                for (int i = 0; i < arrayFeather.size(); i++){
                    if (arrayFeather.get(i).getSuburbName().equals(subComparator)){
                        arrayFeather.get(i).setWeatherCondition(condtis);
                        arrayFeather.get(i).setTemps(temps);
                        arrayFeather.get(i).setWeaImage(bitImageWeather);
                        break;
                    }
                }
                adatperS.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "Already Update to the latest weather", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Sensor change listener and event
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(activityRunning){
            stepsC.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
