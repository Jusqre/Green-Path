package com.example.tmapgreentest;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPolyLine;

import java.util.ArrayList;

public class RecommendTrail extends Thread{
    //private static final String TAG = "Recommend";
    static final ArrayList<TMapMarkerItem> recommendedMarker = new ArrayList<>();
    static double recommendArea; // 산책로 추천 범위
    public RecommendTrail() {}

    public void run()
    {
        for (int i=0; i< recommendedMarker.size(); i++)
        {
            TMapPolyLine tol = new TMapPolyLine();
            tol.addLinePoint(MainActivity.userPoint);
            tol.addLinePoint(recommendedMarker.get(i).getTMapPoint());
            if (tol.getDistance() <= recommendArea)
            {
                MainActivity.tMapView.addMarkerItem("q" + i, recommendedMarker.get(i));
                recommendedMarker.get(i).setVisible(TMapMarkerItem.VISIBLE);
            }
        }
    }

}