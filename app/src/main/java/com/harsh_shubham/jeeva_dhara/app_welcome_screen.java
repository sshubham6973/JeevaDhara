package com.harsh_shubham.jeeva_dhara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_welcome_screen extends AppCompatActivity {
    private static final String TAG = "TAG";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_welcome_screen);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));

        new Thread(){
            public void run()
            {
                try {
                    sleep(3000);
                    boolean net = check_network_connection();
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    if(net)
                    {
                        if(firebaseAuth.getCurrentUser()!=null)
                        {
                            check_user_profile();
                        }
                        else
                        {
                            startActivity(new Intent(getApplicationContext(),app_hello_there.class));
                            finish();
                        }

                    }
                    else
                    {
                        startActivity(new Intent(getApplicationContext(),app_no_internet_page.class));
                        finish();
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void check_user_profile() {
        DocumentReference docRef = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        Log.d(TAG,"number = "+firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String registered = documentSnapshot.getString("registration_status");
                    String login = documentSnapshot.getString("log_in_status");
                    String otp = documentSnapshot.getString("otp_verified");
                    Log.d(TAG," "+registered+" "+login+" "+otp);
                    if(login.equals("true")){
                        startActivity(new Intent(app_welcome_screen.this,MainActivity.class));
                        finish();
                    }
                    else
                    {
                        if(registered.equals("true") && otp.equals("true") && login.equals("false"))
                        {
                            startActivity(new Intent(app_welcome_screen.this,app_login_screen.class));
                            finish();
                        }
                       else
                        {
                            if(registered.equals("false") && otp.equals("true") && login.equals("false"))
                            {
                                startActivity(new Intent(app_welcome_screen.this,app_user_registration.class));
                                finish();
                            }else
                            {
                                startActivity(new Intent(app_welcome_screen.this,app_hello_there.class));
                                finish();
                            }
                        }
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failure");
            }
        });

    }

    public boolean check_network_connection(){
        boolean status = check_connectivity();
        boolean active = check_internet_connection();
        if(status && active ){
            return true;
        }else {
            return false;
        }

    }
    private boolean check_connectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active_network_info = connectivityManager.getActiveNetworkInfo();
        return  ( (active_network_info != null) && (active_network_info.isConnected()));
    }
    private boolean check_internet_connection(){
        try{
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() ==0);
        }catch (Exception e){
            return false;
        }
    }
}