package com.example.liangchenzhou.weatherlife;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import entity.FiveForecast;
import entity.Recommendation;

/**
 * The Fragment is the default first fragment that be displayed
 */
public class StartImageFrag extends Fragment implements View.OnClickListener {
    private TextView labelCity;
    private TextView labelCurrent;
    private TextView labelHumidity;
    private TextView labelCurTemp;
    private TextView labelHighTemp;
    private TextView labelLowTemp;
    private static TextView sport1;
    private static TextView sport2;
    private static TextView sport1Des;
    private static TextView sport2Des;
    private ImageView imageWeather;
    private String apiQuery, suburbQuery, forecastQuery;
    private Button loginB;
    private LineChart chart;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef,reference;

    public static Location locationUsePreference;
    public static String suburbPreference;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_ALLOW_LOCATION = 0;

    private String mParam1;
    private String mParam2;
    public static double tempure = 0.00;
    public static String suburb = "";
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> arrayListData;


    public StartImageFrag() {
    }

    //textview onclick event
    @Override
    public void onClick(View v) {
        Fragment fragment;
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (v.getId() == R.id.sport1) {
            String newCriteria = sport1.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("criteria", newCriteria);
            fragment = new MapsFrag();
            fragment.setArguments(bundle);
            transaction.replace(R.id.content_frame, fragment).commit();
        } else if (v.getId() == R.id.sport2) {
            String newCriteria = sport2.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("criteria", newCriteria);
            fragment = new MapsFrag();
            transaction.replace(R.id.content_frame, fragment).commit();
        }
    }


    //get the result of request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALLOW_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    // AsyncTask for getting suburb of location
    class AsyncTaskSuburb extends AsyncTask<String, Void, String> {

        private Context context;

