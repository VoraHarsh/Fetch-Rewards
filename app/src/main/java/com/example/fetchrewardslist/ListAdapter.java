package com.example.fetchrewardslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private static final String TAG = "ListAdapter";
    private List<ListData> fetchRewardList;
    private MainActivity mainAct;

    ListAdapter(List<ListData> fetchRewardList, MainActivity ma){
        this.fetchRewardList = fetchRewardList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_entry, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListData listData = fetchRewardList.get(position);

        if (listData.getName() != "" && listData.getName() != null) {
            holder.id.setText(listData.getId());
            holder.name.setText(listData.getName());
            holder.listId.setText(listData.getListId());
        }
    }

    @Override
    public int getItemCount() {
        return fetchRewardList.size();
    }
}
