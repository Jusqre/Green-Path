package com.example.tmapgreentest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Objects;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

// 1m 당 lat = 0.000009, long = 0.0000113

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback
{

    Button buttonRecommend; // 산책로 추천 버튼
    Button buttonMakeTrail; // 산책로 생성버튼


    static double enteredDistance; // 입력한 산책로 생성 길이

    //티맵 호출
    static TMapView tMapView = null;
    static TMapPoint userPoint = new TMapPoint(37.279669, 127.043504);

    static double latA = 0;
    static double lngA =0;
    static MakeTrail maketr1;
    static TMapMarkerItem UserLocation = new TMapMarkerItem();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tMapView = new TMapView(this);
        Matrix matrix = new Matrix();
        Bitmap ubi = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        matrix.preScale(0.2f, 0.2f);
        ubi = Bitmap.createBitmap(ubi, 0, 0, ubi.getWidth(), ubi.getHeight(), matrix, false);
        UserLocation.setIcon(ubi);

        //버튼 레이아웃 지정
        buttonRecommend = findViewById(R.id.buttonRecommend);
        buttonMakeTrail = findViewById(R.id.buttonMakeTrail);
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.tmap);
        tMapView.setSKTMapApiKey( BuildConfig.tmaps_key );
        linearLayoutTmap.addView( tMapView );

        //티맵 유저 로케이션
        Bitmap rbi = BitmapFactory.decodeResource(getResources(), R.drawable.poi_star);

        TMapGpsManager gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        gps.OpenGps();
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        tMapView.setCenterPoint(gps.getLocation().getLongitude(),gps.getLocation().getLatitude());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tgreentest").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String localName = (String) document.getData().get("name");
                            latA = document.getDouble("relat");
                            lngA = document.getDouble("relng");

                            TMapMarkerItem at= new TMapMarkerItem();
                            at.setIcon(rbi);
                            at.setTMapPoint(new TMapPoint(latA, lngA));
                            at.setName(localName);
                            at.setCanShowCallout(true);
                            at.setCalloutTitle(localName);

                            double k = document.getDouble("area");
                            int redis = (int) Math.round(Math.sqrt(k * 4 * 3.14));
                            int tim = (int) (redis * 60 / 5000);
                            at.setCalloutSubTitle("길이 : " + redis + "m/" + "소요 시간 : " + tim + "분");

                            RecommendTrail.recommendedMarker.add(at);
                        }
                    }
                });



            //===============================산책로 추천=====================

        final AlertDialog.Builder recommendAlert = new AlertDialog.Builder(this);

        recommendAlert.setTitle("산책로 추천");
        recommendAlert.setMessage("산책로 추천 범위를 입력하십시오.(m)");
        final EditText recommendInput = new EditText(this);
        recommendAlert.setView(recommendInput);
        RecommendTrail Recommend = new RecommendTrail();



        recommendAlert.setPositiveButton("OK", (dialog, whichButton) -> {
            if (Recommend.isAlive())
            {
                Recommend.interrupt();
            }
            RecommendTrail Recommend1 = new RecommendTrail();
//

            RecommendTrail.recommendArea = Integer.parseInt(recommendInput.getText().toString());
            if (RecommendTrail.recommendArea != 0)
            {
                Recommend1.start();
            }
        });

        recommendAlert.setNeutralButton("RESET", (dialog, which) -> {
            for (int i =0; i< RecommendTrail.recommendedMarker.size(); i++)
            {
                RecommendTrail.recommendedMarker.get(i).setVisible(TMapMarkerItem.HIDDEN);
            }
        });

        buttonRecommend.setOnClickListener(view -> {
            if (recommendInput.getParent() != null){
                ((ViewGroup) recommendInput.getParent()).removeView(recommendInput);}

            AlertDialog RR = recommendAlert.create();
            RR.show();
        });




        //===============================산책로 생성=====================

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("산책로 생성");
        alert.setMessage("원하는 산책 길이를 입력하십시오.(m)");
        final EditText inputDistance = new EditText(this);
        alert.setView(inputDistance);
        MakeTrail MakeTrail = new MakeTrail();


        alert.setPositiveButton("OK", (dialogInterface, whichButton) -> {
            if (MakeTrail.isAlive())
            {
                MakeTrail.interrupt();
            }
            maketr1 = new MakeTrail();
            enteredDistance = Integer.parseInt(inputDistance.getText().toString());
            if (enteredDistance !=0)
            {
                maketr1.start();
            }
        });

        alert.setNeutralButton("RESET", (dialog, which) -> {
            tMapView.removeAllTMapPolyLine();
            UserLocation.setCanShowCallout(false);
            MakeTrail.interrupt();
        });

        // 산책로 생성 클릭할 때 온클리커
        buttonMakeTrail.setOnClickListener(view -> {
            if (inputDistance.getParent() != null){
                ((ViewGroup) inputDistance.getParent()).removeView(inputDistance);}

            AlertDialog MM = alert.create();
            MM.show();
        });


    }

    @Override
    public void onLocationChange(Location location) {
        boolean tM = true;
        if (tM){
            tMapView.setLocationPoint(location.getLongitude(),location.getLatitude());
            userPoint = tMapView.getLocationPoint();
            moveUser();
        }
    }

    public void moveUser()
    {
        tMapView.removeMarkerItem("User Location");

        UserLocation.setPosition(0.5f, 1.0f);
        UserLocation.setTMapPoint( userPoint );
        UserLocation.setName("사용자 현재 위치");
        tMapView.addMarkerItem("User Location", UserLocation);
    }

    

}

