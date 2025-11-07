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
 * 
 * <p>The adapter provides click listeners for canceling entrants and handles
 * different UI layouts based on the current mode.</p>
 */
public class EntrantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Sets the list type based on a string identifier and updates the adapter mode accordingly.
     *
     * @param currentListType The list type string ("WaitingList", "Chosen", "Cancelled", or "Final")
     */
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

    /**
     * Enum representing the different display modes for the entrant adapter.
     */
    public enum Mode { 
        /** Entrants on the waiting list */
        WAITING_LIST, 
        /** Entrants selected from the waitlist */
        CHOSEN, 
        /** Entrants who have been cancelled */
        CANCELLED, 
        /** Final confirmed attendees */
        FINAL 
    }

    private List<Entrant> entrants;
    private Mode mode = Mode.WAITING_LIST;
    private OnCancelClickListener cancelListener;

    /**
     * Constructs a new EntrantAdapter with the specified list of entrants.
     *
     * @param entrants The list of entrants to display
     */
    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    /**
     * Updates the list of entrants and notifies the adapter of the change.
     *
     * @param entrantsList The new list of entrants to display
     */
    public void setEntrants(List<Entrant> entrantsList) {
        this.entrants = entrantsList;
        notifyDataSetChanged();
    }

    /**
     * Sets the display mode for the adapter.
     *
     * @param mode The mode to set (WAITING_LIST, CHOSEN, CANCELLED, or FINAL)
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    /**
     * Sets the click listener for cancel actions on entrants.
     *
     * @param listener The listener to be called when an entrant is cancelled
     */
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

    /**
     * Interface for handling cancel click events on entrants.
     */
    public interface OnCancelClickListener {
        /**
         * Called when an entrant is cancelled.
         *
         * @param entrant The entrant that was cancelled
         */
        void onCancelClick(Entrant entrant);
    }
}
