package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class app_donate_blood extends AppCompatActivity {
    EditText password,pin;
    CheckBox checkBox;
    Button schedule;
    String value = "rbc";
    RadioButton rbc,wbc;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_donate_blood);
        Window window = this.getWindow();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        password = findViewById(R.id.donate_password);
        pin = findViewById(R.id.donate_pin);
        checkBox = findViewById(R.id.checkBox3);
        rbc = findViewById(R.id.donate_RBC);
        wbc = findViewById(R.id.donate_platelets);
        schedule = findViewById(R.id.button_schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_network_connection()){
                    try {
                        if(Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1){
                            if(checkBox.isChecked()){
                                if(!(password.getText().toString().isEmpty() || pin.getText().toString().isEmpty())){
                                    DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                String correct_password = documentSnapshot.getString("Password");
                                                String correct_pin = documentSnapshot.getString("PIN");
                                                if(password.getText().toString().trim().equals(correct_password) && pin.getText().toString().trim().equals(correct_pin)){
                                                    place_donation_request();
                                                }else
                                                {
                                                    Toast.makeText(getApplicationContext(),"PIN or Password is incorrect",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }else
                                {
                                    Toast.makeText(getApplicationContext(),"Kindly fill all details",Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Please accept our rules and regulations",Toast.LENGTH_SHORT).show();
                            }
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"Please enable auto date and time",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void donate_back_pressed(View view){
        startActivity(new Intent(app_donate_blood.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void donate_selector(View view) {

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
    private void place_donation_request(){
        final DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    final String f_name = documentSnapshot.getString("First-Name");
                    final String l_name = documentSnapshot.getString("Last-Name");
                    String branch = documentSnapshot.getString("Branch");
                    Calendar calendar = Calendar.getInstance();
                    final String bg = documentSnapshot.getString("Blood Group");
                    if(rbc.isChecked())
                    {
                        value = "rbc";
                    }else
                    {
                        if(wbc.isChecked()){
                            value = "wbc";
                        }
                    }
                    final String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
                    final DocumentReference docRef_4 = firebaseFirestore.collection("private_users_history").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                    switch (branch)  {
                        case "M-101": final DocumentReference docRef_1= firebaseFirestore.collection("public_donation_M-101").document(""+current_date);
                                      docRef_1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                                              if(documentSnapshot.exists()){
                                                     String get_count = documentSnapshot.getString("Count");
                                                     int count = Integer.parseInt(get_count);
                                                     count+=1;
                                                     get_count = ""+count;
                                                     final String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                                     docRef_1.update("Donor-"+get_count,data);
                                                     docRef_1.update("Count",get_count);
                                              }else
                                              {
                                                  String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                                  Map<String,Object> status2 = new HashMap<>();
                                                  String num = "1";
                                                  status2.put("Count",num);
                                                  status2.put("Donor-1",data);
                                                  docRef_1.set(status2);
                                              }
                                              docRef_4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      if(documentSnapshot.exists()){
                                                          String get_count = documentSnapshot.getString("Count");
                                                          int count = Integer.parseInt(get_count);
                                                          count+=1;
                                                          get_count = ""+count;
                                                          String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                          docRef_4.update("History-"+get_count,his_data);
                                                          docRef_4.update("Count",get_count);
                                                      }else {
                                                          Map<String,Object> history = new HashMap<>();
                                                          String num = "1";
                                                          history.put("Count",num);
                                                          String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                          history.put("History-1",his_data);
                                                          docRef_4.set(history);
                                                      }
                                                  }
                                              });
                                          }
                                      });
                                      break;
                        case "M-102": final DocumentReference docRef_2= firebaseFirestore.collection("public_donation_M-102").document(""+current_date);
                                      docRef_2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                      if(documentSnapshot.exists()){
                                        String get_count = documentSnapshot.getString("Count");
                                        int count = Integer.parseInt(get_count);
                                        count+=1;
                                            Toast.makeText(getApplicationContext(),"Sorry,we are full today",Toast.LENGTH_SHORT).show();
                                            get_count = ""+count;
                                            String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                            docRef_2.update("Donor-"+get_count,data);
                                            docRef_2.update("Count",get_count);
                                      }else
                                    {
                                        String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                        Map<String,Object> status2 = new HashMap<>();
                                        String num = "1";
                                        status2.put("Count",num);
                                        status2.put("Donor-1",data);
                                        docRef_2.set(status2);
                                    }
                                    docRef_4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                String get_count = documentSnapshot.getString("Count");
                                                int count = Integer.parseInt(get_count);
                                                count+=1;
                                                get_count = ""+count;
                                                String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                docRef_4.update("History-"+get_count,his_data);
                                                docRef_4.update("Count",get_count);
                                            }else {
                                                Map<String,Object> history = new HashMap<>();
                                                String num = "1";
                                                history.put("Count",num);
                                                String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                history.put("History-1",his_data);
                                                docRef_4.set(history);
                                            }
                                        }
                                    });
                                }
                            });
                                      break;
                        case "M-103": final DocumentReference docRef_3= firebaseFirestore.collection("public_donation_M-103").document(""+current_date);
                            docRef_3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        String get_count = documentSnapshot.getString("Count");
                                        int count = Integer.parseInt(get_count);
                                        count+=1;
                                            Toast.makeText(getApplicationContext(),"Sorry,we are full today",Toast.LENGTH_SHORT).show();
                                            get_count = ""+count;
                                            String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                            docRef_3.update("Donor-"+get_count,data);
                                            docRef_3.update("Count",get_count);
                                    }else
                                    {
                                        String data = ""+firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13)+"/"+value+"/"+bg+"/"+f_name+"/"+l_name;
                                        Map<String,Object> status2 = new HashMap<>();
                                        String num = "1";
                                        status2.put("Count",num);
                                        status2.put("Donor-1",data);
                                        docRef_3.set(status2);
                                    }
                                    docRef_4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                String get_count = documentSnapshot.getString("Count");
                                                int count = Integer.parseInt(get_count);
                                                count+=1;
                                                get_count = ""+count;
                                                String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                docRef_4.update("History-"+get_count,his_data);
                                                docRef_4.update("Count",get_count);
                                            }else {
                                                Map<String,Object> history = new HashMap<>();
                                                String num = "1";
                                                history.put("Count",num);
                                                String his_data = "D/"+current_date.replace("/", ".")+"/"+value;
                                                history.put("History-1",his_data);
                                                docRef_4.set(history);
                                            }
                                        }
                                    });
                                }
                            });
                            break;
                    }
                    docRef.update("LAD",current_date.replace("/","."));
                    if(value.equals("rbc")){
                        docRef.update("Request","R");
                    }else
                    {
                        if(value.equals("wbc")){
                            docRef.update("Request","W");
                        }
                    }
                    Toast.makeText(getApplicationContext(),"Request successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(app_donate_blood.this,MainActivity.class));
                }
            }
        });
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