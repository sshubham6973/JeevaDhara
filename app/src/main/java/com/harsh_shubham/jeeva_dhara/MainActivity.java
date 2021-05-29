package com.harsh_shubham.jeeva_dhara;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Button sign_out, donate, receive, who_can, facts, founders, contact;
    ScrollView scrollView;
    Dialog dialog;
    ImageView img_profile, img_report;
    TextView head, blood_head, c_news_1, c_news_2;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.main_scroll_bar);
        img_profile = findViewById(R.id.image_profile);
        img_report = findViewById(R.id.image_report);
        scrollView.setVerticalScrollBarEnabled(false);
        head = findViewById(R.id.title_hello);
        donate = findViewById(R.id.main_donate);
        blood_head = findViewById(R.id.title_blood);
        c_news_1 = findViewById(R.id.critical_news_1);
        receive = findViewById(R.id.button6);
        c_news_2 = findViewById(R.id.critical_news_2);
        who_can = findViewById(R.id.button4);
        facts = findViewById(R.id.button8);
        founders = findViewById(R.id.main_founders);
        contact = findViewById(R.id.main_contact_us);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        make_report();
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_network_connection()) {
                    try {
                        if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1) {
                            DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        String last_date = documentSnapshot.getString("LAD");
                                        String request = documentSnapshot.getString("Request");
                                        Log.d("TAGG", "" + last_date + "  " + request);
                                        if (last_date.equals("")) {
                                            startActivity(new Intent(MainActivity.this, app_donate_blood.class));
                                        } else {
                                            Calendar calendar = Calendar.getInstance();
                                            final String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime()).replace("/", ".");
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                            try {
                                                Date date = sdf.parse(current_date);
                                                Date date1 = sdf.parse(last_date);
                                                long timeDiff = date.getTime() - date1.getTime();
                                                int days = (int) TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                                                Log.d("TAGG", "" + days);
                                                if (request.equals("R")) {
                                                    if (days <= 60) {
                                                        int rem_days = 60 - days;
                                                        Toast.makeText(getApplicationContext(), "Wait for " + rem_days + " days ", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        startActivity(new Intent(MainActivity.this, app_donate_blood.class));
                                                    }
                                                } else {
                                                    if (request.equals("W")) {
                                                        if (days <= 7) {
                                                            int rem_days = 7 - days;
                                                            Toast.makeText(getApplicationContext(), "Wait for " + rem_days + " days", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            startActivity(new Intent(MainActivity.this, app_donate_blood.class));
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enable auto date and time in settings", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    DocumentReference documentReference;
                    String f_name = documentSnapshot.getString("First-Name");
                    String blood_group = documentSnapshot.getString("Blood Group");
                    String gender = documentSnapshot.getString("Sex");
                    String branch = documentSnapshot.getString("Branch");
                    head.setText("Hello," + f_name);
                    switch (blood_group) {
                        case "A+":
                            blood_head.setText("I am A+ (A-positive)");
                            break;
                        case "A-":
                            blood_head.setText("I am A- (A-negative)");
                            break;
                        case "B+":
                            blood_head.setText("I am B+ (B-positive)");
                            break;
                        case "B-":
                            blood_head.setText("I am B- (B-negative)");
                            break;
                        case "AB+":
                            blood_head.setText("I am AB+ (Universal Receiver)");
                            break;
                        case "AB-":
                            blood_head.setText("I am AB- (AB-negative)");
                            break;
                        case "O+":
                            blood_head.setText("I am O+ (Can donate to A+,B+,AB+,O+)");
                            break;
                        case "O-":
                            blood_head.setText("I am O- (Universal Donor)");
                            break;
                    }
                    switch (gender) {
                        case "Male":
                            img_profile.setImageResource(R.drawable.user_image_male);
                            img_report.setImageResource(R.drawable.user_image_male);
                            break;
                        case "Female":
                            img_profile.setImageResource(R.drawable.user_image_female);
                            img_report.setImageResource(R.drawable.user_image_female);
                            break;
                    }
                    DocumentReference docRef_1 = firebaseFirestore.collection("public_critical_news").document("" + branch);
                    docRef_1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String news_1 = documentSnapshot.getString("Message-1");
                            String news_2 = documentSnapshot.getString("Message-2");
                            c_news_1.setText(news_1);
                            c_news_2.setText(news_2);
                        }
                    });
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dialog = new Dialog(this);

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_network_connection()) {
                    try {
                        if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1) {
                            startActivity(new Intent(MainActivity.this, app_receive_blood.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enable auto date and time", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        who_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, app_who_can.class));
            }
        });
        facts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, app_blood_facts.class));
            }
        });
        founders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, app_founders.class));
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, app_contact_us.class));
            }
        });

    }

    private void make_report() {
        final TextView name, group, date, hem, rbc, hct, mcv, mch, mchc;
        name = findViewById(R.id.report_name);
        group = findViewById(R.id.report_bg);
        date = findViewById(R.id.report_date);
        hem = findViewById(R.id.report_hem);
        rbc = findViewById(R.id.report_rbc);
        hct = findViewById(R.id.report_hct);
        mcv = findViewById(R.id.report_mcv);
        mch = findViewById(R.id.report_mch);
        mchc = findViewById(R.id.report_mchc);
        DocumentReference docRef_10 = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef_10.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String data_name = documentSnapshot.getString("First-Name");
                    String data_bg = documentSnapshot.getString("Blood Group");
                    String data_date = documentSnapshot.getString("Report-Date");
                    String data_hem = documentSnapshot.getString("Haemoglobin");
                    String data_rbc = documentSnapshot.getString("RBC");
                    String data_hct = documentSnapshot.getString("HCT");
                    String data_mcv = documentSnapshot.getString("MCV");
                    String data_mch = documentSnapshot.getString("MCH");
                    String data_mchc = documentSnapshot.getString("MCHC");

                    name.setText(data_name);
                    group.setText(data_bg);
                    date.setText("Dated : " + data_date);
                    hem.setText(data_hem);
                    rbc.setText(data_rbc);
                    hct.setText(data_hct);
                    mcv.setText(data_mcv);
                    mch.setText(data_mch);
                    mchc.setText(data_mchc);

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        final LatLng pos_101 = new LatLng(19.0377316, 72.9474448);
        final LatLng pos_102 = new LatLng(19.216396, 72.989792);
        final LatLng pos_103 = new LatLng(19.130727, 72.833503);
        map.addMarker(new MarkerOptions().position(pos_101).title("M-101"));
        map.addMarker(new MarkerOptions().position(pos_102).title("M-102"));
        map.addMarker(new MarkerOptions().position(pos_103).title("M-103"));
        map.setMinZoomPreference(11);
        map.setMaxZoomPreference(11);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        DocumentReference docRef_00 = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef_00.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String branch = documentSnapshot.getString("Branch");
                    switch (branch){
                        case "M-101": map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_101,11));
                                      break;
                        case "M-102": map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_102,11));
                                      break;
                        case "M-103": map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos_103,11));
                                      break;
                    }
                }
            }
        });

    }

    public void show_dialog(View view) {
        try {
            if(Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1){
                final ImageView back,dialog_profile;
                final TextView blood_gp,name,dob,email,branch,phone;
                final Button sign_out;
                dialog.setContentView(R.layout.app_profile_menu);
                back = dialog.findViewById(R.id.dialog_back);
                blood_gp = dialog.findViewById(R.id.profile_bg);
                dialog_profile = dialog.findViewById(R.id.dialog_profile);
                name = dialog.findViewById(R.id.profile_name);
                dob = dialog.findViewById(R.id.profile_dob);
                email = dialog.findViewById(R.id.profile_email);
                branch = dialog.findViewById(R.id.profile_branch);
                phone = dialog.findViewById(R.id.profile_phone);
                back.setImageResource(R.drawable.back_button);
                sign_out = dialog.findViewById(R.id.profile_sign_out);

                DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Calendar calendar;
                            String gender = documentSnapshot.getString("Sex");
                            String blood_grp = documentSnapshot.getString("Blood Group");
                            String f_name = documentSnapshot.getString("First-Name");
                            String l_name = documentSnapshot.getString("Last-Name");
                            String date_of_birth = documentSnapshot.getString("DOB");
                            String e_id = documentSnapshot.getString("Email-id");
                            String br = documentSnapshot.getString("Branch");
                            switch (gender){
                                case "Male" : dialog_profile.setImageResource(R.drawable.user_image_male);
                                    break;
                                case "Female" : dialog_profile.setImageResource(R.drawable.user_image_female);
                                    break;
                            }
                            blood_gp.setText(blood_grp);
                            String full_name = f_name+" "+l_name;
                            name.setText(full_name);
                            dob.setText("DOB: "+date_of_birth);
                            email.setText(e_id);
                            branch.setText("Preferred Branch: "+br);
                            String sh_phone = firebaseAuth.getCurrentUser().getPhoneNumber().substring(3,13);
                            phone.setText("Linked Phone number: "+sh_phone);
                        }
                    }
                });
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            } else
            {
                Toast.makeText(getApplicationContext(),"Please enable Auto date and time in Settings",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void signed_out(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        documentReference.update("log_in_status","false");
        documentReference.update("otp_verified","false");
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this,app_hello_there.class));
        finish();
        Toast.makeText(MainActivity.this,"Sign Out Successful",Toast.LENGTH_SHORT).show();
    }

    public void change_details(View view){
        if(check_network_connection()){
            startActivity(new Intent(MainActivity.this,app_change_details.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }

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