package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class app_change_details extends AppCompatActivity {

    int MAX_VALID_YR = 2021;
    int MIN_VALID_YR = 1947;

    ImageView photo;
    RadioButton c_m101,c_m102,c_m103;
    EditText fname,lname,dob,pin;
    String final_fname,final_lname,final_dob,final_branch,entered_pin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Button update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_change_details);
        c_m101 = findViewById(R.id.branch_changed_m_101);
        c_m102 = findViewById(R.id.branch_changed_m_102);
        c_m103 = findViewById(R.id.branch_changed_m_103);
        fname = findViewById(R.id.changed_fname);
        lname = findViewById(R.id.changed_lname);
        photo = findViewById(R.id.image_gender);
        dob = findViewById(R.id.changed_dob);
        pin = findViewById(R.id.changed_pin);
        update = findViewById(R.id.button13);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    fname.setText(documentSnapshot.getString("First-Name"));
                    lname.setText(documentSnapshot.getString("Last-Name"));
                    dob.setText(documentSnapshot.getString("DOB"));
                    String branch = documentSnapshot.getString("Branch");
                    String gender = documentSnapshot.getString("Sex");
                    final String correct_pin = documentSnapshot.getString("PIN");

                    switch (gender){
                        case "Male":
                            photo.setImageResource(R.drawable.user_image_male);
                            break;
                        case "Female":
                            photo.setImageResource(R.drawable.user_image_female);
                            break;
                    }
                    switch(branch){
                        case "M-101":
                             c_m101.setChecked(true);
                             c_m101.setTextColor(getResources().getColor(R.color.color_1));
                             final_branch = "M-101";
                             break;
                        case "M-102":
                            c_m102.setChecked(true);
                            c_m102.setTextColor(getResources().getColor(R.color.color_1));
                            final_branch = "M-102";
                            break;
                        case "M-103":
                            c_m103.setChecked(true);
                            c_m103.setTextColor(getResources().getColor(R.color.color_1));
                            final_branch = "M-103";
                            break;
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final_fname = fname.getText().toString().trim();
                            final_lname = lname.getText().toString().trim();
                            final_dob = dob.getText().toString().trim();
                            entered_pin = pin.getText().toString().trim();

                            if(check_network_connection()){
                                if(final_fname.isEmpty() || final_lname.isEmpty() || final_dob.isEmpty() || entered_pin.isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),"Fields cannot be empty.",Toast.LENGTH_SHORT).show();
                                }else
                                {
                                       try{
                                           if(Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1){
                                               if(final_fname.length()>=3 && final_lname.length()>=3){
                                                   if(final_dob.substring(2,3).equals(".") && final_dob.substring(5,6).equals(".")){
                                                       int day = Integer.parseInt(dob.getText().toString().substring(0, 2));
                                                       int month = Integer.parseInt(dob.getText().toString().substring(3, 5));
                                                       int year = Integer.parseInt(dob.getText().toString().substring(6, 10));
                                                       if (year >= MIN_VALID_YR && year <= MAX_VALID_YR) {
                                                           if(isValidDate(day,month,year)){
                                                               Calendar calendar = Calendar.getInstance();
                                                               String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
                                                               int current_year = Integer.parseInt(current_date.substring(6,10));
                                                               if(current_year - year >=18){
                                                                   if(entered_pin.equals(correct_pin)){
                                                                       docRef.update("First-Name",final_fname);
                                                                       docRef.update("Last-Name",final_lname);
                                                                       docRef.update("DOB",final_dob);
                                                                       docRef.update("Branch",final_branch);
                                                                       Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                                                                       startActivity(new Intent(app_change_details.this,MainActivity.class));
                                                                       finish();

                                                                   }else
                                                                   {
                                                                       Toast.makeText(getApplicationContext(),"PIN is incorrect",Toast.LENGTH_SHORT).show();
                                                                   }
                                                               }else
                                                               {
                                                                   Toast.makeText(getApplicationContext(),"Entered date is not 18+",Toast.LENGTH_SHORT).show();
                                                               }

                                                           }else{
                                                               Toast.makeText(getApplicationContext(),"Invalid Date entered",Toast.LENGTH_SHORT).show();
                                                           }
                                                       }else{
                                                           Toast.makeText(getApplicationContext(),"Invalid Year",Toast.LENGTH_SHORT).show();
                                                       }
                                                   }else{
                                                       Toast.makeText(getApplicationContext(),"Invalid Date Format",Toast.LENGTH_SHORT).show();
                                                   }
                                               }else{
                                                   Toast.makeText(getApplicationContext(),"Entered name is too small",Toast.LENGTH_SHORT).show();
                                               }
                                           }else{
                                               Toast.makeText(getApplicationContext(),"Please enable Automatic Date and time",Toast.LENGTH_SHORT).show();
                                           }
                                       }catch (Settings.SettingNotFoundException e){
                                           e.printStackTrace();
                                       }
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    public void changed_back_clicked(View view){
        startActivity(new Intent(app_change_details.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
   public void changed_branch_selector(View view){
       boolean is_selected = ((AppCompatRadioButton)view).isChecked();
       switch (view.getId()) {
           case R.id.branch_changed_m_101:
               if (is_selected) {
                   c_m101.setTextColor(getResources().getColor(R.color.color_1));
                   c_m102.setTextColor(getResources().getColor(R.color.color_2));
                   c_m103.setTextColor(getResources().getColor(R.color.color_2));
                   final_branch = "M-101";
               }
               break;
           case R.id.branch_changed_m_102:
               if (is_selected) {
                   c_m101.setTextColor(getResources().getColor(R.color.color_2));
                   c_m102.setTextColor(getResources().getColor(R.color.color_1));
                   c_m103.setTextColor(getResources().getColor(R.color.color_2));
                   final_branch = "M-102";
               }
               break;
           case R.id.branch_changed_m_103:
               if (is_selected) {
                   c_m101.setTextColor(getResources().getColor(R.color.color_2));
                   c_m102.setTextColor(getResources().getColor(R.color.color_2));
                   c_m103.setTextColor(getResources().getColor(R.color.color_1));
                   final_branch = "M-103";
               }
               break;
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

    public boolean isLeap(int year)
    {
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }
    public boolean isValidDate(int d,
                               int m,
                               int y) {

        if (y > MAX_VALID_YR ||
                y < MIN_VALID_YR)
            return false;
        if (m < 1 || m > 12)
            return false;
        if (d < 1 || d > 31)
            return false;
        if (m == 2)
        {
            if (isLeap(y))
                return (d <= 29);
            else
                return (d <= 28);
        }
        if (m == 4 || m == 6 ||
                m == 9 || m == 11)
            return (d <= 30);

        return true;
    }
}