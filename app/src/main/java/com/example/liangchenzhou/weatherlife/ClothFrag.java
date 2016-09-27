package com.example.liangchenzhou.weatherlife;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

import AdapterList.LAdapter;
import entity.Cloth;

/**
 * The Fragment for cloth helper
 */
public class ClothFrag extends Fragment {
    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef,reference;
    private ListView listView;
    private ArrayList<Cloth> arrayList;
    private LAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClothFrag() {
        // Required empty public constructor
    }


    public static ClothFrag newInstance(String param1, String param2) {
        ClothFrag fragment = new ClothFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = new ArrayList<>();
        fireDatabase = FirebaseDatabase.getInstance();
        myRef = fireDatabase.getReference();
        reference = myRef.child("ClothEntry");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloth, container, false);
        listView = (ListView) view.findViewById(R.id.listViewCloth);

        adapter = new LAdapter(getActivity(), arrayList);
        listView.setAdapter(adapter);
       // getActivity().setTitle(arrayList.size());

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

    //find the scope of temperatures and return a type
    public String findType(){
        double d = StartImageFrag.tempure;
      //  double d = ((TypesCloth) ClothFrag.this.getActivity()).getClothTypes();
        if (d >= 10.00 && d <= 20.00){
            return "1020";
        } else if (d < 10.00 && d >= 0.00){
            return "1000";
        } else if (d > 20.00 && d <= 30.00){
            return "2030";
        } else if (d > 30.00 && d <= 40.00){
            return "3040";
        } else if (d > 40.00){
            return "4000";
        }
        return "";
    }

//    interface TypesCloth{
//        public double getClothTypes(1020);
//    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        //display the recommendations of cloth according to the type
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String typeScope = findType();
        Query query = reference.orderByChild("clothType").equalTo(typeScope);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Cloth loadedCloth = new Cloth((int) (long) hashMap.get("clothId"), (String) hashMap.get("clothName"),
                        (String) hashMap.get("clothDesc"), (String) hashMap.get("clothType"));
                arrayList.add(loadedCloth);
                adapter.notifyDataSetChanged();
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
