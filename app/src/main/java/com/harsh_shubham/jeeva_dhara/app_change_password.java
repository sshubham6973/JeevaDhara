package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_change_password extends AppCompatActivity {

    EditText new_1,new_2;
    Button change;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_change_password);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        new_1 = findViewById(R.id.editTextTextPassword2);
        new_2 = findViewById(R.id.editTextTextPassword3);
        change = findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(change.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if(check_network_connection()){
                    if(new_1.getText().toString().isEmpty() || new_2.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Fields cannot be empty",Toast.LENGTH_SHORT).show();
                    }else{
                        String regex = "^(?=.*[0-9])"
                                + "(?=.*[a-z])(?=.*[A-Z])"
                                + "(?=.*[@#$%^&+=])"
                                + "(?=\\S+$).{8,20}$";
                        if(new_1.getText().toString().matches(regex)){
                            if(new_1.getText().toString().equals(new_2.getText().toString())){
                                DocumentReference documentReference = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                documentReference.update("Password",new_1.getText().toString());
                                Toast.makeText(getApplicationContext(),"Password changed successfully.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(app_change_password.this,app_login_screen.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"Both passwords don't match",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Invalid Format",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void back_pressed(View view){
        startActivity(new Intent(app_change_password.this,app_login_screen.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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