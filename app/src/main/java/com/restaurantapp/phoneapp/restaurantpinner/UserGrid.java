package com.restaurantapp.phoneapp.restaurantpinner;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserGrid {
    public final static String FAV = "fav", WISH="wishlist",REC="reccomend",LIKE="like",DIS="dislike";
    private static String baseUrl;
    private String accessToken;
    private String uId;

    public UserGrid(String baseUrl){
        accessToken = null;
        uId=null;
        this.baseUrl = baseUrl;
    }

    public UserGrid(String baseUrl,String uId,String accessToken){
        this.accessToken = accessToken;
        this.uId=uId;
        this.baseUrl = baseUrl;
    }

    // Account Functions:
    public boolean login(String user,String password){
        String query = "/token?grant_type=password&username="+user+"&password="+password;
        JSONObject response = sendGet(query,null);
        try{
            accessToken = response.getString("access_token");
            uId = response.getJSONObject("user").getString("uuid");
        }catch(JSONException e){
            accessToken = null;
            uId = null;
            return false;
        }
        return true;
    }

    public boolean logout(String user){
        String query = "/users/"+user+"/revoketokens?access_token="+accessToken;

        //request(query, "PUT");
        try {
            JSONObject response = sendPut(query, null);
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
            JSONObject response = sendPut(query, jsonParam);
        }catch(JSONException e){
            System.out.println(e.getMessage());
            return false;
        }catch(IOException e){
            System.out.println(e.getMessage());
            return false;
        }

        //put(address,json);
        return true;
    }

    public boolean addAccount(String username,String pass){
        String query = "/users";
        String params = "{\"username\":"+ username + ", \"password\":"+ pass + "}";

        try {
            JSONObject jsonParam = new JSONObject(params);
            JSONObject response = sendPut(query, jsonParam);
            System.out.println(response.toString());
        }catch(JSONException e){
            System.out.println(e.getMessage());
            return false;
        }catch(IOException e){
            System.out.println(e.getMessage());
            return false;
        }
        //put(address,json);
        return true;
    }

    public boolean deleteAccount(String username,String pass)
    {
        String query = "/users/"+username+"?access_token="+accessToken;
        request(query, "DELETE");
        return true;
    }

    public HashMap<String,String> getFriends(){
        Log.d("ADD PIN","Searching for frineds....");
        HashMap<String,String> friends = new HashMap<String,String>();
        String entity = "users/"+uId+"/friend?ql=";
        String query = "select uuid,username,email";
        JSONObject response = sendGet(entity,query);
        friends=userParse(response);
        return friends;
    }

    public boolean addFriend( String friend){
        String query = "/users/" + uId + "/friends/"+friend+"?access_token="+accessToken;
        request(query,"PUT");
        return true;
    }

    public boolean removeFriend( String friend){
        String query = "/users/" + uId + "/friends/"+friend+"?access_token="+accessToken;
        request(query,"DELETE");
        return true;
    }


    public Map<String,String> searchUser(String username){
        Map<String,String> users = new HashMap<String,String>();
        String entity ="users?ql=";
        String query = "select uuid,username,email where username= '"+username + "*'";
        JSONObject response = sendGet(entity,query);
        users = userParse(response);
        return users;
    }

    public HashMap<String,String> userParse(JSONObject response){
        HashMap<String,String> users = new HashMap<String,String>();
        //PARSE STUFF HERE
        JSONArray friends = null;
        try {
            friends = (JSONArray)response.get("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<friends.length();i++){
            JSONArray friend = null;
            try {
                friend = (JSONArray)friends.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(friend.toString());

            String username = null;
            try {
                username = friend.getString(1);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            if(username!=null){
                System.out.println("HERE");
                String email= null; //null is not reconized as string
                try {
                    email = (String) friend.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(email!=null)
                    users.put(username, email);
                else
                    users.put(username, "None");

            }

        }
        System.out.println(users.toString());
        return users;
    }

    //Pin Functions:

    public JSONObject getPinInfo(){
        JSONObject response = new JSONObject();
        try {
            String entity ="users/"+uId+ "/"+LIKE+"?ql=";
            String query = "select uuid,name";
            response.accumulate(LIKE,  sendGet(entity,query));

            entity ="users/"+uId+ "/"+FAV+"?ql=";
            query = "select uuid,name";

            response.accumulate( FAV, sendGet(entity, query));


            entity = "users/" + uId + "/"+WISH+"?ql=";
            query = "select uuid,name";
            response.accumulate(WISH, sendGet(entity, query));

            entity = "users/" + uId + "/"+REC+"?ql=";
            query = "select uuid,name";
            response.accumulate(REC, sendGet(entity, query));

            entity = "users/" + uId + "/"+DIS+"?ql=";
            query = "select uuid,name,loc";
            response.accumulate(DIS, sendGet(entity, query));
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    //Returns all users pins <uuid,name>
    public HashMap<String, Pin> getPins(){
        HashMap<String,Pin> pins = new HashMap<String,Pin>();
        JSONObject data = getPinInfo();

        Iterator<String> keys = data.keys();

        while(keys.hasNext()){
            String key = keys.next();
            JSONArray restraunts;
            try {
                restraunts = (JSONArray)((JSONObject) data.get(key)).get("entities");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            for(int i=0;i<restraunts.length();i++){
                try {
                    String uuid = ((JSONObject)restraunts.get(i)).getString("uuid");
                    String name = ((JSONObject)restraunts.get(i)).getString("name");
                    double lat = ((JSONObject)restraunts.get(i)).getDouble("latitude");
                    double lng = ((JSONObject)restraunts.get(i)).getDouble("longitude");

                    //Update old data
                    if(!pins.containsKey(uuid)) {
                        Pin pin = pins.get(key);
                        pin.types.add(key);
                        pins.put(uuid, pin);
                    }
                    //Create new
                    else
                        pins.put(uuid,new Pin(uuid,name,lat,lng));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return pins;
    }

    public boolean addPin(String restraunt,List<String> pinType){
        //to quickly grab all pins
        Log.d("ADD PIN","ADDING PIN....");
        String query;
        //Specific Type, allows user filters to be added later on
        for(String pin:pinType) {
            query = "/users/" +uId +"/"+ pin + "/" + restraunt+"?access_token="+accessToken;
            Log.d("ADD PIN",query);
            request(query, "PUT");
        }
        return true;
    }

    //In phoneApp check if completely removed add pin to list if it is
    //Also for dislike only allow dislike pin(Later Feature)
    public boolean removePin(String restraunt,List<String> pinType){
        if(pinType==null)
            return false;

        for(String pin:pinType) {
            String query = "/users/" +uId +"/"+ pin + "/" + restraunt+"?access_token="+accessToken;
            request(query,"DELETE");
        }
        return true;
    }


    public boolean addRecomendation(String restraunt, List<String> users){
        //to quickly grab all pins
        String query;
        //Specific Type, allows user filters to be added later on
        Log.d("Adding the pin.....","recomendation");
        if(users == null)
            return false;

        for(String user: users) {
            query = "/users/" + user + "/recnotice/" + restraunt + "?access_token=" + accessToken;
            Log.d("Adding the pin.....","query");
            request(query, "PUT");
        }
        return true;
    }


    public boolean removeRecomendation(String restraunt, String user){
        //to quickly grab all pins
        String query;
        //Specific Type, allows user filters to be added later on

        query = "/users/" +user +"/recnotice/" + restraunt+"?access_token="+accessToken;
        request(query,"DELETE");

        return true;
    }

    //Restaurant Functions:

    //Search by location name
    public ArrayList<MarkerOptions>  restrauntSearch(String name,String address, Boolean pinsOnly){
        String entity;
        String query;

        if(pinsOnly){
            entity=uId+"/pin?ql=";
            query="select uuid,name,location";
        }
        else{
            entity="restaurants?ql=";
            query= "select uuid,name,location";
        }


        if(name!=null){
            query+=" where name_index contains \'"+ name + "\'";
        }

        //LOCATION HANDLING HERE

        JSONObject response = sendGet(entity,query);
        JSONArray data = null;
        try {
            data = response.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return buildMarkers(data);
    }


    //Search location by lat lng
    public ArrayList<MarkerOptions>  restrauntSearch(String name,double lat,double lon,double km,boolean pinsOnly){
        String entity;
        String query;
        Log.d("Got a location",lat+" "+lon);

        if(pinsOnly){
            entity=uId+"/pin?ql=";
            query="select uuid,name,location";
        }
        else{
            entity="restaurants?ql=";
            query= "select uuid,name,location";
        }


        if(!name.isEmpty()){
            query+=" where name_index contains \'"+ name + "\'";
        }

        if(lat !=-1){
            Log.d("Got here","Im here");
            if(name.isEmpty())
                query+=" where";


            if(km!=-0) {
                query += " location within " + km * 1000 + " of " + lat + ',' + lon;
            }
            //This accounts for tracking error
            else
                query+=" location within 750 of " + lat + ',' + lon;
            Log.d("query",query);
        }

        JSONObject response = sendGet(entity,query);
        Log.d("Response",response.toString());
        JSONArray data = null;
        try {
            data = response.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
       return buildMarkers(data);
    }

    public ArrayList<MarkerOptions> buildMarkers(JSONArray markers){

      ArrayList<MarkerOptions> markerList=new ArrayList<MarkerOptions>();
      for(int i=0; i<markers.length(); i++) {
          JSONArray data = null;
          try {
              data = markers.getJSONArray(i);

              String uuid = data.getString(0);
              String name = data.getString(1);

              JSONObject location = data.getJSONObject(2);
              LatLng latlng = new LatLng(location.getDouble("latitude"),location.getDouble("longitude"));

              MarkerOptions marker = new MarkerOptions()
                      .position(latlng)
                      .title(name)
                      .snippet(uuid);
              markerList.add(marker);
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
        Log.d("I got",markerList.toString());
      return markerList;
      }

    public JSONObject restaurantInfo(String restrauntID){
        String entity = "restaurants?ql=";
        String query = "select * where uuid="+restrauntID;
        JSONObject response=sendGet(entity,query);

        //Extracting Restaurant Attributes Object
        JSONArray entities = null;
        try {
            entities = ((JSONArray)response.get("entities"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject restraunt = null;
        try {
            restraunt = (JSONObject) entities.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return restraunt;
    }

    private String request(String query,String method){
        URL obj;
        try {
            obj = new URL(baseUrl + query);
            Log.d("Query",obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(method);
            return con.getResponseMessage();
        }catch(Exception e){
            return null;
        }
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

    public  JSONObject sendGet(String entity,String query){
        try {
            String encodedURL=baseUrl + "/" + entity;
            if(query!=null)
                encodedURL+=java.net.URLEncoder.encode(query,"UTF-8");

            if (accessToken !=null)
                encodedURL+="&access_token="+accessToken;
            URL url;
            try {
                url = new URL(encodedURL);
            } catch (MalformedURLException e) {
                System.out.println("Bad url");
                return null;
            }
            HttpURLConnection con;
            try {
                con = (HttpURLConnection) url.openConnection();

            } catch (IOException e) {
                System.out.println("No connection");
                return null;
            }
            JSONObject results;
            try {

                InputStreamReader in = new InputStreamReader(con.getInputStream());
                results= readStream(new BufferedReader(in));
            } catch (IOException e) {
                System.out.println("No response");
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                con.disconnect();
            }
            return  results;
        } catch (UnsupportedEncodingException e1) {
            return null;
        }

    }

    static public JSONObject readStream(BufferedReader in) throws IOException, JSONException {
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        return new JSONObject(response.toString());
    }

    private JSONObject sendPut(String query, JSONObject jsonParam) throws IOException, JSONException {
        URL url = new URL(baseUrl + query);
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
            System.out.println(e.getMessage());
            return null;
        } finally {
            con.disconnect();
        }
        return  results;
    }

}


