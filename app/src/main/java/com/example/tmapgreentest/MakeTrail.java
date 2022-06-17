package com.example.tmapgreentest;

import android.graphics.Color;
import android.util.Log;

import com.skt.Tmap.TMapData;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import static com.example.tmapgreentest.MainActivity.UserLocation;
import static com.example.tmapgreentest.MainActivity.enteredDistance;
import static com.example.tmapgreentest.MainActivity.tMapView;
import static com.example.tmapgreentest.MainActivity.userPoint;

public class MakeTrail extends Thread {

    private static final String TAG = "MakeTrail";

    static ArrayList<Integer> firstQuadrant, secondQuadrant, thirdQuadrant, fourthQuadrant;
    ArrayList<TMapPoint> randomMarkerArray, arPoint;
    Random rand = new Random();
    TMapPolyLine typ;
    static Double totalDistance = (double) 0;
    static Double[] distanceMap;
    final TMapData tmapdata = new TMapData();
    static int phase;

    public MakeTrail() {
    }

    public void run() {

//        TMapCircle tMapCircle = new TMapCircle();
//        tMapCircle.setCenterPoint(userPoint);
//        tMapCircle.setRadius(enteredDistance / 2);
//        tMapCircle.setCircleWidth(2);
//        tMapCircle.setLineColor(Color.BLUE);
//        tMapCircle.setAreaColor(Color.GRAY);
//        tMapCircle.setAreaAlpha(100);
//        tMapView.addTMapCircle("ci",tMapCircle);

        totalDistance = (double) 0;
        Log.d(TAG, firstQuadrant + "/" + secondQuadrant + "/" + thirdQuadrant + "/" + fourthQuadrant);
        MakeRandomMarker();
        DirectionSearcher(userPoint);
        phase = 0;
        int R = (int) (Math.random() * 5);
        while (true) {

            if (R == 0 || R == 5) {
                R = (int) (Math.random() * 5);
            }

            if (R == 1) {
                if (phase == 0) {
                    arPoint = null;
                    getDistance(userPoint, 0, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, userPoint, (TMapPoint) randomMarkerArray.get(firstQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLACK);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = typ.getDistance();
                        arPoint = typ.getLinePoint();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException | ParserConfigurationException | SAXException e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 1 : " + totalDistance);
                } else if (phase == 1) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 1, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(secondQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLUE);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 2 : " + totalDistance);
                } else if (phase == 2) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 2, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(thirdQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.RED);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 3 : " + totalDistance);
                } else if (phase == 3) {
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), userPoint);
                        typ.setLineColor(Color.YELLOW);
                        tMapView.addTMapPolyLine("" + rand.nextInt(30), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();

                        UserLocation.setCanShowCallout(true);
                        UserLocation.setCalloutTitle("총 거리 : " + (int) Math.round(totalDistance) + "m");
                        int tim = (int) (((int) Math.round(totalDistance)) * 60 / 5000);
                        UserLocation.setCalloutSubTitle("소요시간 : " + tim + "분");
                        tMapView.addMarkerItem("ed", UserLocation);

                        Log.d(TAG, "탐색 종료 : " + totalDistance);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                } else if (phase == 4) {
                    if (totalDistance <= 1.2 * enteredDistance && totalDistance >= 0.8 * enteredDistance) {
                        break;
                    } else {
                        tMapView.removeAllTMapPolyLine();
                        MakeRandomMarker();
                        DirectionSearcher(userPoint);
                        phase = 0;
                        R = (int) (Math.random() * 5);
                        totalDistance = (double) 0;
                        Log.d(TAG, "생성 실패");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (R == 2) {
                if (phase == 0) {
                    arPoint = null;
                    getDistance(userPoint, 0, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, userPoint, (TMapPoint) randomMarkerArray.get(secondQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLACK);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = typ.getDistance();
                        arPoint = typ.getLinePoint();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 1 : " + totalDistance);
                } else if (phase == 1) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 1, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(thirdQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLUE);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 2 : " + totalDistance);
                } else if (phase == 2) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 2, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(fourthQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.RED);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 3 : " + totalDistance);
                } else if (phase == 3) {
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), userPoint);
                        typ.setLineColor(Color.YELLOW);
                        tMapView.addTMapPolyLine("" + rand.nextInt(30), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();

                        UserLocation.setCanShowCallout(true);
                        UserLocation.setCalloutTitle("총 거리 : " + (int) Math.round(totalDistance) + "m");
                        int tim = (int) (((int) Math.round(totalDistance)) * 60 / 5000);
                        UserLocation.setCalloutSubTitle("소요시간 : " + tim + "분");
                        tMapView.addMarkerItem("ed", UserLocation);

                        Log.d(TAG, "탐색 종료 : " + totalDistance);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                } else if (phase == 4) {
                    if (totalDistance <= 1.2 * enteredDistance && totalDistance >= 0.8 * enteredDistance) {
                        break;
                    } else {
                        tMapView.removeAllTMapPolyLine();
                        MakeRandomMarker();
                        DirectionSearcher(userPoint);
                        phase = 0;
                        R = (int) (Math.random() * 5);
                        totalDistance = (double) 0;
                        Log.d(TAG, "생성 실패");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (R == 3) {
                if (phase == 0) {
                    arPoint = null;
                    getDistance(userPoint, 0, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, userPoint, (TMapPoint) randomMarkerArray.get(thirdQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLACK);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = typ.getDistance();
                        arPoint = typ.getLinePoint();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 1 : " + totalDistance);
                } else if (phase == 1) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 1, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(fourthQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLUE);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 2 : " + totalDistance);
                } else if (phase == 2) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 2, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(firstQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.RED);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 3 : " + totalDistance);
                } else if (phase == 3) {
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), userPoint);
                        typ.setLineColor(Color.YELLOW);
                        tMapView.addTMapPolyLine("" + rand.nextInt(30), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();

                        UserLocation.setCanShowCallout(true);
                        UserLocation.setCalloutTitle("총 거리 : " + (int) Math.round(totalDistance) + "m");
                        int tim = (int) (((int) Math.round(totalDistance)) * 60 / 5000);
                        UserLocation.setCalloutSubTitle("소요시간 : " + tim + "분");
                        tMapView.addMarkerItem("ed", UserLocation);

                        Log.d(TAG, "탐색 종료 : " + totalDistance);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                } else if (phase == 4) {
                    if (totalDistance <= 1.2 * enteredDistance && totalDistance >= 0.8 * enteredDistance) {
                        break;
                    } else {
                        tMapView.removeAllTMapPolyLine();
                        MakeRandomMarker();
                        DirectionSearcher(userPoint);
                        phase = 0;
                        R = (int) (Math.random() * 5);
                        totalDistance = (double) 0;
                        Log.d(TAG, "생성 실패");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (R == 4) {
                if (phase == 0) {
                    arPoint = null;
                    getDistance(userPoint, 0, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, userPoint, (TMapPoint) randomMarkerArray.get(fourthQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLACK);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = typ.getDistance();
                        arPoint = typ.getLinePoint();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 1 : " + totalDistance);
                } else if (phase == 1) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 1, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(firstQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.BLUE);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 2 : " + totalDistance);
                } else if (phase == 2) {
                    DirectionSearcher(arPoint.get(arPoint.size() - 1));
                    getDistance(arPoint.get(arPoint.size() - 1), 2, R);
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), (TMapPoint) randomMarkerArray.get(secondQuadrant.get(SuitablePoint())));
                        typ.setLineColor(Color.RED);
                        tMapView.addTMapPolyLine("line" + rand.nextInt(1000), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                    Log.d(TAG, "페이즈 3 : " + totalDistance);
                } else if (phase == 3) {
                    try {
                        typ = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, arPoint.get(arPoint.size() - 1), userPoint);
                        typ.setLineColor(Color.YELLOW);
                        tMapView.addTMapPolyLine("" + rand.nextInt(30), typ);
                        totalDistance = totalDistance + typ.getDistance();
                        arPoint = typ.getLinePoint();

                        UserLocation.setCanShowCallout(true);
                        UserLocation.setCalloutTitle("총 거리 : " + (int) Math.round(totalDistance) + "m");
                        int tim = (int) (((int) Math.round(totalDistance)) * 60 / 5000);
                        UserLocation.setCalloutSubTitle("소요시간 : " + tim + "분");
                        tMapView.addMarkerItem("ed", UserLocation);

                        Log.d(TAG, "탐색 종료 : " + totalDistance);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    phase++;
                } else if (phase == 4) {
                    if (totalDistance <= 1.2 * enteredDistance && totalDistance >= 0.8 * enteredDistance) {
                        break;
                    } else {
                        tMapView.removeAllTMapPolyLine();
                        MakeRandomMarker();
                        DirectionSearcher(userPoint);
                        phase = 0;
                        R = (int) (Math.random() * 5);
                        totalDistance = (double) 0;
                        Log.d(TAG, "생성 실패");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public int finder(TMapPoint a, TMapPoint b) {
        if (a.getLatitude() - b.getLatitude() > 0 && a.getLongitude() - b.getLongitude() > 0) {
            return 1;
        }
        //2사분면
        if (a.getLatitude() - b.getLatitude() > 0 && a.getLongitude() - b.getLongitude() < 0) {
            return 2;
        }
        //3사분면
        if (a.getLatitude() - b.getLatitude() < 0 && a.getLongitude() - b.getLongitude() < 0) {
            return 3;
        }
        //4사분면
        if (a.getLatitude() - b.getLatitude() < 0 && a.getLongitude() - b.getLongitude() > 0) {
            return 4;
        } else
            return 1;
    }

    //임의의 노드 마커 생성
    public void MakeRandomMarker() {
        randomMarkerArray = new ArrayList<>(); //포지션 저장 어레이리스트

        //ArrayList<TMapMarkerItem> visibleMarkerArray = new ArrayList<>();
        int i = 0;

        while (i < 1000) {
            double ran = Math.random() * 2 - 1;
            double fan = Math.random() * 2 - 1;
            double latq = userPoint.getLatitude() + (0.000009 * enteredDistance / 2) * ran;
            double lngq = userPoint.getLongitude() + (0.0000113 * enteredDistance / 2) * fan;
            TMapPolyLine tol = new TMapPolyLine();
            tol.addLinePoint(userPoint);
            tol.addLinePoint(new TMapPoint(latq, lngq));
            if (tol.getDistance() < enteredDistance / 2) {
                randomMarkerArray.add(new TMapPoint(latq, lngq));
            }
            i++;
        }
        Log.d(TAG, "생성된 마커 " + randomMarkerArray.size());

        /*for(int j=0; j<randomMarkerArray.size(); j++)
        {
            TMapMarkerItem visibleMarker = new TMapMarkerItem();
            visibleMarker.setTMapPoint((TMapPoint) randomMarkerArray.get(j));
            visibleMarkerArray.add(visibleMarker);
            tMapView.addMarkerItem("rma" + j, visibleMarkerArray.get(j));
            visibleMarkerArray.get(j).setVisible(TMapMarkerItem.VISIBLE);
        }*/
    }

    // 도달점 기준 방향성 판별
    public void DirectionSearcher(TMapPoint tt) {
        firstQuadrant = new ArrayList<>();
        secondQuadrant = new ArrayList<>();
        thirdQuadrant = new ArrayList<>();
        fourthQuadrant = new ArrayList<>();

        for (int q = 0; q < randomMarkerArray.size() - 1; q++) {
            //1사분면
            if (finder(randomMarkerArray.get(q), tt) == 1) {
                firstQuadrant.add(q);
            }
            //2사분면
            if (finder(randomMarkerArray.get(q), tt) == 2) {
                secondQuadrant.add(q);
            }
            //3사분면
            if (finder(randomMarkerArray.get(q), tt) == 3) {
                thirdQuadrant.add(q);
            }
            //4사분면
            if (finder(randomMarkerArray.get(q), tt) == 4) {
                fourthQuadrant.add(q);
            }
        }
    }

    // 도달점으로부터 각 노드마커들까지의 길이 저장
    public void getDistance(TMapPoint tt, int phase, int R) {
        if (R == 1) {
            if (phase == 0) {
                distanceMap = new Double[firstQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(firstQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 1) {
                distanceMap = new Double[secondQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(secondQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 2) {
                distanceMap = new Double[thirdQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(thirdQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            }
        } else if (R == 2) {
            if (phase == 0) {
                distanceMap = new Double[secondQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(secondQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 1) {
                distanceMap = new Double[thirdQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(thirdQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 2) {
                distanceMap = new Double[fourthQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(fourthQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            }
        } else if (R == 3) {
            if (phase == 0) {
                distanceMap = new Double[thirdQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(thirdQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 1) {
                distanceMap = new Double[fourthQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(fourthQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 2) {
                distanceMap = new Double[firstQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(firstQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            }
        } else if (R == 4) {
            if (phase == 0) {
                distanceMap = new Double[fourthQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(fourthQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 1) {
                distanceMap = new Double[firstQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(firstQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            } else if (phase == 2) {
                distanceMap = new Double[firstQuadrant.size()];
                for (int i = 0; i < distanceMap.length; i++) {
                    TMapPolyLine temp = new TMapPolyLine();
                    temp.addLinePoint(tt);
                    temp.addLinePoint(randomMarkerArray.get(firstQuadrant.get(i)));
                    distanceMap[i] = temp.getDistance();
                }
            }
        }
    }

    //가장 적합한 노드마커 선정
    public int SuitablePoint() {
        int answer = 0;
        double temp = 0;
        for (int i = 0; i < distanceMap.length; i++) {
            if (Math.abs(((enteredDistance - totalDistance) / (4 - phase)) - distanceMap[i]) < Math.abs(((enteredDistance - totalDistance) / (4 - phase)) - temp)) {
                temp = distanceMap[i];
                answer = i;
            }
        }
        return answer;
    }
}

