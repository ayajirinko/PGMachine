package com.example.pgmachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WasherAdapter extends RecyclerView.Adapter<WasherAdapter.ViewHolder> {

    private List<ApiResponse.Data.Item> items;

    public WasherAdapter(List<ApiResponse.Data.Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiResponse.Data.Item item = items.get(position);
        holder.name.setText(item.getName());
        holder.status.setText(item.getStatus() == 1 ? "Available" : "Not Available");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(android.R.id.text1);
            status = view.findViewById(android.R.id.text2);
        }
    }
}
