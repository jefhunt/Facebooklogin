package com.um56.jefhunt.facebooklogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class DataSyncToFirebase extends AsyncTask<Void, Void, Void> {

    String src;
    private Bitmap myBitmap;
    private FirebaseUser mUser;
    private FirebaseFirestore firebaseFirestore;
    Context context;


    DataSyncToFirebase(Uri url, FirebaseUser user, Context con){
        src = String.valueOf(url);
        mUser = user;
        this.context = con;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (myBitmap!=null) {
            Log.e("T", "Youhaaaa");

            uploadImage(myBitmap);
        }

    }

    public void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://facebooklogin-22d27.appspot.com");
        final StorageReference imagesRef = storageRef.child("fb_photo").child(mUser.getDisplayName()+".jpg");

        final UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                     //   Log.d("URL", uri.toString());
                        Map<String, String> userMap = new HashMap<>();
                        userMap.put("name",mUser.getDisplayName());
                        userMap.put("email",mUser.getEmail());
                        userMap.put("photo",uri.toString());

                        firebaseFirestore.collection("Users").document(mUser.getDisplayName()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                boolean flag = false;
                                if (task.isSuccessful()){

                                    if (flag == false) {
                                        Intent intent = new Intent(context, AccountDetailActivity.class);
                                        intent.putExtra("user", mUser.getDisplayName());
                                        context.startActivity(intent);

                                        flag=true;
                                    }
                                    if (flag==true){
                                        Log.e("AFETR","After activity");
                                        MailAsync mailAsync = new MailAsync(mUser);
                                        mailAsync.execute();

                                    }
                                    // Sending mail with info


                                }else {
                                  String err = task.getException().getMessage();
                                    Log.e("TAGA","Error"+err);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
