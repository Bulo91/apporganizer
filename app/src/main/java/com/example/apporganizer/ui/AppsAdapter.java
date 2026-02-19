package com.example.apporganizer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apporganizer.R;
import com.example.apporganizer.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    private final List<AppInfo> items = new ArrayList<>();

    public void setItems(List<AppInfo> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = items.get(position);
        holder.txtLabel.setText(app.getLabel());
        holder.imgIcon.setImageDrawable(app.getIcon());

        // Plus tard (étape tri intelligent) :
        holder.txtCategory.setText("");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgIcon;
        final TextView txtLabel;
        final TextView txtCategory;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
