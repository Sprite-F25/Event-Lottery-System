package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.Entrant;
import com.example.sprite.R;

import java.util.List;

/**
 * Adapter for displaying a list of entrants in a RecyclerView.
 * 
 * <p>This adapter supports different display modes based on the entrant list type:
 * <ul>
 *     <li><b>WAITING_LIST:</b> Shows entrants on the waiting list</li>
 *     <li><b>CHOSEN:</b> Shows selected entrants</li>
 *     <li><b>CANCELLED:</b> Shows cancelled entrants</li>
 *     <li><b>FINAL:</b> Shows final confirmed attendees</li>
 * </ul>
 * </p>
 * 
 * <p>The adapter provides click listeners for canceling entrants and handles
 * different UI layouts based on the current mode.</p>
 */
public class EntrantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public void setListType(String currentListType) {
        switch (currentListType) {
            case "WaitingList":
                setMode(Mode.WAITING_LIST);
                break;
            case "Chosen":
                setMode(Mode.CHOSEN);
                break;
            case "Cancelled":
                setMode(Mode.CANCELLED);
                break;
            case "Final":
                setMode(Mode.FINAL);
                break;
            default:
                setMode(Mode.WAITING_LIST);
                break;
        }
    }

    public enum Mode { WAITING_LIST, CHOSEN, CANCELLED, FINAL }

    private List<Entrant> entrants;
    private Mode mode = Mode.WAITING_LIST;
    private OnCancelClickListener cancelListener;

    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    public void setEntrants(List<Entrant> entrantsList) {
        this.entrants = entrantsList;
        notifyDataSetChanged();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (mode == Mode.CHOSEN) ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.entrant_list_item_with_cancel_button, parent, false);
            return new CancelledViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.entrant_list_item, parent, false);
            return new EntrantViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Entrant entrant = entrants.get(position);
        if (holder instanceof EntrantViewHolder) {
            ((EntrantViewHolder) holder).bind(entrant);
        } else if (holder instanceof CancelledViewHolder) {
            ((CancelledViewHolder) holder).bind(entrant);
        }
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    // Standard ViewHolder
    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;

        EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.entrant_name);
            role = itemView.findViewById(R.id.entrant_user_role);
        }

        void bind(Entrant entrant) {
            name.setText(entrant.getName());
            role.setText(entrant.getRole().name());
        }
    }

    // Cancelled ViewHolder - for 'selected list' where organizers can cancel users
    class CancelledViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;
        Button cancelButton;

        CancelledViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.entrant_name);
            role = itemView.findViewById(R.id.entrant_user_role);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }

        void bind(Entrant entrant) {
            name.setText(entrant.getName());
            role.setText(entrant.getRole().name());
            // When the cancel button is clicked, call the fragment's listener
            cancelButton.setOnClickListener(v -> {
                if (cancelListener != null) cancelListener.onCancelClick(entrant);
            });
        }
    }

    public interface OnCancelClickListener {
        void onCancelClick(Entrant entrant);
    }
}
