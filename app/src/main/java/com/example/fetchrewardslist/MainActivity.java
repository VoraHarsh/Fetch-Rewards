package com.example.fetchrewardslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ListData> fetchRewardsArrayList;
    private ArrayList<ListData> fetchList;
    private RecyclerView recyclerList;
    private SwipeRefreshLayout swiper;
    private ListAdapter mAdapter;
    private static final String TAG = "MainActivity";
    Menu menu;
    private HashMap<String, ArrayList<ListData>> listHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerList = findViewById(R.id.recyclerlist);
        fetchRewardsArrayList = new ArrayList<>();
        fetchList = new ArrayList<>();
        swiper = findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if(NetworkCheck()){
            getListData();
        }
        else{
            noInternet();
        }

        mAdapter = new ListAdapter(fetchRewardsArrayList, this);
        recyclerList.setAdapter(mAdapter);
        recyclerList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void doRefresh() {
        if (NetworkCheck()) {
            if(fetchRewardsArrayList.isEmpty()){
                getListData();
                swiper.setRefreshing(false);
            }
            swiper.setRefreshing(false);
        }
        else {
            noInternet();
            swiper.setRefreshing(false);
        }
        Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
    }

    public void getListData(){
        ListRunnable listRunnable = new ListRunnable(this);
        new Thread(listRunnable).start();
    }

    public void updateListData(final ArrayList<ListData> tempList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchRewardsArrayList.clear();
                fetchRewardsArrayList.addAll(sortListData(tempList));
                fetchList.addAll(sortListData(tempList));
                setupCategory(fetchRewardsArrayList);
                mAdapter.notifyDataSetChanged();
                swiper.setRefreshing(false);
            }
        });
    }

    ArrayList<ListData> sortListData(ArrayList<ListData> list){
        Collections.sort(list, new Comparator<ListData>() {
            @Override
            public int compare(ListData l1, ListData l2) {
                String listData1 = l1.getListId();
                String listData2 = l2.getListId();
                int comp = extractInt(listData1) - extractInt(listData2);

                if(comp!=0){
                    return comp;
                }

                return extractInt(l1.getName()) - extractInt(l2.getName());
            }

            int extractInt(String s){
                String number = s.replaceAll("\\D", "");
                return number.isEmpty() ? 0 : Integer.parseInt(number);
            }

        });
        return list;
    }

    public void setupCategory(ArrayList<ListData> list){
        listHashMap.clear();
        try{
            menu.clear();

            final ArrayList<ListData> finalList = list;

            for (int j=0; j < list.size(); j++ ){
                String ListId = list.get(j).getListId();

                if(listHashMap.containsKey(ListId)){
                    listHashMap.get(ListId).add(finalList.get(j));
                }
                else{
                    ArrayList<ListData> tempList = new ArrayList<>();
                    tempList.add(finalList.get(j));
                    listHashMap.put(ListId, tempList);
                }
            }
            listHashMap.put("All", fetchList);

            for (String category : listHashMap.keySet()){
                menu.add(category);
            }
            Log.d(TAG, "setupCategory: " + listHashMap.get("All").size());

        }catch (Exception e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(NetworkCheck() == true){
            setTitle("List Id: "+item.getTitle().toString());

            fetchRewardsArrayList.clear();

            ArrayList<ListData> drawerTempList = listHashMap.get(item.getTitle().toString());

            if(drawerTempList != null)
            {
                fetchRewardsArrayList.addAll(drawerTempList);
            }

            mAdapter.notifyDataSetChanged();
            swiper.setRefreshing(false);
            Toast.makeText(this, drawerTempList.size() + " Sources Loaded ", Toast.LENGTH_SHORT).show();
        }
        else{
            noInternet();
        }
        return super.onOptionsItemSelected(item);

    }

    public boolean NetworkCheck(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            Toast.makeText(this, "Cannot Access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    public void noInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data Cannot be accessed/loaded without an internet connection.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void downloadFailed(){
        Toast.makeText(this, "Cannot Download Data for Some Internal Error. ", Toast.LENGTH_SHORT).show();
    }
}