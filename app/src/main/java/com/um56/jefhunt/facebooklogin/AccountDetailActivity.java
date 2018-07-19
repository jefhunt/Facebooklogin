package com.um56.jefhunt.facebooklogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountDetailActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    ImageView imageView;
    TextView mEmail;
    TextView mUser;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent getIntent=getIntent();
        Bundle b=getIntent.getExtras();

        imageView = findViewById(R.id.image);
        mEmail =findViewById(R.id.email);
        mUser = findViewById(R.id.name);
        webView = findViewById(R.id.web);
        if(b!=null)
        {
           final String user = (String) b.get("user");
           firebaseFirestore.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                   if (task.getResult().exists()){
                       String name = task.getResult().getString("name");
                       String email = task.getResult().getString("email");
                       Log.e("USER","Name :"+name+" email: "+email);

                        mEmail.setText(email);
                        mUser.setText(name);
                        webView.loadUrl(task.getResult().getString("photo"));

                   }
               }
           });

        }
    }
}
