package com.example.sprite.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.R;
import com.example.sprite.Adapters.NotificationLogAdapter;
import com.example.sprite.Models.NotificationLogEntry;
import com.example.sprite.ViewModels.NotificationLogsViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.EnumSet;
import java.util.Set;

/**
 * Fragment that displays notification logs for organizers. (admins ???)
 * 
 * <p>This fragment shows a list of notification log entries with filtering
 * capabilities. Users can search by text and filter by notification type
 * (invited, accepted, declined, replacement, waitlist joined/left).</p>
 */
public class NotificationLogsFragment extends Fragment {

    private NotificationLogAdapter adapter;
    private NotificationLogsViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        vm = new ViewModelProvider(this).get(NotificationLogsViewModel.class);
        adapter = new NotificationLogAdapter();

        RecyclerView rv = v.findViewById(R.id.rv_notification_logs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        View tvEmpty = v.findViewById(R.id.tv_empty);
        ChipGroup chips = v.findViewById(R.id.chips_type);
        TextInputEditText search = v.findViewById(R.id.et_search);


        vm.visibleLogs.observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            tvEmpty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
        });


        if (search != null) {
            search.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    vm.setSearchQuery(s == null ? "" : s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }


        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Set<NotificationLogEntry.Type> set = EnumSet.noneOf(NotificationLogEntry.Type.class);
            if (checkedIds.contains(R.id.ch_invited))     set.add(NotificationLogEntry.Type.INVITED);
            if (checkedIds.contains(R.id.ch_accepted))    set.add(NotificationLogEntry.Type.ACCEPTED);
            if (checkedIds.contains(R.id.ch_declined))    set.add(NotificationLogEntry.Type.DECLINED);
            if (checkedIds.contains(R.id.ch_replacement)) set.add(NotificationLogEntry.Type.REPLACEMENT);
            if (checkedIds.contains(R.id.ch_waitlist))    set.add(NotificationLogEntry.Type.WAITLIST_JOINED);
            vm.setTypeFilter(set);
        });

        // demo data for now (replace with Firestore later)
        vm.load();
    }
}
