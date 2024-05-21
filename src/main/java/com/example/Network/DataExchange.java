package com.mcl; 


import com.google.gson.Gson;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class DataExchange {
    public List<String> get_versions(){
        List<String> versions = new ArrayList<>();
        
        try 
        {
            URL url = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            
            int responseCode = con.getResponseCode();   

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            Gson gson = new Gson();
            Map<String, Object> json = gson.fromJson(response.toString(), Map.class);
            List<Map<String, Object>> versions_list = (List<Map<String, Object>>) json.get("versions");

            for (Map<String, Object> version : versions_list) {
                versions.add((String) version.get("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return versions;
    }
}
