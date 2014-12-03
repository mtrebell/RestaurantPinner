//Decide if handling exceptions in request functions or outside of them
//Handle fail in deletes

package com.restaurantapp.phoneapp.restaurantpinner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserGrid {
    public final static String FAV = "fav", WISH="wishlist",REC="reccomend",LIKE="like",DIS="dislike";
    private String baseUrl;
    private String accessToken;
    private String uId;

    public UserGrid(String baseUrl){
        accessToken = null;
        uId=null;
        this.baseUrl = baseUrl;
    }

    /*--------------------For Testing Purposes only----------------------*/
    public UserGrid(String baseUrl,String uId,String accessToken){
        this.accessToken = accessToken;
        this.uId=uId;
        this.baseUrl = baseUrl;
    }

    /*------------------------------Account Functions--------------------------------*/
    public boolean login(String user,String password){
        String query = "token?grant_type=password&username="+user+"&password="+password;
            JSONObject response = sendGet(query,null);
            if(response == null) {
                accessToken = null;
                uId = null;
                return false;
            }
        try {
            accessToken = response.getString("access_token");
            uId = response.getJSONObject("user").getString("uuid");
            return true;
        }catch (JSONException e) {
            accessToken = null;
            uId = null;
            return false;
        }
    }

    public boolean logout(String user){
        String query = "/users/"+user+"/revoketokens?access_token="+accessToken;
        try {
            sendPut(query, null);
        }catch(JSONException e){
            return false;
        }catch(IOException e){
            return false;
        }
        accessToken = null;
        uId = null;

        return true;
    }

    public boolean chgPassword(String user,String oldPass,String newPass){
        String query = "/users/"+user+"/password";
        String params = "{\"newpassword\":" + newPass + ",\"oldPassword\":" + oldPass +"}";

        try {
            JSONObject jsonParam = new JSONObject(params);
            sendPut(query, jsonParam);
        }catch(JSONException e){
            return false;
        }catch(IOException e){
            return false;
        }
        return true;
    }

    public boolean addAccount(String username,String pass,String email){
        String query = "/users";
        String params = "{\"username\":\""+ username + "\",\"password\":\""+ pass + "\",\"email\":\"" + email +"\"}";

        try {
            JSONObject jsonParam = new JSONObject(params);
            sendPut(query, jsonParam);
        }catch(JSONException e){
            return false;
        }catch(IOException e){
            return false;
        }

        return true;
    }

    public boolean deleteAccount(String username)
    {
        String query = "/users/"+username+"?access_token="+accessToken;
        request(query, "DELETE");
        return true;
    }

    /*---------------------Friends Functions--------------------*/
    public HashMap<String,String> searchUser(String username){
        String entity ="/users?ql=";
        String query = "select uuid,username,email where username= '"+username + "*'";
        JSONObject response = sendGet(entity,query);
        return userParse(response);
    }

    public HashMap<String,String> getFriends(){
        String entity = "users/"+uId+"/friends?ql=";
        String query = "select uuid,username,email";
        JSONObject response = sendGet(entity,query);
        return userParse(response);
    }

    public HashMap<String,String> userParse(JSONObject response){
        HashMap<String,String> users = new HashMap<String,String>();
        JSONArray friends;

        try {
            friends = response.getJSONArray("list");
        } catch (JSONException e) {
           return null;
        }
        for(int i=0;i<friends.length();i++){
            JSONArray friend;
            try {
                friend = friends.getJSONArray(i);
            } catch (JSONException e) {
                return null;
            }

            String username;
            try {
                username = friend.getString(1);
            } catch (JSONException e) {
                return null;
            }

            if(username!=null){
                String email;
                try {
                    email = friend.getString(2);
                } catch (JSONException e) {
                    return null;
                }
                if(email!=null)
                    users.put(username, email);
                else
                    users.put(username, "");
            }
        }
        return users;
    }

    public boolean addFriend( String friend){
        String query = "/users/" + uId + "/friends/users/"+friend+"?access_token="+accessToken;
        try {
            sendPut(query,null);
        } catch (IOException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean removeFriend( String friend){
        String query = "/users/" + uId + "/friends/"+friend+"?access_token="+accessToken;
        request(query,"DELETE");
        return true;
    }

    public boolean requestFriend( List<String> friends){
        for(String friend:friends) {
            String query = "/users/" + friend + "/reqfriend/users/" + uId + "?access_token=" + accessToken;
            try {
                sendPut(query,null);
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    public boolean removeRequestFriend( String friend){
        String query = "/users/" + uId + "/reqfriend/users/"+friend+"?access_token="+accessToken;
        request(query,"DELETE");
        return true;
    }

    /*-----------------Pin Functions--------------*/
    public HashMap<String, Pin> getPins(){
        HashMap<String,Pin> pins = new HashMap<String,Pin>();
        JSONObject data;

        String base = "/users/" + uId + "/";
        String query = "?ql=select uuid,name,address,location";
        String[] types = {DIS,WISH,REC,FAV,LIKE};

        try {
            data = accumulate(base,types,query);
        } catch (JSONException e) {
            return null;
        }

        Iterator<String> keys = data.keys();

        while(keys.hasNext()){
           String key = keys.next();
            JSONArray restaurants;
            try {
                restaurants = (JSONArray)((JSONObject) data.get(key)).get("list");
            } catch (JSONException e) {
                return null;
            }

            for(int i=0;i<restaurants.length();i++){
                try {
                    String uuid =restaurants.getJSONArray(i).getString(0);
                    String name = restaurants.getJSONArray(i).getString(1);

                    StringBuilder address =new StringBuilder();
                    JSONArray add = restaurants.getJSONArray(i).getJSONArray(2);
                    for(int j=0;j<add.length()-1;j++){ //Skip Province and Country
                        address.append(add.getString(j));
                        address.append(" ");
                    }

                    JSONObject latlng = restaurants.getJSONArray(i).getJSONObject(3);
                    Double lat = latlng.getDouble("latitude");
                    Double lng = latlng.getDouble("longitude");

                    //Update old data
                    if(pins.containsKey(uuid)) {
                        Pin pin = pins.get(uuid);
                        pin.types.add(key);
                        pins.put(uuid, pin);
                    }
                    //Create new
                    else {
                        pins.put(uuid, new Pin(uuid, name,address.toString(),key, lat, lng));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return pins;
    }

    public boolean addPin(String restaurant,List<String> pinType){
        String query;

        for(String pin:pinType) {
            query = "/users/" +uId +"/"+ pin + "/restaurants/" + restaurant+"?access_token="+accessToken;
            Log.d("ADD PIN",query);
            try {
                sendPut(query,null);
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    public boolean removePin(String restaurant,List<String> pinType){
        for(String pin:pinType) {
            String query = "/users/" +uId +"/"+ pin + "/restaurants/" + restaurant+"?access_token="+accessToken;
            request(query,"DELETE");
        }
        return true;
    }

    public boolean addRecommendation(String restraunt, List<String> users){
        for(String user : users) {
            String query = "/users/" + user + "/recpin/restaurant/" + restraunt + "?access_token=" + accessToken;
            try {
                sendPut(query,null);
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    public boolean removeRecomendation(String restraunt){

        String query;

        query = "/users/" +uId +"/recpin/" + restraunt+"?access_token="+accessToken;
        request(query,"DELETE");

        return true;
    }

    /*-------------------------Notification Functions----------------------*/
    public HashMap<String,Notification> getNotifications(){
        HashMap<String,Notification> notifications = new HashMap<String, Notification>();

        try {

            String base = "/users/" + uId + "/";
            String query = "?ql=select uuid,name,email";
            String[] types = {"recpin","friends"};

            JSONObject response = accumulate(base,types,query);

            Iterator<String> keys = response.keys();
            while (keys.hasNext()){
                String uuid;
                String name;
                int type;

                String key = keys.next();
                JSONArray data = response.getJSONObject(key).getJSONArray("list");

                if(key.equals("pin"))
                    type = Notification.PIN;
                else
                    type = Notification.FRIEND;

                for (int i = 0; i < data.length(); i++) {

                        JSONArray notification = data.getJSONArray(i);
                        uuid = notification.getString(0);
                        name = notification.getString(1);

                        if(type == Notification.PIN) {
                            StringBuilder address = new StringBuilder();
                            JSONArray add = notification.getJSONArray(2);

                            for (int j = 0; j < add.length() - 2; j++) {
                                address.append(add.getString(j));
                            }

                            notifications.put(uuid,new Notification(type,uuid,name,address.toString()));
                        }

                        else{
                            String address  = notification.getString(2);
                            notifications.put(uuid,new Notification(type,uuid,name,address));
                        }
                    }


            }
        }catch(Exception e){
            return null;
        }

        return notifications;
    }

    public ArrayList<MarkerOptions> restaurantSearch(String name, double lat, double lon, double km, boolean pinsOnly){
        String entity;
        String query;

        if(pinsOnly){
            entity=uId+"/pin?ql=";
            query="select uuid,name,location";
        }

        else{
            entity="/restaurants?ql=";
            query= "select uuid,name,location";
        }

        if(!name.isEmpty()){
            query+=" where name_index contains \'"+ name + "\'";
        }

        if(lat !=-1){
            if(name.isEmpty())
                query+=" where";

            if(km!=-0) {
                query += " location within " + km * 1000 + " of " + lat + ',' + lon;
            }

            else
                query+=" location within 750 of " + lat + ',' + lon;
        }

        JSONObject response = sendGet(entity,query);
        JSONArray data;
        try {
            data = response.getJSONArray("list");
        } catch (JSONException e) {
            return null;
        }
       return buildMarkers(data);
    }

    public ArrayList<MarkerOptions> buildMarkers(JSONArray markers){

      ArrayList<MarkerOptions> markerList=new ArrayList<MarkerOptions>();
      for(int i=0; i<markers.length(); i++) {
          try {
              JSONArray data = markers.getJSONArray(i);
              String uuid = data.getString(0);
              String name = data.getString(1);

              JSONObject location = data.getJSONObject(2);
              LatLng latlng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));

              MarkerOptions marker = new MarkerOptions()
                      .position(latlng)
                      .title(name)
                      .snippet(uuid);
              markerList.add(marker);
          } catch (JSONException e) {
              return null;
          }
      }
      return markerList;
      }

    public JSONObject restaurantInfo(String restrauntID){
        String entity = "/restaurants?ql=";
        String query = "select * where uuid="+restrauntID;
        JSONObject response=sendGet(entity,query);

        JSONArray entities;
        try {
            entities = ((JSONArray)response.get("entities"));
        } catch (JSONException e) {
            return null;
        }
        JSONObject restaurant;
        try {
            restaurant = (JSONObject) entities.get(0);
        } catch (JSONException e) {
            return null;
        }
        return restaurant;
    }

    public String getUID(){
        return uId;
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    public String getAccessToken(){
        return accessToken;
    }

    private JSONObject accumulate(String base,String[] types,String query) throws JSONException {
        JSONObject response = new JSONObject();

        for(String type : types)
                response.accumulate(type, sendGet(base+type,query));

        return response;
    }

    private String request(String query,String method){
        URL url;
        try {
            url = new URL(baseUrl + query);
            Log.d("Query", url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            return con.getResponseMessage();
        }catch(Exception e){
            return null;
        }
    }

    private  JSONObject sendGet(String entity,String query){
        try {
            String encodedURL=baseUrl + "/" + entity;
            if(query!=null)
                encodedURL+=java.net.URLEncoder.encode(query,"UTF-8");

            if (accessToken !=null)
                encodedURL+="&access_token="+accessToken;
            URL url;
            try {
                url = new URL(encodedURL);
                Log.d("Query",url.toString());
            } catch (MalformedURLException e) {
                return null;
            }
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) url.openConnection();

            } catch (IOException e) {
                return null;
            }
            JSONObject results;
            try {
                InputStreamReader in = new InputStreamReader(con.getInputStream());
                results= readStream(new BufferedReader(in));
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            } finally {
                con.disconnect();
            }
            return  results;
        } catch (UnsupportedEncodingException e1) {
            return null;
        }

    }

   private JSONObject readStream(BufferedReader in) throws IOException, JSONException {
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        return new JSONObject(response.toString());
    }

    private JSONObject sendPut(String query, JSONObject jsonParam) throws IOException, JSONException {
        String temp = (baseUrl + query);
        System.out.println(temp);
        URL url = new URL(baseUrl + query);
        Log.d("Query",url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        if(jsonParam != null){
            String data = jsonParam.toString();
            wr.write(data);
        }
        wr.flush();

        JSONObject results;
        try {
            InputStreamReader in = new InputStreamReader(con.getInputStream());
            results= readStream(new BufferedReader(in));
        } catch (IOException e) {
            System.out.println(baseUrl);
            System.out.println(e.getMessage());
            return null;
        } finally {
            con.disconnect();
        }
        return  results;
    }

}


