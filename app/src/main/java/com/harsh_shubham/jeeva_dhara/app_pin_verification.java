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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_pin_verification extends AppCompatActivity {

    TextView name_detector;
    EditText pin;
    Button login;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_pin_verification);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        name_detector = findViewById(R.id.textView35);
        pin = findViewById(R.id.editTextNumberPassword);
        login = findViewById(R.id.button3);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference =  firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        final DocumentReference documentReference1 =  firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("First-Name");
                    name_detector.setText("Are you really "+name+" ?");
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(login.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if(check_network_connection()){
                    if(pin.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Field cannot be Empty",Toast.LENGTH_SHORT).show();
                    }else {
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    String correct_pin = documentSnapshot.getString("PIN");
                                    if(correct_pin.equals(pin.getText().toString())){
                                      documentReference1.update("log_in_status","true");
                                        startActivity(new Intent(app_pin_verification.this,MainActivity.class));
                                      finish();
                                      Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Invalid PIN",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void back_1_pressed(View view){
        startActivity(new Intent(app_pin_verification.this,app_login_screen.class));
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