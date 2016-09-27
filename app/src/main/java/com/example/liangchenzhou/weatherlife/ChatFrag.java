package com.example.liangchenzhou.weatherlife;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import AdapterList.ChatSenderAdapter;
import entity.MsgChat;

/**
 * The Fragment for chat and request the weather conditions
 */
public class ChatFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private static int indexs = 0;

    private String galleryImageName = "";
    private static String nameDownloadImage;
    private ListView listVChat;
    private ChatSenderAdapter chatSenderAdapter;
    private ArrayList<MsgChat> arrayChatMsgs;
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat simpleDateFormat, simpleDateFormat2;
    private EditText sendContent;
    private Button sendButton;
    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRef,reference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef, imageRef;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChatFrag() {
        // Required empty public constructor
    }


    public static ChatFrag newInstance(String param1, String param2) {
        ChatFrag fragment = new ChatFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrayChatMsgs = new ArrayList<>();
        sharedPreferences = getActivity().getApplicationContext().
                getSharedPreferences("shareUser", Context.MODE_PRIVATE);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        simpleDateFormat2 = new SimpleDateFormat("yyyyMMdd");
        fireDatabase = FirebaseDatabase.getInstance();
        myRef = fireDatabase.getReference();
        reference = myRef.child("Chats");

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReferenceFromUrl("gs://airy-task-129314.appspot.com");
        imageRef = storageRef.child("StreetView");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        sendContent = (EditText) view.findViewById(R.id.sendText);
        sendButton = (Button) view.findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(this);
        listVChat = (ListView) view.findViewById(R.id.listViewChat);
        chatSenderAdapter = new ChatSenderAdapter(getActivity().getApplicationContext(), arrayChatMsgs);
        listVChat.setAdapter(chatSenderAdapter);
        listVChat.setOnItemLongClickListener(this);
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

    // save message to server
    public void saveMsgOnServer(MsgChat msgChat){
      //  String date = simpleDateFormat.format(dateStream);
        HashMap<String,String> msgSave = new HashMap<>();
        msgSave.put("senderName", msgChat.getSenderName());
        msgSave.put("msgDate", msgChat.getMsgDate());
        msgSave.put("content", msgChat.getContent());
        msgSave.put("imageName", "");

        indexs = indexs + 1;
        reference.child(String.valueOf(indexs)).setValue(msgSave);
    }

    // get values of message and prepare to send
    public MsgChat prepareMeg(){
        MsgChat msg = new MsgChat();
        msg.setContent(sendContent.getText().toString());

        Date currentDate = new Date(System.currentTimeMillis());
        String cDate = simpleDateFormat.format(currentDate);
        msg.setMsgDate(cDate);


        msg.setSenderName(sharedPreferences.getString("userName", ""));
        return msg;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //add data change listener and the event
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();
                MsgChat msgChat = new MsgChat(data.get("content"), data.get("msgDate"), data.get("senderName"), data.get("imageName"));
                arrayChatMsgs.add(msgChat);
                refreshList();
                System.out.println(msgChat);

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



        Query query = reference.orderByKey().limitToLast(1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                indexs = Integer.parseInt(dataSnapshot.getKey());

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

    //button onclick event
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSend){
            this.saveMsgOnServer(this.prepareMeg());
            sendContent.setText("");

        }
    }

    //refresh the adapter and ask it update the values
    public void refreshList(){
        chatSenderAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

        saveURLofImage();

    }

    //get bundle from photo activity and save photo to chat
    public void saveURLofImage(){
        String uriStr = "";
        if (getArguments() != null && !getArguments().getString("uriString").equals("")){
            uriStr = getArguments().getString("uriString");

            MsgChat msg = new MsgChat();
            msg.setContent(uriStr);
            Date currentDate = new Date(System.currentTimeMillis());
            String cDate = simpleDateFormat.format(currentDate);
            msg.setMsgDate(cDate);
            msg.setSenderName(sharedPreferences.getString("userName", ""));
            msg.setImageName(getArguments().getString("uriName"));
            HashMap<String, String> strHashMap = new HashMap<>();
            strHashMap.put("senderName", msg.getSenderName());
            strHashMap.put("msgDate", msg.getMsgDate());
            strHashMap.put("content", msg.getContent());
            strHashMap.put("imageName", msg.getImageName());
            indexs = indexs + 1;
            reference.child(String.valueOf(indexs)).setValue(strHashMap);
        }
    }

    //listview item onClick listener
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        nameDownloadImage = "";
        final MsgChat msgC = new MsgChat();
        msgC.setImageName(arrayChatMsgs.get(position).getImageName());
        msgC.setContent(arrayChatMsgs.get(position).getContent());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Download")
                .setMessage("Do you want to download?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!msgC.getImageName().equals("")) {
                            nameDownloadImage = msgC.getImageName();
                            new AsyncDownloadTask(getActivity().getApplicationContext()).execute(msgC.getContent());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    galleryAddPic();
                                }
                            }, 5000);
                            Toast.makeText(getActivity().getApplicationContext(), "Downloading in background.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "This is not a valid image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();

        return false;
    }

    //create the file and path to store the image
    private File createImageFile(String nameStr) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + nameStr;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpeg", storageDir);
            System.out.println("File Create successfully!++++++++++++++++++++++++++");
        } catch (IOException e) {
            System.out.println("File Create Failed!================================");
            e.printStackTrace();
        }
        return imageFile;
    }

    class AsyncDownloadTask extends AsyncTask<String, Void, Bitmap>{
        private Context context;

        AsyncDownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL urls = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);
                return bitmapImage;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            if (bitmap != null){
                File newFile = createImageFile(nameDownloadImage);
                galleryImageName = newFile.toString();
                try {
                    outputStream = new FileOutputStream(newFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //update and save photos to system gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(galleryImageName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
}
