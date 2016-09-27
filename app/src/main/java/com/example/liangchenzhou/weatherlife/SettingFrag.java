package com.example.liangchenzhou.weatherlife;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * The Fragment for application setting
 */
public class SettingFrag extends Fragment {
    private Switch aSwitch;
    private SharedPreferences sharedPreferences;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingFrag() {
        // Required empty public constructor
    }


    public static SettingFrag newInstance(String param1, String param2) {
        SettingFrag fragment = new SettingFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //fragment oncreate method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getApplicationContext()
                .getSharedPreferences("shareUser", Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // fragment oncreateview method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        aSwitch = (Switch) view.findViewById(R.id.switchCancelAuto);
        this.firstCheckState();

        // set onclickchange listener for switch
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.switchCancelAuto) {
                    if (aSwitch.isChecked()) {
                        sharedPreferences.edit().putBoolean("isChecked_Auto", true).commit();
                        Toast.makeText(getActivity().getApplicationContext(), "Auto Login switch on", Toast.LENGTH_SHORT).show();
                    } else {
                        sharedPreferences.edit().putBoolean("isChecked_Auto", false).commit();
                        Toast.makeText(getActivity().getApplicationContext(), "Auto Login switch off", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        return view;
    }

    //check the state of user previous preference for login and set value for switch
    public void firstCheckState(){
        if (sharedPreferences.getBoolean("isChecked_Auto", false)){
            aSwitch.setChecked(true);
        } else if (!sharedPreferences.getBoolean("isChecked_Auto", false)) {
            aSwitch.setChecked(false);
        }
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
