package com.sudalv.subway.util;

import com.baidu.mapapi.model.LatLng;
import com.sudalv.subway.listitem.LineItem;
import com.sudalv.subway.listitem.StationItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunWe on 2015/10/6.
 */
public class BaiduMapUtils {
    private static List<LineItem> lines = new ArrayList<LineItem>();
    private static List<StationItem> stations = new ArrayList<StationItem>();
    private static List<LatLng> stationList = new ArrayList<LatLng>();
    public static List<StationItem> initStations(InputStream input){
        try {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            String json = new String(buffer, "utf-8");
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("stations");
            for(int i=0; i<arr.length();i++){
                JSONObject temp = arr.getJSONObject(i);
                String name = temp.getString("name");
                double locX = temp.getDouble("locX");
                double locY = temp.getDouble("locY");
                int line = temp.getInt("line");
                int id = temp.getInt("id");
                stations.add(new StationItem(name,id,line,new LatLng(locY,locX)));
            }
            input.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stations;
    }

    public static List<LatLng> getStationPosList(){
        for(StationItem item : stations){
            stationList.add(item.getmPos());
        }
        return stationList;
    }
    public static List<LineItem> initSubway(InputStream input){
        try {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            String json = new String(buffer, "utf-8");
            JSONObject obj = new JSONObject(json);
            addItemToList("line1", obj);
            addItemToList("line5",obj);
            addItemToList("line8",obj);
            addItemToList("line4",obj);
            addItemToList("line3",obj);
            addItemToList("line2",obj);
            addItemToList("line6",obj);
            addItemToList("line7",obj);
            addItemToList("line9",obj);
            addItemToList("line10",obj);
            addItemToList("line11",obj);
            addItemToList("line12",obj);
            addItemToList("line13",obj);
            addItemToList("line16",obj);
            input.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return lines;
    }

    private static void addItemToList( String lineName, JSONObject obj) throws Exception{
        JSONArray arr = obj.getJSONArray(lineName);
        for(int i=0; i<arr.length();i++){
            JSONObject tempObject = arr.getJSONObject(i);
            LineItem tempItem = new LineItem(tempObject.getString("from"),tempObject.getString("to"));
            JSONArray temparr = tempObject.getJSONArray("pos");
            for(int j=0; j<temparr.length();j++){
                JSONObject temp = temparr.getJSONObject(j);
                double locX = temp.getDouble("locX");
                double locY = temp.getDouble("locY");
                tempItem.addPos(locX,locY);
            }
            lines.add(tempItem);
        }
    }
}