        AsyncTaskSuburb(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... subuerbUrls) {
            try {
                URL requestUrl = new URL(subuerbUrls[0]);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                InputStream inputStream = connection.getInputStream();
                String re = "";
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder reader = new StringBuilder();
                while ((re = bufferedReader.readLine()) != null) {
                    reader.append(re);
                }
                return reader.toString();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject resultObj = new JSONObject(result);
                    JSONArray resultArray = resultObj.getJSONArray("results");
                    JSONObject add_components = resultArray.getJSONObject(0);
                    JSONArray addressFirst = add_components.getJSONArray("address_components");
                    JSONObject suburbJson = addressFirst.getJSONObject(2);
                    String suburbName = suburbJson.getString("long_name");
                    suburb = suburbName;

                    suburbPreference = suburb;
                    System.out.println(suburb);
                    String[] items = suburb.split(" ");
                    String strSub = "";
                    StringBuilder builder = new StringBuilder();
                    if (items.length > 1){
                        for(int i = 0; i < items.length; i++){
                            builder.append(items[i]);
                            if (i < items.length - 1){
                                builder.append("%20");
                            }
                        }
                        strSub = builder.toString();
                    } else {
                        strSub = suburb;
                    }

                    apiQuery = "http://api.openweathermap.org/data/2.5/weather?q=" + strSub + ",melbourne,au&APPID=2d1c00b78d0f6e3d60bb4d36dded4a15";
                    new SetupAsyncTask(StartImageActivity.cont).execute(apiQuery);
                } catch (Exception e) {

                }
            } else {
                suburb = "Caulfield";
            }
        }
    }

    //AsyncTask for getting weather conditions by suburb
    class SetupAsyncTask extends AsyncTask<String, Void, String> {
        public String mainWea = "";
        public int idWeather = 0;

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
                    String hignT = jMain.getString("temp_max");
                    String lowT = jMain.getString("temp_min");
                    double tempHighKelvin = Double.parseDouble(hignT);
                    double tempHighCelsius = tempHighKelvin - 273.15;
                    tempHighCelsius = Double.parseDouble(decimalFormat.format(tempHighCelsius));
                    double tempLowKelvin = Double.parseDouble(lowT);
                    double tempLowCelsius = tempLowKelvin - 273.15;
                    tempLowCelsius = Double.parseDouble(decimalFormat.format(tempLowCelsius));


                    String jHumidity = jMain.getString("humidity");
                    String jName = jsonObject.getString("name");
                    JSONArray weatherArray = new JSONArray(jResult);
                    JSONObject mainObj = weatherArray.getJSONObject(0);
                    String mainWeather = mainObj.getString("main");
                    String descWeather = mainObj.getString("description");
                    String imageName = mainObj.getString("icon");
                    String imageUrl = "http://openweathermap.org/img/w/" + imageName + ".png";

                    idWeather = mainObj.getInt("id");

                    new AsyncTaskPicWeather(StartImageActivity.cont).execute(imageUrl);

                    labelCity.setText(jName);
                    labelCurrent.setText(descWeather);
                    labelHumidity.setText("Humidity: " + jHumidity + " %");
                    labelCurTemp.setText(String.valueOf(tempDoubleCelsius) + " ˚C");
                    labelHighTemp.setText("↑" + String.valueOf(tempHighCelsius) + " ˚C");
                    labelLowTemp.setText("↓" + String.valueOf(tempLowCelsius) + " ˚C");

                    mainWea = mainWeather;
                    getDataFromServer(fetchRecommendation(identifyWeather(idWeather)));
                    tempure = tempDoubleCelsius;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Random random = new Random();
                            int dicRoll = random.nextInt(6) + 0;
                            int dicR = -1;
                            String activityShow = arrayListData.get(dicRoll);
                            do {
                                dicR = random.nextInt(6) + 0;
                            } while (dicR == dicRoll);
                            String activitySh2 = arrayListData.get(dicR);
                            sport1.setText(activityShow);
                            sport2.setText(activitySh2);
                        }
                    }, 1500);

                } catch (Exception e) {

                }
            }
        }


    }

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
                imageWeather.setImageBitmap(s);
            }
        }
    }

    class AsyncFiveDaysForecast extends AsyncTask<String, Void, String>{
        private Context context;
        AsyncFiveDaysForecast(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String re = "";
                while ((re = reader.readLine()) != null){
                    builder.append(re);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null){
                try {
                    JSONObject forecastResult = new JSONObject(s);
                    JSONArray arrayItemList = forecastResult.getJSONArray("list");
                    DecimalFormat decimalFormat = new DecimalFormat("##");
                    decimalFormat.setDecimalSeparatorAlwaysShown(false);

                    JSONObject itemOne = arrayItemList.getJSONObject(0);
                    JSONObject mainOne = itemOne.getJSONObject("main");
                    double temOneKelvin = mainOne.getDouble("temp");
                    double tempOneCelsius = Double.parseDouble(decimalFormat.format(temOneKelvin - 273.15));
                    String dayOne = itemOne.getString("dt_txt");
                    String oneD = dayOne.substring(8, 11);

                    JSONObject itemTwo = arrayItemList.getJSONObject(8);
                    JSONObject mainTwo = itemTwo.getJSONObject("main");
                    double temTwoKelvin = mainTwo.getDouble("temp");
                    double tempTwoCelsius = Double.parseDouble(decimalFormat.format(temTwoKelvin - 273.15));
                    String dayTwo = itemTwo.getString("dt_txt");
                    String twoD = dayTwo.substring(8, 11);

                    JSONObject itemThree = arrayItemList.getJSONObject(16);
                    JSONObject mainThree = itemThree.getJSONObject("main");
                    double temThreeKelvin = mainThree.getDouble("temp");
                    double tempThreeCelsius = Double.parseDouble(decimalFormat.format(temThreeKelvin - 273.15));
                    String dayThree = itemThree.getString("dt_txt");
                    String threeD = dayThree.substring(8, 11);

                    JSONObject itemFour = arrayItemList.getJSONObject(24);
                    JSONObject mainFour = itemFour.getJSONObject("main");
                    double temFourKelvin = mainFour.getDouble("temp");
                    double tempFourCelsius = Double.parseDouble(decimalFormat.format(temFourKelvin - 273.15));
                    String dayFour = itemFour.getString("dt_txt");
                    String fourD = dayFour.substring(8, 11);

                    JSONObject itemFive = arrayItemList.getJSONObject(32);
                    JSONObject mainFive = itemFive.getJSONObject("main");
                    double temFiveKelvin = mainFive.getDouble("temp");
                    double tempFiveCelsius = Double.parseDouble(decimalFormat.format(temFiveKelvin - 273.15));
                    String dayFive = itemFive.getString("dt_txt");
                    String fiveD = dayFive.substring(8, 11);


                    displayChartData(chart, new FiveForecast(tempOneCelsius, tempTwoCelsius, tempThreeCelsius,
                            tempFourCelsius, tempFiveCelsius, oneD, twoD, threeD, fourD, fiveD));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static StartImageFrag newInstance(String param1, String param2) {
        StartImageFrag fragment = new StartImageFrag();
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
        reference = myRef.child("Activity");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //ActivitCreated method
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        arrayListData = new ArrayList<>();
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            //locationManager request the updates of location
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 8, locationListener);
            if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                locationUsePreference = location;
            }

            if (location != null) {
                double latLoction = location.getLatitude();
                double longLocation = location.getLongitude();
                suburbQuery = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLoction + "," + longLocation + "&key=" + getResources().getString(R.string.google_maps_key);
                new AsyncTaskSuburb(StartImageActivity.cont).execute(suburbQuery);
                forecastQuery = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latLoction + "&lon=" + longLocation + "&APPID=2d1c00b78d0f6e3d60bb4d36dded4a15";
                new AsyncFiveDaysForecast(StartImageActivity.cont).execute(forecastQuery);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "GPS Sensor is not available, try to open GPS and restart app!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_image, container, false);
        labelCity = (TextView) rootView.findViewById(R.id.textView2);
        labelCurrent = (TextView) rootView.findViewById(R.id.textView3);
        labelHumidity = (TextView) rootView.findViewById(R.id.textView4);
        labelCurTemp = (TextView) rootView.findViewById(R.id.currentTem);
        labelHighTemp = (TextView) rootView.findViewById(R.id.highTem);
        labelLowTemp = (TextView) rootView.findViewById(R.id.lowTem);
        imageWeather = (ImageView) rootView.findViewById(R.id.imageWeath);
        sport1 = (TextView) rootView.findViewById(R.id.sport1);
        sport1.setOnClickListener(this);
        sport2 = (TextView) rootView.findViewById(R.id.sport2);
        sport2.setOnClickListener(this);
        chart = (LineChart) rootView.findViewById(R.id.chartWeather);
//        chart.setAutoScaleMinMaxEnabled(true);

//        getPhoto();
        return rootView;
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

    //Display the chart data of 5 days forecast
    public void displayChartData(LineChart lChart, FiveForecast forecast) {
        ArrayList<Entry> vals = new ArrayList<Entry>();
        Entry entry = new Entry((float) forecast.getDayOne(), 0);
        Entry entry1 = new Entry((float) forecast.getDayTwo(), 1);
        Entry entry2 = new Entry((float) forecast.getDayThree(), 2);
        Entry entry3 = new Entry((float) forecast.getDayFour(), 3);
        Entry entry4 = new Entry((float) forecast.getDayFive(), 4);

        vals.add(entry);
        vals.add(entry1);
        vals.add(entry2);
        vals.add(entry3);
        vals.add(entry4);


        LineDataSet lineDataSet = new LineDataSet(vals, "5 Days Weather Forecast");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(forecast.getOne());
        xVals.add(forecast.getTwo());
        xVals.add(forecast.getThree());
        xVals.add(forecast.getFour());
        xVals.add(forecast.getFive());

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextSize(15);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (new DecimalFormat("##").format(value) + "˚");
            }
        });

        YAxis leftYAxis = lChart.getAxisLeft();
        leftYAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return (new DecimalFormat("##").format(value) + "˚");
            }
        });
        leftYAxis.setAxisMaxValue(45f);
        leftYAxis.setAxisMinValue(0f);

        YAxis rightYAxis = lChart.getAxisRight();
        rightYAxis.setEnabled(false);

        XAxis xAxis = lChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        lChart.setDescription("Date");
        lChart.setDescriptionColor(Color.WHITE);
        lChart.setBackgroundColor(Color.parseColor("#ca58b2b7"));
        lChart.setData(data);
        lChart.invalidate();
    }

    // identify weather condition to decide the recommendations
    public String identifyWeather(int id) {
        if(id != 0){
            //Thunderstorm
            if(id >= 200 && id <= 299){
                return "Thunderstorm";
            }
            //Drizzle
            else if(id >= 300 && id <= 399){
                return "Drizzle";
            }
            //Rain
            else if (id >= 500 && id <= 599){
                return "Rain";
            }
            //Snow
            else if (id >= 600 && id <= 699){
                return "Snow";
            }
            //Atmosphere
            else if (id >= 700 && id <= 799){
                return "Atmosphere";
            }
            //Clouds
            else if (id >= 801 && id <= 809){
                return "Clouds";
            }
            //Extreme
            else if (id >= 900 && id <= 909){
                return "Extreme";
            }
            //Additional
            else if (id >= 910 && id <= 999){
                return "Additional";
            }
            //Clear
            else if (id == 800){
                return "Clear";
            }
        }
        return "NG";
    }

    // get the recommendations by identifyWeather method
    public String fetchRecommendation(String condition) {
        String category = "";
        if (!condition.equals("NG")) {
            if (condition.equals("Thunderstorm") || condition.equals("Drizzle") ||
                    condition.equals("Rain") || condition.equals("Snow") ||
                    condition.equals("Extreme") || condition.equals("Additional")){
                category = "Indoor";
            } else if (condition.equals("Atmosphere") || condition.equals("Clouds") ||
                    condition.equals("Clear")){
                category = "Outdoor";
            }
            return category;
        }
        return "NG";
    }

    //get recommendation from server
    public void getDataFromServer(String cate){

        reference.child(cate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                arrayListData.add((String) dataSnapshot.getValue());

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

    interface ParseValue {
        public double getCurrentTem();
    }

    //Autogenerate interface
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
