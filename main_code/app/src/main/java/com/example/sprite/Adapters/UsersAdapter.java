package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.User;
import com.example.sprite.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a list of User objects in a RecyclerView.
 * Supports updating the displayed list and handling delete-button clicks
 * on individual user items.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> users = new ArrayList<>();
    private OnUserClickListener listener;

    /**
     * Listener interface for handling delete actions on individual users.
     */
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    /**
     * Registers a listener that will be notified when a user is selected
     * for deletion.
     *
     * @param listener The callback invoked on delete-button clicks.
     */
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    /**
     * Replaces the current list of users displayed by the adapter.
     *
     * @param newList The updated list of users; if null, an empty list is used.
     */
    public void submitList(List<User> newList) {
        users = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Inflates the layout for a single user item view.
     *
     * @param parent   The parent ViewGroup that will host the new View.
     * @param viewType The type of View (unused since there's only one type).
     * @return A ViewHolder representing the inflated user layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds a User's information to the corresponding item view.
     *
     * @param holder   The ViewHolder to bind data into.
     * @param position The position of the user in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User u = users.get(position);
        holder.name.setText(u.getName());
        holder.role.setText(u.getRole().toString());
        holder.email.setText(u.getEmail());
    }

    /**
     * Returns the number of users currently shown.
     *
     * @return Size of the internal user list.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder representing a single row in the users list.
     * Binds view references and forwards delete-button clicks
     * to the registered click listener.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, role, email;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            role = itemView.findViewById(R.id.user_role);
            email = itemView.findViewById(R.id.user_email);
            deleteButton = itemView.findViewById(R.id.delete_button);

            deleteButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(users.get(position));
                }
            });
        }
    }
}