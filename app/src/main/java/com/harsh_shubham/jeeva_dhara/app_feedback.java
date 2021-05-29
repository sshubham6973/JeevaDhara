package com.harsh_shubham.jeeva_dhara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class app_feedback extends AppCompatActivity {
ImageView back;
RatingBar rat_1,rat_2;
EditText review;
Button submit;
private NotificationManagerCompat notificationManagerCompat;
FirebaseAuth firebaseAuth;
FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_2));
        notificationManagerCompat = NotificationManagerCompat.from(this);
        window.setNavigationBarColor(ContextCompat.getColor(this,R.color.color_2));
        rat_1 = findViewById(R.id.ratingBar2);
        rat_2 = findViewById(R.id.ratingBar3);
        review = findViewById(R.id.review_text);
        submit = findViewById(R.id.button9);
        back = findViewById(R.id.imageView9);
        firebaseAuth  = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(app_feedback.this,app_contact_us.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_network_connection()){
                     if(review.getText().toString().trim().isEmpty()){
                         Toast.makeText(getApplicationContext(),"Please give review",Toast.LENGTH_SHORT).show();
                     }else{
                         final DocumentReference docRef = firebaseFirestore.collection("Feedback").document(firebaseAuth.getCurrentUser().getPhoneNumber());
                         docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                             @Override
                             public void onSuccess(DocumentSnapshot documentSnapshot) {
                                 if(documentSnapshot.exists()){
                                     docRef.update("Feedback-1",""+rat_1.getRating());
                                     docRef.update("Feedback-2",""+rat_2.getRating());
                                     docRef.update("Review",review.getText().toString());
                                 }else{
                                     Map<String,Object> status = new HashMap<>();
                                     status.put("Feedback-1",""+rat_1.getRating());
                                     status.put("Feedback-2",""+rat_2.getRating());
                                     status.put("Review",review.getText().toString());
                                     docRef.set(status);
                                 }
                                 Toast.makeText(getApplicationContext(),"Feedback Submitted",Toast.LENGTH_SHORT).show();
                                 startActivity(new Intent(app_feedback.this,app_contact_us.class));
                                 overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                             }
                         });
                     }
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
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