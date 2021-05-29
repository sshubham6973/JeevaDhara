package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.harsh_shubham.jeeva_dhara.R.color.color_1;

public class app_user_registration extends AppCompatActivity {
    private static final String TAG ="TAG" ;
    int MAX_VALID_YR = 2021;
    int MIN_VALID_YR = 1947;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    RadioButton r_male,r_female,b_1_a,b_1_b,b_1_ab,b_1_o,b_2_plus,b_2_minus;
    RadioButton m101,m102,m103;
    ImageView imageView;
    ScrollView sc_view;
    Button register;
    String blood_a,blood_b;
    EditText first,last,dob,email;
    public String first_name,last_name,sex,date_of_birth,blood_group,email_id,preferred_branch;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_user_registration);
        // Radio Buttons
        r_male= findViewById(R.id.radio_male);
        r_female = findViewById(R.id.radio_female);
        imageView = findViewById(R.id.img_view);
        b_1_a = findViewById(R.id.radio_a);
        b_1_b = findViewById(R.id.radio_b);
        b_1_ab = findViewById(R.id.radio_ab);
        b_1_o = findViewById(R.id.radio_o);
        b_2_plus = findViewById(R.id.radio_plus);
        b_2_minus= findViewById(R.id.radio_minus);
        sc_view = findViewById(R.id.scrollView2);
        m101 = findViewById(R.id.branch_m_101);
        m102 = findViewById(R.id.branch_m_102);
        m103 = findViewById(R.id.branch_m_103);
        blood_a = "A";
        blood_b = "+";
        preferred_branch = "M-101";
        sex = "Male";
        checkBox = findViewById(R.id.checkBox);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        sc_view.setVerticalScrollBarEnabled(false);

        // Data
        first = findViewById(R.id.id_first_name);
        last = findViewById(R.id.id_last_name);
        dob = findViewById(R.id.id_date_of_birth);
        email = findViewById(R.id.id_email_id);
    }

    private void hello_78() {
        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(app_user_registration.this,app_set_password.class);
        startActivity(i);
        finish();
    }

    public void Radio_button_selected(View view){
        boolean is_selected = ((AppCompatRadioButton)view).isChecked();
        switch (view.getId()){
            case R.id.radio_male:
                if(is_selected){
                    r_male.setTextColor(getResources().getColor(R.color.color_1));
                    r_female.setTextColor(getResources().getColor(R.color.color_2));
                    imageView.setImageResource(R.drawable.user_image_male);
                    sex = "Male";
                }
                break;
            case R.id.radio_female:
                if(is_selected){
                    r_male.setTextColor(getResources().getColor(R.color.color_2));
                    r_female.setTextColor(getResources().getColor(R.color.color_1));
                    imageView.setImageResource(R.drawable.user_image_female);
                    sex = "Female";
                }
                break;
        }
    }

    public void blood_part_a(View view) {
        boolean is_selected = ((AppCompatRadioButton)view).isChecked();
        switch (view.getId()){
            case R.id.radio_a:
                if(is_selected){
                    b_1_a.setTextColor(getResources().getColor(R.color.color_1));
                    b_1_b.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_ab.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_o.setTextColor(getResources().getColor(R.color.color_2));
                    blood_a = "A";
                }
                break;
            case R.id.radio_b:
                if(is_selected){
                    b_1_a.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_b.setTextColor(getResources().getColor(R.color.color_1));
                    b_1_ab.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_o.setTextColor(getResources().getColor(R.color.color_2));
                    blood_a = "B";
                }
                break;
            case R.id.radio_ab:
                if(is_selected){
                    b_1_a.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_b.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_ab.setTextColor(getResources().getColor(R.color.color_1));
                    b_1_o.setTextColor(getResources().getColor(R.color.color_2));
                    blood_a = "AB";
                }
                break;
            case R.id.radio_o:
                if(is_selected){
                    b_1_a.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_b.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_ab.setTextColor(getResources().getColor(R.color.color_2));
                    b_1_o.setTextColor(getResources().getColor(R.color.color_1));
                    blood_a = "O";
                }
                break;
        }

    }

    public void blood_part_b(View view) {
        boolean is_selected = ((AppCompatRadioButton)view).isChecked();
        switch (view.getId()) {
            case R.id.radio_plus:
                if (is_selected) {
                    b_2_plus.setTextColor(getResources().getColor(R.color.color_1));
                    b_2_minus.setTextColor(getResources().getColor(R.color.color_2));
                    blood_b = "+";
                }
                break;
            case R.id.radio_minus:
                if (is_selected) {
                    b_2_plus.setTextColor(getResources().getColor(R.color.color_2));
                    b_2_minus.setTextColor(getResources().getColor(R.color.color_1));
                    blood_b = "-";
                }
                break;
        }
    }

    public void branch_selector(View view){
        boolean is_selected = ((AppCompatRadioButton)view).isChecked();
        switch (view.getId()) {
            case R.id.branch_m_101:
                if (is_selected) {
                    m101.setTextColor(getResources().getColor(R.color.color_1));
                    m102.setTextColor(getResources().getColor(R.color.color_2));
                    m103.setTextColor(getResources().getColor(R.color.color_2));
                    preferred_branch = "M-101";
                }
                break;
            case R.id.branch_m_102:
                if (is_selected) {
                    m101.setTextColor(getResources().getColor(R.color.color_2));
                    m102.setTextColor(getResources().getColor(R.color.color_1));
                    m103.setTextColor(getResources().getColor(R.color.color_2));
                    preferred_branch = "M-102";
                }
                break;
            case R.id.branch_m_103:
                if (is_selected) {
                    m101.setTextColor(getResources().getColor(R.color.color_2));
                    m102.setTextColor(getResources().getColor(R.color.color_2));
                    m103.setTextColor(getResources().getColor(R.color.color_1));
                    preferred_branch = "M-103";
                }
                break;
        }
    }

    public boolean isLeap(int year)
    {
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }

    public void jhunjhunwala(View view){
        boolean net = check_network_connection();
        boolean checked = checkBox.isChecked();
        if(net)
        {

            first_name= first.getText().toString().trim();
            last_name = last.getText().toString().trim();
            date_of_birth = dob.getText().toString().trim();
            email_id = email.getText().toString().trim();
            if(first_name.isEmpty() || last_name.isEmpty() || date_of_birth.isEmpty() || email_id.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Fields cannot be empty.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    if(Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 1) {
                        if (!checked) {
                            Toast.makeText(getApplicationContext(), "Please accept our policy.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(date_of_birth.substring(2,3).equals(".") && date_of_birth.substring(5,6).equals("."))
                            {
                                int day = Integer.parseInt(dob.getText().toString().substring(0, 2));
                                int month = Integer.parseInt(dob.getText().toString().substring(3, 5));
                                int year = Integer.parseInt(dob.getText().toString().substring(6, 10));
                                if (year >= MIN_VALID_YR && year <= MAX_VALID_YR) {
                                    if(isValidDate(day,month,year))
                                    {
                                        Calendar calendar = Calendar.getInstance();
                                        String current_date = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
                                        int current_year = Integer.parseInt(current_date.substring(6,10));
                                        if(current_year - year >=18)
                                        {
                                            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                                            if (email.getText().toString().trim().matches(emailPattern))
                                            {
                                                if(first_name.length() >=3 && last_name.length()>=3)
                                                {
                                                    first_name = first.getText().toString().trim();
                                                    last_name = last.getText().toString().trim();
                                                    date_of_birth = dob.getText().toString().trim();
                                                    email_id = email.getText().toString().trim();
                                                    blood_group = blood_a + blood_b;
                                                    DocumentReference documentReference1 = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                                                    documentReference1.update("First-Name",first_name);
                                                    documentReference1.update("Last-Name",last_name);
                                                    documentReference1.update("DOB",date_of_birth);
                                                    documentReference1.update("Sex",sex);
                                                    documentReference1.update("Blood Group",blood_group);
                                                    documentReference1.update("Email-id",email_id);
                                                    documentReference1.update("Branch",preferred_branch);
                                                    hello_78();
                                                }else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Entered name is too small",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),"Invalid Email Id.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"You are not 18+.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else
                                    {
                                        Toast.makeText(getApplicationContext(),"Invalid Date entered",Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Invalid Year",Toast.LENGTH_SHORT).show();
                                }

                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Date Format",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please enable Automatic Date and time in settings.",Toast.LENGTH_SHORT).show();
                    }

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
        }
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