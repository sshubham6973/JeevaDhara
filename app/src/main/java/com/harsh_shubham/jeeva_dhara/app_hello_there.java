package com.harsh_shubham.jeeva_dhara;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class app_hello_there extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    EditText phone_number,otp;
    String number,otp_entered,verification_id;
    Button next;
    ImageView back;
    int t=0,action=0;
    TextView sts;
    PhoneAuthCredential credential;
    ViewFlipper viewFlipper;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(app_hello_there.this,"OTP Detected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(app_hello_there.this,"Error in Verification",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verification_id= s;
            Toast.makeText(app_hello_there.this,"Code Sent",Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_hello_there);
        phone_number = findViewById(R.id.edit_text_phone);
        otp = findViewById(R.id.edit_text_otp);
        next = findViewById(R.id.button_next);
        viewFlipper = findViewById(R.id.view_flipper);
        sts = findViewById(R.id.text_status);
        back = findViewById(R.id.btn_back);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean net = check_network_connection();
                if(net) {
                    if(action>=1)
                    {
                        Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (t == 1) {
                        String b = otp.getText().toString().trim();
                        action+=1;
                        if (b.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Field cannot be Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if (b.length() == 6) {
                                otp_entered = b;
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(next.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                credential = PhoneAuthProvider.getCredential(verification_id, otp_entered);
                                SigninWithPhone(credential);
                            }

                        }
                    } else {
                        String a = phone_number.getText().toString().trim();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(next.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        if (a.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Field cannot be Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if ((a.substring(0, 1).equals("7") || a.substring(0, 1).equals("8") || a.substring(0, 1).equals("9")) &&
                                    !phone_number.getText().toString().isEmpty() && a.length() == 10) {
                                number = "+91" + a;
                                firebaseAuth = FirebaseAuth.getInstance();
                                firebaseFirestore = FirebaseFirestore.getInstance();
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, app_hello_there.this, mCallback);
                                viewFlipper.setInAnimation(app_hello_there.this, R.anim.slide_in_right);
                                viewFlipper.setOutAnimation(app_hello_there.this, R.anim.slide_out_left);
                                viewFlipper.showNext();
                                sts.setVisibility(View.VISIBLE);
                                back.setVisibility(View.VISIBLE);
                                next.setText("Verify");
                                t = 1;
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(app_hello_there.this,"Otp Login Successful",Toast.LENGTH_SHORT).show();
                    if(task.getResult().getAdditionalUserInfo().isNewUser())
                    {
                        DocumentReference documentReference = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                        Map<String,Object> status = new HashMap<>();
                        status.put("log_in_status","false");
                        status.put("otp_verified","true");
                        status.put("registration_status","false");
                        DocumentReference documentReference1 = firebaseFirestore.collection("private_users_list").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                        Map<String,Object> status1 = new HashMap<>();
                        status1.put("First-Name","null");
                        status1.put("Last-Name","null");
                        status1.put("DOB","null");
                        status1.put("Sex","null");
                        status1.put("Blood Group","null");
                        status1.put("Email-id","null");
                        status1.put("Branch","null");
                        status1.put("Password","null");
                        status1.put("PIN","null");
                        status1.put("LAD","");
                        status1.put("Request","");
                        status1.put("Report-Date","");
                        status1.put("Haemoglobin","");
                        status1.put("RBC","");
                        status1.put("HCT","");
                        status1.put("MCV","");
                        status1.put("MCH","");
                        status1.put("MCHC","");
                        documentReference1.set(status1);
                        documentReference.set(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(app_hello_there.this,app_user_registration.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failure Occurred",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        DocumentReference documentReference = firebaseFirestore.collection("private_login_status").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                        documentReference.update("otp_verified","true");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists())
                                {
                                    String registration = documentSnapshot.getString("registration_status");
                                    if(registration.equals("true"))
                                    {
                                        startActivity(new Intent(app_hello_there.this,app_login_screen.class));
                                        finish();
                                    }
                                    else
                                    {
                                        startActivity(new Intent(app_hello_there.this,app_user_registration.class));
                                        finish();
                                    }
                                }
                            }
                        });

                    }

                }else{
                    Toast.makeText(app_hello_there.this,"Incorrect Otp",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back_clicked(View view){
        viewFlipper.setInAnimation(app_hello_there.this,android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(app_hello_there.this,android.R.anim.slide_out_right);
        viewFlipper.showPrevious();
        next.setText("Next");
        back.setVisibility(View.INVISIBLE);
        sts.setVisibility(View.INVISIBLE);
        t=0;
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