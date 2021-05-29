package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_login_screen extends AppCompatActivity {

    Button login;
    EditText ed_username,ed_password;
    CheckBox show_password;
    String username,password;
    ImageView security_way;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login_screen);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        ed_username = findViewById(R.id.editTextTextEmailAddress);
        ed_password = findViewById(R.id.editTextTextPassword);
        show_password = findViewById(R.id.checkBox2);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ ;
                    ed_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }else
                {
                    ed_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                ed_password.setSelection(ed_password.getText().length());
            }
        });
        login = findViewById(R.id.button2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean net = check_network_connection();
                if(net)
                {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(login.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    username = ed_username.getText().toString();
                    password = ed_password.getText().toString();
                    DocumentReference documentReference = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                String real_username = documentSnapshot.getString("Email-id");
                                String real_password = documentSnapshot.getString("Password");
                                if ( username.equals(real_username) && (password.equals(real_password))){
                                    final DocumentReference documentReference1 = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                    documentReference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                String login_status = documentSnapshot.getString("log_in_status");
                                                if(login_status.equals("false")){
                                                    documentReference1.update("log_in_status","true");
                                                    startActivity(new Intent(app_login_screen.this,MainActivity.class));
                                                    finish();
                                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                                }else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Someone already uses this account",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }else
                                {
                                    Toast.makeText(getApplicationContext(),"Username or Password is Invalid",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }

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

    public void forgot_password_clicked(View view){
        if(check_network_connection()){
            startActivity(new Intent(app_login_screen.this,app_forgot_password.class));
        }else
        {
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }
    }

    public void pin_pressed(View view){
        if(check_network_connection()){
            startActivity(new Intent(app_login_screen.this,app_pin_verification.class));
        }else{
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }
    }
}