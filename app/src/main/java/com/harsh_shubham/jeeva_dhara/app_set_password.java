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
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class app_set_password extends AppCompatActivity {

    int t=0;
    ImageView back;
    EditText password_1,password_2,pin_1,pin_2;
    ViewFlipper view_flipper_1;
    TextView title,sub_title,description;
    Button sign_up;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String password,pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_set_password);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.color_2));

        password_1 = findViewById(R.id.id_password_1);
        password_2 = findViewById(R.id.id_password_2);
        pin_1 = findViewById(R.id.id_pin_1_1);
        pin_2 = findViewById(R.id.id_pin_2);
        sign_up =findViewById(R.id.button_repartee);
        view_flipper_1 = findViewById(R.id.viewFlipper_1);
        back = findViewById(R.id.back_btn);
        title = findViewById(R.id.title_set_password);
        sub_title = findViewById(R.id.title_secure_account);
        description = findViewById(R.id.textView29);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean net = check_network_connection();
                if(net){
                    if(t==1)
                    {
                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(sign_up.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        int a = Integer.parseInt(pin_1.getText().toString().substring(0,1));
                        int b = Integer.parseInt(pin_1.getText().toString().substring(1,2));
                        int c = Integer.parseInt(pin_1.getText().toString().substring(2,3));
                        int d = Integer.parseInt(pin_1.getText().toString().substring(3,4));
                        if(Math.abs(a-b)==Math.abs(b-c) || Math.abs(b-c)==Math.abs(c-d))
                        {
                            Toast.makeText(getApplicationContext(),"Please enter a better pin",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(pin_1.getText().toString().equals(pin_2.getText().toString()))
                            {
                                password = password_1.getText().toString();
                                pin = pin_1.getText().toString();
                                DocumentReference documentReference1 = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                DocumentReference documentReference = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                documentReference.update("registration_status","true");
                                documentReference1.update("Password",password);
                                documentReference1.update("PIN",pin);
                                Toast.makeText(getApplicationContext(),"Successfully Created Account",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(app_set_password.this,app_login_screen.class));
                                finish();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Both PINs don't match",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else {
                        String regex = "^(?=.*[0-9])"
                                + "(?=.*[a-z])(?=.*[A-Z])"
                                + "(?=.*[@#$%^&+=])"
                                + "(?=\\S+$).{8,20}$";
                        if(password_1.getText().toString().isEmpty() || password_2.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(),"Fields cannot be empty.",Toast.LENGTH_SHORT).show();
                        }else{
                            if(password_1.getText().toString().trim().matches(regex))
                            {
                                if(password_1.getText().toString().equals(password_2.getText().toString()))
                                {
                                    view_flipper_1.setInAnimation(app_set_password.this, R.anim.slide_in_right);
                                    view_flipper_1.setOutAnimation(app_set_password.this, R.anim.slide_out_left);
                                    view_flipper_1.showNext();
                                    title.setText(R.string.title_100);
                                    sub_title.setText(R.string.title_101);
                                    sign_up.setText(R.string.title_104);
                                    description.setText("");
                                    back.setVisibility(View.VISIBLE);
                                    t+= 1;
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Both passwords don't match",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Format",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void back_button_clicked(View view)
    {
        view_flipper_1.setInAnimation(app_set_password.this,android.R.anim.slide_in_left);
        view_flipper_1.setOutAnimation(app_set_password.this,android.R.anim.slide_out_right);
        view_flipper_1.showPrevious();
        back.setVisibility(View.INVISIBLE);
        title.setText(R.string.title_set);
        sub_title.setText(R.string.title_secure);
        sign_up.setText(R.string.btn_set_password);
        back.setVisibility(View.INVISIBLE);
        description.setText(R.string.status_99);
        t-=1;
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