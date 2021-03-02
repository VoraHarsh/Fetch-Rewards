package com.example.fetchrewardslist;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListRunnable implements Runnable{

    private static final String TAG = "ListRunnable";
    private MainActivity mainActivity;
    private static String DATA_URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    ListRunnable(MainActivity mainActivity) {this.mainActivity = mainActivity;}

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            java.net.URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }

        final ArrayList<ListData> fetchRewardsList = parseJSON(s);
        final ArrayList<ListData> finalList = new ArrayList<>();
        Log.d(TAG, "handleResults: "+ fetchRewardsList);
        for (int i = 0; i < fetchRewardsList.size(); i++ ){
            if(!(fetchRewardsList.get(i).getName().equals("null")) && fetchRewardsList.get(i).getName().length() != 0){
                finalList.add(fetchRewardsList.get(i));
            }
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalList != null)
                    Toast.makeText(mainActivity, "Loaded " + finalList.size() + " list.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "run: "+ finalList.size());
                mainActivity.updateListData(finalList);
            }
        });
    }

    private ArrayList<ListData> parseJSON(String s) {

        ArrayList<ListData> fetchRewardsList = new ArrayList<>();
        Log.d(TAG, "parseJSON list: " + fetchRewardsList);
        try {
            JSONArray jArrayMain = new JSONArray(s);

            //Getting normalizedInput Array
            for(int i =0; i<jArrayMain.length(); i++) {
                String id = "", listId = "", name = "";
                JSONObject objects = (JSONObject) jArrayMain.get(i);

                try {
                    id = objects.getString("id");
                } catch (Exception e) {
                    Log.d(TAG, "parseJSON: " + e.getMessage());
                    e.printStackTrace();
                }

                try {
                    listId = objects.getString("listId");
                } catch (Exception e) {
                    Log.d(TAG, "parseJSON: " + e.getMessage());
                    e.printStackTrace();
                }

                try {
                    name = objects.getString("name");
                } catch (Exception e) {
                    Log.d(TAG, "parseJSON: " + e.getMessage());
                    e.printStackTrace();
                }
                fetchRewardsList.add(new ListData(id, listId, name));
                Log.d(TAG, "parseJSON: bp: id: "+id + " listId: "+ listId + " name: "+name );
            }

            return fetchRewardsList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
