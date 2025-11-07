package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.NotificationLogEntry;
import com.example.sprite.R;

/**
 * Adapter for displaying notification log entries in a RecyclerView.
 * 
 * <p>This adapter uses DiffUtil for efficient list updates and displays
 * notification log entries with organizer name, event title, message, type,
 * and date information.</p>
 */
public class NotificationLogAdapter
        extends ListAdapter<NotificationLogEntry, NotificationLogAdapter.VH> {

    public NotificationLogAdapter() { super(DIFF); }

    private static final DiffUtil.ItemCallback<NotificationLogEntry> DIFF =
            new DiffUtil.ItemCallback<>() {
                @Override public boolean areItemsTheSame(@NonNull NotificationLogEntry a, @NonNull NotificationLogEntry b) {
                    return a.message.equals(b.message) && a.dateText.equals(b.dateText);
                }
                @Override public boolean areContentsTheSame(@NonNull NotificationLogEntry a, @NonNull NotificationLogEntry b) {
                    return a.organizerName.equals(b.organizerName)
                            && a.eventTitle.equals(b.eventTitle)
                            && a.type == b.type;
                }
            };

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_log, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NotificationLogEntry it = getItem(position);
        h.orgHeader.setText("Organizer: " + it.organizerName + "  •  Event: " + it.eventTitle);
        h.message.setText(it.message);
        h.type.setText(it.type.name());
        h.date.setText(it.dateText);
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView orgHeader, message, type, date;
        VH(@NonNull View v) {
            super(v);
            orgHeader = v.findViewById(R.id.tv_org_header);
            message   = v.findViewById(R.id.tv_message);
            type      = v.findViewById(R.id.tv_type);
            date      = v.findViewById(R.id.tv_date);
        }
    }
}
