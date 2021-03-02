package com.example.fetchrewardslist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    TextView id, listId, name;

    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.id);
        listId = itemView.findViewById(R.id.listId);
        name = itemView.findViewById(R.id.name);
    }
}
