package com.example.liangchenzhou.weatherlife;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * The Activity for taking photo
 */
public class Photos extends AppCompatActivity implements View.OnClickListener {
    private static int indexs = 0;
    private String photoFileStr = null;
    private String mCurrentPhotoPath = "";
    private Uri uri;
    private static int lastImageId;
    private FirebaseDatabase fireDatabase;
    private DatabaseReference myRefs, refer, chatTrack;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef, imageRef;
    private Button photoBack, photoCamera, photoChoose, photoSend;
    private ImageView photo;
    private EditText nameImage;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        uri = null;
        //allow network task run on main thread
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }


        lastImageId = 0;
        fireDatabase = FirebaseDatabase.getInstance();
        myRefs = fireDatabase.getReference();
        chatTrack = myRefs.child("Chats");
        refer = myRefs.child("StreetImageId");

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReferenceFromUrl("gs://airy-task-129314.appspot.com");
        imageRef = storageRef.child("StreetView");

        nameImage = (EditText) findViewById(R.id.imageName);
        photo = (ImageView) findViewById(R.id.imagePhoto);
        photoBack = (Button) findViewById(R.id.photoBack);
        photoChoose = (Button) findViewById(R.id.photoChoose);
        photoCamera = (Button) findViewById(R.id.photoCamera);
        photoSend = (Button) findViewById(R.id.photoSend);
        photoBack.setOnClickListener(this);
        photoChoose.setOnClickListener(this);
        photoCamera.setOnClickListener(this);
        photoSend.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = refer.orderByChild("imageId").limitToLast(1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                long result = (long) dataSnapshot.getValue();
                lastImageId = (int) result;
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


        Query que = chatTrack.orderByKey().limitToLast(1);
        que.addChildEventListener(new ChildEventListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            photo.setImageBitmap(bitmap);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getExtras() == null && photoFileStr != null) {
                    Uri uris = Uri.fromFile(new File(photoFileStr));
                    uri = uris;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo.setImageBitmap(bitmap);
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    photo.setImageBitmap(imageBitmap);
                }
                galleryAddPic();

            } else {
                System.out.println("no data passed");

            }
        }
    }

    //onclick event in photo fragment
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photoBack) {
            Intent intent = new Intent(Photos.this, StartImageActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.photoChoose) {
            pickPhoto();
        } else if (v.getId() == R.id.photoCamera) {
            if (ActivityCompat.checkSelfPermission(Photos.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
            } else {
                openCamera();
            }
        } else if (v.getId() == R.id.photoSend) {
            if (uri != null){
                this.uploadImage(uri);
            }
        }
    }

    //require take photo permission for camera
    public void requestTakePhotoPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using camera, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Photos.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),
                                    "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //choose photos from gallery
    public void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    //upload the picked image to server
    public void uploadImage(Uri uriImage) {
        final int newId = lastImageId + 1;
        String id = String.valueOf(newId);
        if (checkImageName()) {
            String generatedN = generateImageName(newId);
            StorageReference newStreetView = imageRef.child(generatedN);
            UploadTask uploadTask = newStreetView.putFile(uriImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "upload failed, try again", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //save image identify id that be used for image name
                    HashMap<String, Long> hashMap = new HashMap<>();
                    hashMap.put("imageId", (long) newId);
                    refer.setValue(hashMap);
                    Toast.makeText(getApplicationContext(), "upload successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Photos.this, StartImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uriName", nameImage.getText().toString());
                    bundle.putString("uriString", taskSnapshot.getDownloadUrl().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();


                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "name of photo cannot be empty", Toast.LENGTH_SHORT).show();

        }
    }

    // open system camera method
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                photoFileStr = photoFile.toString();
            } catch (Exception ex) {
                System.out.print(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //update and save photos to system gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //create the file and path to store the photos
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            System.out.println("File Create successfully!++++++++++++++++++++++++++");
        } catch (IOException e) {
            System.out.println("File Create Failed!================================");
            e.printStackTrace();
        }

        mCurrentPhotoPath = image.toString();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Photos.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestTakePhotoPermissions();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION DENIED, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

        //check if image name is empty
    public Boolean checkImageName() {
        String name = nameImage.getText().toString();
        if (name.equals("")) {
            return false;
        }
        return true;
    }

    //check if image name is exist on server
    public String generateImageName(int identifyId) {
        String generatedName = String.valueOf(identifyId) + nameImage.getText().toString();
        return generatedName;
    }

}