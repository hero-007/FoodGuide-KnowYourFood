package ml.ayushdhanai.foodguide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Permission
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    //Progress bar code
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    ProgressBar mProgress;
    Drawable drawable;
    Resources res;
    ImageView progressImageview;


    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    Bitmap mBitmap;
    Button button;
    GridLayout gridLayout;
    RelativeLayout mylayout;
    AnimationDrawable animationDrawable;
    private StorageReference mStorageRef;
    String BASE_URL = "https://foodguide-101.herokuapp.com/api?url=";
    String apiResponse;
    ArrayList<Uri> alldownloadUrls = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mylayout = findViewById(R.id.mylayout);

        gridLayout=(GridLayout) findViewById(R.id.gridlayout1);
        animationDrawable = (AnimationDrawable) mylayout.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();


        //Permisiions
        checkAndRequestPermissions();

        //Progress bar code
        res = getResources();
        drawable = res.getDrawable(R.drawable.circular);
        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        progressImageview=(ImageView) findViewById(R.id.progressimageview);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/

        tv = (TextView) findViewById(R.id.tv);

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://foodguide-101.appspot.com");


    }

    //Camera
    Uri tempUri;
    File finalFile;String pathx;String s[];
    StorageReference storageReference = null;
    Uri downloadUrl;

    String url;
    JsonObjectRequest objectRequest;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                mBitmap = photo;
                imageView = findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(photo);

                //FireBase Connectivity


                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                tempUri = getImageUri(getApplicationContext(), photo);


                // CALL THIS METHOD TO GET THE ACTUAL PATH
                finalFile = new File(getRealPathFromURI(tempUri));


                //Extracting file name from file path
                pathx = String.valueOf(finalFile);
                s = pathx.split("/");
                pathx = s[s.length - 1];

                //progressbar start

                mProgress.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                progressImageview.setVisibility(View.VISIBLE);

                pStatus=0;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        while (pStatus < 100) {
                            pStatus += 1;

                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    mProgress.setProgress(pStatus);
                                    tv.setText(pStatus + "%");

                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                // Just to display the progress slowly
                                Thread.sleep(300); //thread will take approx 10 seconds to finish
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(pStatus==100) {
                            mProgress.setVisibility(View.INVISIBLE);
                            tv.setVisibility(View.INVISIBLE);
                            progressImageview.setVisibility(View.INVISIBLE);

                        }

                    }
                }).start();


                //Progress bar code end

                storageReference = mStorageRef.child(pathx);

                storageReference.putFile(tempUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = null;
                                        downloadUrl = uri;


                                        alldownloadUrls.add(downloadUrl);
                                        Log.d("MainActivity", "URL : " + downloadUrl);

                                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(downloadUrl));


                                        // Calling Rest API


                                        // Instantiate the RequestQueue.
                                        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                                        url = BASE_URL + downloadUrl;

                                        objectRequest = new JsonObjectRequest(
                                                Request.Method.GET,
                                                url,
                                                null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        apiResponse = " ";
                                                        Log.e("Rest Response", response.toString());
                                                        apiResponse = response.toString();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e("Rest Response", error.toString());
                                                    }
                                                }

                                        );
                                        //For handling timeout error
                                        objectRequest.setRetryPolicy(new RetryPolicy() {
                                            @Override
                                            public int getCurrentTimeout() {
                                                return 50000;
                                            }

                                            @Override
                                            public int getCurrentRetryCount() {
                                                return 50000;
                                            }

                                            @Override
                                            public void retry(VolleyError error) throws VolleyError {

                                            }
                                        });
                                        requestQueue.add(objectRequest);


                                        //Api part end

                                        try {
                                            Thread.sleep(10000);
                                            button.setVisibility(View.VISIBLE);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });





            }
        }catch (Exception e){
            onResume();
        }



    }



    ByteArrayOutputStream bytes;String path;

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        inImage = Bitmap.createScaledBitmap(inImage, (int) (inImage.getWidth() * 2), (int) (inImage.getHeight() * 2), true);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    int idx;String pathUri;Cursor cursor;

    public String getRealPathFromURI(Uri uri) {
        pathUri = "";
        if (getContentResolver() != null) {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                pathUri = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Uri a : alldownloadUrls) {
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(a));
            //Deleting the file from Storage
            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d("Main Activity  : ", "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d("Main Activity  : ", "onFailure: did not delete file");
                }
            });
        }

    }

    JSONObject reader;
    protected void classify(View view) {

       // do {
            try {

                //Json parsing

                if(pStatus==100) {
                    reader = new JSONObject(apiResponse);

                    String status = reader.getString("status");

                    if (status.equals("200")) {
                        String food_name = reader.getString("food_name");
                        String food_ingredients = reader.getString("food_ingredients");
                        String food_recipe = reader.getString("food_recipe");
                        String category = reader.getString("category");
                        Intent i = new Intent(this, ClassifyMainActivity.class);
                        i.putExtra("title", food_name);
                        i.putExtra("food_ingredients", food_ingredients);
                        i.putExtra("food_recipe", food_recipe);
                        i.putExtra("category", category);
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Sorry Unable to Classify Food! ", Toast.LENGTH_SHORT).show();
                        onResume();
                    }


                }
                else
                {
                    Toast.makeText(this, "Please wait for a second or two!", Toast.LENGTH_SHORT).show();
                    onResume();
                }

            } catch (JSONException e) {
                Toast.makeText(this, "Response Not received Wait for a While", Toast.LENGTH_SHORT).show();
                onResume();
            }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://foodguide-101.appspot.com");

        Button photoButton = this.findViewById(R.id.button1);
        button = (Button) findViewById(R.id.button2);

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                button.setVisibility(View.INVISIBLE);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });

    }

    public void northIndian(View view) {

        Intent i=new Intent(this,NorthActivity.class);
        startActivity(i);

    }

    public void southIndian(View view) {
        Intent i=new Intent(this,SouthActivity.class);
        startActivity(i);
    }

    public void chinese(View view) {
        Intent i=new Intent(this,ChineseActivity.class);
        startActivity(i);
    }

    public void continental(View view) {
        Intent i=new Intent(this,ContinentalActivity.class);
        startActivity(i);
    }

    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


}


/*
//This will run if image is correctly classified




 */