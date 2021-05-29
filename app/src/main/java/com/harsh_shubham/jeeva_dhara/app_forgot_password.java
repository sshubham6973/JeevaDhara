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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_forgot_password extends AppCompatActivity {

    EditText pin;
    Button verify;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_forgot_password);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));

        pin = findViewById(R.id.id_pin_1_1);
        verify = findViewById(R.id.abundant);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(verify.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                boolean net = check_network_connection();
                if(net){
                    final String code = pin.getText().toString();
                    if(!code.isEmpty()){
                        DocumentReference documentReference = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    String correct_pin = documentSnapshot.getString("PIN");
                                    if(code.equals(correct_pin)){
                                        startActivity(new Intent(app_forgot_password.this,app_change_password.class));
                                    }else
                                    {
                                        Toast.makeText(getApplicationContext(),"Incorrect PIN.",Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    Toast.makeText(getApplicationContext(),"An Error Occurred",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        Toast.makeText(getApplicationContext(),"Please enter PIN.",Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void back_button_clicked(View view){
        startActivity(new Intent(app_forgot_password.this,app_login_screen.class));
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