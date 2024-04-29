package com.example.monitoring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "mychannel";
    private static final int NOTIFICATION_ID = 100;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private TextView Temperaturevalue , humidityvalue , heartratevalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.images,null);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//        Bitmap largeIcon = bitmapDrawable.getBitmap();
//
//        NotificationManager mn = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Notification notification;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//             notification = new Notification.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setSmallIcon(R.drawable.images)
//                    .setContentText("Message")
//                    .setSubText("New Message")
//                    .setChannelId(CHANNEL_ID)
//                    .build();
//            mn.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New channel",NotificationManager.IMPORTANCE_HIGH));
//
//        }else{
//            notification = new Notification.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setSmallIcon(R.drawable.images)
//                    .setContentText("Message")
//                    .setSubText("New Message")
//                    .build();
//        }

//        mn.notify(NOTIFICATION_ID,notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("DHT");
        Temperaturevalue = findViewById(R.id.temperature);
        humidityvalue = findViewById(R.id.humidity_value);
        heartratevalue = findViewById(R.id.bpm);

//        getdata();

        Button btnRefresh = findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        initialdata();


    }

    private void initialdata() {
        Temperaturevalue.setText("Temperature : " + "00");
        humidityvalue.setText("Humidity : " + "00");
        heartratevalue.setText("BPM : " + "00");
    }

    private void getdata() {

        // calling add value event listener method
        // for getting the values from database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                String temp_value = snapshot.child("temperature").getValue(String.class);
                String humidity_value = snapshot.child("humidity").getValue(String.class);
                String heart_value = snapshot.child("heartbeat").getValue(String.class);

                int temp = Integer.parseInt(temp_value);
                if(temp>38){

                }


                // after getting the value we are setting
                // our value to our text view in below line.
                Temperaturevalue.setText("Temperature : " + temp_value);
                humidityvalue.setText("Humidity : " + humidity_value);
                heartratevalue.setText("BPM : " + heart_value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNotification(){
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.images,null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();
        


        NotificationManager mn = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.images)
                    .setContentText("Message")
                    .setSubText("New Message")
                    .setChannelId(CHANNEL_ID)
                    .build();
            mn.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New channel",NotificationManager.IMPORTANCE_HIGH));

        }else{
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.images)
                    .setContentText("Message")
                    .setSubText("New Message")
                    .build();
        }

    }
}
