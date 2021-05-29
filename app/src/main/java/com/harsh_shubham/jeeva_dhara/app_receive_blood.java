package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class app_receive_blood extends AppCompatActivity {

    ImageView back;
    ScrollView scrollView;
    EditText rbc,wbc,des,hos,group;
    CheckBox e_service;
    Button request;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_receive_blood);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        back = findViewById(R.id.founder_back);
        scrollView = findViewById(R.id.scrollView4);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        scrollView.setVerticalScrollBarEnabled(false);

        rbc = findViewById(R.id.receive_rbc);
        wbc = findViewById(R.id.receive_wbc);
        des = findViewById(R.id.receive_purpose);
        hos = findViewById(R.id.receive_hospital);
        group = findViewById(R.id.receive_group);
        e_service = findViewById(R.id.checkBox4);
        request = findViewById(R.id.button5);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_send();
            }
        });

    }

    private void request_send() {
        if(check_network_connection()){
            try{
                if(Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1){
                    final String s_rbc = rbc.getText().toString();
                    final String s_wbc = wbc.getText().toString();
                    final String s_des = des.getText().toString();
                    final String s_hos = hos.getText().toString();
                    final String s_group = group.getText().toString();
                    Calendar calendar = Calendar.getInstance();
                    final String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());

                    if(s_des.isEmpty() || s_hos.isEmpty() || s_rbc.isEmpty() || s_wbc.isEmpty() || s_group.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Please Fill all required fields",Toast.LENGTH_SHORT).show();
                    }else
                    {
                         if(e_service.isChecked()){
                             emergency();
                         }else{
                             final DocumentReference docRef = firebaseFirestore.collection("reception_service").document(""+current_date);
                             docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                 @Override
                                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                                     if(documentSnapshot.exists()){
                                         String get_count = documentSnapshot.getString("Count");
                                         int count = Integer.parseInt(get_count);
                                         count+=1;
                                         String a = ""+count;
                                         String dat = "R/"+s_group+"/"+firebaseAuth.getCurrentUser().getPhoneNumber()+"/"+s_rbc+"/"+s_wbc+"/"+s_hos+"/"+s_des;
                                         docRef.update("Count",a);
                                         docRef.update("Request-"+a , dat);

                                     }else {
                                         Map<String,Object> status = new HashMap<>();
                                         String data = "R/"+s_group+"/"+firebaseAuth.getCurrentUser().getPhoneNumber()+"/"+s_rbc+"/"+s_wbc+"/"+s_hos+"/"+s_des;
                                         status.put("Count","1");
                                         status.put("Request-1",data);
                                         docRef.set(status);
                                     }
                                     write_history();
                                     Toast.makeText(getApplicationContext(),"Request Successful.",Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enable auto date and time",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }
    }

    private void write_history() {
        final String s_rbc = rbc.getText().toString();
        final String s_wbc = wbc.getText().toString();
        final String s_group = group.getText().toString();
        Calendar calendar = Calendar.getInstance();
        final String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime()).replace("/",".");
        final DocumentReference docRef_4 = firebaseFirestore.collection("private_users_history").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef_4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String get_count = documentSnapshot.getString("Count");
                    int count = Integer.parseInt(get_count);
                    count+=1;
                    String a = ""+count;
                    String dat = "R/"+current_date+"/"+s_group+"/"+s_rbc+"/"+s_wbc;
                    docRef_4.update("Count",a);
                    docRef_4.update("History-"+a , dat);
                }else {
                    Map<String,Object> status = new HashMap<>();
                    String data = "R/"+current_date+"/"+s_group+"/"+s_rbc+"/"+s_wbc;
                    status.put("Count","1");
                    status.put("History-1",data);
                    docRef_4.set(status);
                }
            }
        });
    }

    private void emergency() {
        Calendar calendar = Calendar.getInstance();
        final String s_rbc = rbc.getText().toString();
        final String s_wbc = wbc.getText().toString();
        final String s_des = des.getText().toString();
        final String s_hos = hos.getText().toString();
        final String s_group = group.getText().toString();
        final String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
        final DocumentReference docRef_2 = firebaseFirestore.collection("emergency_service").document(""+current_date);
        docRef_2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String get_count = documentSnapshot.getString("Count");
                    int count = Integer.parseInt(get_count);
                    count+=1;
                    String a = ""+count;
                    String dat = "R/"+s_group+"/"+firebaseAuth.getCurrentUser().getPhoneNumber()+"/"+s_rbc+"/"+s_wbc+"/"+s_hos+"/"+s_des;
                    docRef_2.update("Count",a);
                    docRef_2.update("Emergency-"+a , dat);
                }else {
                    Map<String,Object> status = new HashMap<>();
                    String data = "R/"+s_group+"/"+firebaseAuth.getCurrentUser().getPhoneNumber()+"/"+s_rbc+"/"+s_wbc+"/"+s_hos+"/"+s_des;
                    status.put("Count","1");
                    status.put("Emergency-1",data);
                    docRef_2.set(status);
                }
                write_history();
                Toast.makeText(getApplicationContext(),"Request Successful.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void receive_back_clicked(View view)
    {
        startActivity(new Intent(app_receive_blood.this,MainActivity.class));
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