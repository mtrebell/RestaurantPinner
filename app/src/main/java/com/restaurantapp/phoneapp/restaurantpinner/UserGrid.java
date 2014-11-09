package com.example.fix.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by fix on 20/10/2014.
 */
public class UserGrid {
    String baseUrl;
    String accesstoken;
    String uId;

    public UserGrid(String address,String org,String app){
        baseUrl = address+'/'+org+'/'+app;
        accesstoken = null;
        uId = null;
    }

   // Account Functions:
    public boolean login(String user,String password){
        String query = "/token?grant_type=password&username="+user+"&password="+password;
        JSONArray response = sendGet(query);
        //request(query,"GET");
        //Parse this
        try{
            JSONObject userInfo = response.getJSONObject(0);
            accesstoken = userInfo.getString("access_token");
            uId = userInfo.getJSONObject("user").getString("uuid");
        }catch(JSONException e){
            accesstoken = null;
            uId = null;
            return false;
        }
        return true;
    }

    public boolean logout(String user){
        String query = "/users/"+user+"/revoketokens?access_token="+accesstoken;

        //request(query, "PUT");
        try {
            JSONArray response = sendPut(query, null);
        }catch(JSONException e){
            return false;
        }catch(IOException e){
            return false;
        }
        accesstoken = null;
        uId = null;

        return true;
    }

    public boolean chgPassword(String user,String oldPass,String newPass){
        String query = "/users/"+user+"/password";
        String params = "{\"newpassword\":" + newPass + ",\"oldPassword\":" + oldPass +"}";

        try {
            JSONObject jsonParam = new JSONObject(params);
            JSONArray response = sendPut(query, jsonParam);
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
            JSONArray response = sendPut(query, jsonParam);
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
        String query = "/users/"+username+"?access_token="+accesstoken;
        request(query, "DELETE");
        return true;
    }

    //Friend Functions:
    public List<String> getFriends(String user){
        List<String> friends = new ArrayList<String>();
        String query = "/users/"+user+"/friends";
        String response = request(query,"GET");
        //Parse funcition
        return friends;
    }

    //Verify: Can do this search?
    public List<String> findFriend(String user,String friend){
        List<String> friends = new ArrayList<String>();
        String query = "/users/"+user+"/friends?ql= select * where name contains \'" + friend +'\'';
        String response = request(query,"GET");
        //Parse funcition
        return friends;
    }

    public boolean addFriend(String user,String friend){
        String query = "/users/" + user + "/friends/"+friend;
        String response = request(query,"POST");
        return true;
    }

    public boolean removeFriend(String user, String friend){
        String query = "/users/" + user + "/friends/"+friend;
        String response = request(query,"DELETE");
        return true;
    }

    public List<String> searchUser(String user){
        List<String> users = new ArrayList<String>();
        String query = "/pins?ql=select * where username contains"+user;
        String response = request(query,"GET");
        return users;
    }

    //Pin Functions:

    public List<Integer> getDislike(String user,String restraunt){
        List dislike =new ArrayList<Integer>();
        String query = "/user/"+user + "/dislikes/" + restraunt;
        request(query,"GET");
        //Parse funcition
        return dislike;
    }

    public List<Integer> getPins(String user,String restraunt){
        List<Integer> pins = new ArrayList<Integer>();
        String query = "/user/"+user + "liked/" + restraunt;
        request(query,"GET");
        query = "/user/"+user + "favorite/" + restraunt;
        request(query,"GET");
        query = "/user/"+user + "wishlist/" + restraunt;
        request(query,"GET");
        query = "/user/"+user + "reccomend/" + restraunt;
        request(query,"GET");
        //Parse funcition
        return pins;
    }

    public boolean addPin(String user,String restraunt,List<String> pinType){
        for(String type:pinType) {
            String query = "/user/" +user +"/"+ type + "/" + restraunt;
            request(query, "PUT");
        }
        return true;
    }

    public boolean removePin(String user,String restraunt,List<String> pinType){
        for(String type:pinType) {
            String query = "/user/" +user +"/"+ type + "/" + restraunt;
            request(query, "DELETE");
        }
        return true;
    }

    public Restraunt pinInfo(String user,int pinID){
        Restraunt restraunt = new Restraunt();
        String query = "/pins/"+pinID;
        request(query,"GET");
        //Parse function here
        return restraunt;
    }

    //Search Functions:

    //Called if GPS is disabled
    //Finish GEOCODING LOCATION
    public List<Restraunt> restrauntSearch(String name,String location,int km){
        List<Restraunt> restraunt = new ArrayList<Restraunt>();
        String query = "/restraunt?ql=select uuid,name,location,address where ";
        if(name!=null){
            query+="name contains \'"+ name + "\' ";
        }
        if(location!=null){
            if(km!=0) {
                double lat=0;
                double lon=0;
                //GEOCODE STUFF HERE
                query += "location within " + km * 1000 + " of " + lat + ',' + lon;
            }
            else
                query+="address contains \'"+ location + '\'';
        }
        query=query.substring(0,query.length());
        request(query,"GET");
        //Convert to PIN
        return restraunt;
    }

    //Called if GPS is enabled
    public List<Restraunt> restrauntSearch(String name,int lat,int lon,int km){
        List<Restraunt> restraunts = new ArrayList<Restraunt>();

        String query = "/restraunt?ql=select uuid,name,location,address where ";
        if(name!=null){
            query+="name contains \'"+ name + "\' ";
        }
        if(km!=0) {
            query += "location within " + km * 1000 + " of " + lat + ',' + lon;
        }
        else
            query+="location within 100 of \'" + lat + ',' + lon;

        query=query.substring(0,query.length());
        request(query,"GET");
        //Build restraunt list here
        return restraunts;
    }

    public String restrauntInfo(int restrauntID){
        String query = "/restraunt/"+restrauntID;
        return request(query, "GET");
    }

    private String request(String query,String method){
        URL obj;
        try {
            obj = new URL(baseUrl + query);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(method);
            return con.getResponseMessage();
        }catch(Exception e){
            return null;
        }
    }

    private JSONArray sendGet(String query){
        URL url;
        try{
            url = new URL(baseUrl + query);
        }catch(Exception e){
            return null;
        }

        HttpURLConnection con;
        try{
            con = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            return null;
        }

        JSONArray results;
        try{
            InputStreamReader in =  new InputStreamReader(con.getInputStream());
            results = readStream(new BufferedReader(in));
        }catch (Exception e){
            return null;
        }finally {
            con.disconnect();
        }
        return results;
    }

    static public JSONArray readStream(BufferedReader in) throws IOException, JSONException{
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        JSONArray data = new JSONArray();
        JSONObject collections = new JSONObject(response.toString());
        data.put(collections);

        return data;
    }

    private JSONArray sendPut(String query, JSONObject jsonParam) throws IOException, JSONException {
        URL url = new URL(baseUrl + query);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        //con.setRequestMethod("POST");

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        if(jsonParam != null){
            String data = jsonParam.toString();
            wr.write(data);
        }
        wr.flush();

        JSONArray results;
        try {
            InputStreamReader in = new InputStreamReader(con.getInputStream());
            results= readStream(new BufferedReader(in));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }finally {
            con.disconnect();
        }
        return  results;

        //return null;
    }

    private boolean put(String address,String data){
        try {
        URL url = new URL(baseUrl + address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        con.addRequestProperty("Content-Type", "application/json");
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(data);
        out.close();
        }catch(Exception e){
            return false;
        }
        return true;
    }
}



