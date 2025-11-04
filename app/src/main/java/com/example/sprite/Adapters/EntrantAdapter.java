package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.Entrant;
import com.example.sprite.R;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    private List<Entrant> entrants;

    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    public void setEntrants(List<Entrant> entrantsList) {
        this.entrants = entrantsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entrant_list_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = entrants.get(position);
        holder.name.setText(entrant.getName());
        holder.role.setText(entrant.getRole().ordinal());
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.entrant_name);
            role = itemView.findViewById(R.id.entrant_user_role);
        }
    }
}
