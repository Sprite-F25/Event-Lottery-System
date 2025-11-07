package com.example.sprite.screens.viewEntrants;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EntrantAdapter;
import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * Fragment that displays the list of entrants registered for a selected event.
 * Uses ViewEntrantsViewModel to load entrants and supports real-time updates.
 * Typically accessed by organizers or admins for event management.
 */


public class ViewEntrantsFragment extends Fragment {

    private ViewEntrantsViewModel mViewModel;
    private RecyclerView recyclerView;
    private EntrantAdapter adapter;

    private Button notifFab;
    private Button exportFab;

    private String currentListType = "WaitingList";
    private Event currentEvent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_entrants, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize FABs
        notifFab = rootView.findViewById(R.id.fab_send_notif);
        exportFab = rootView.findViewById(R.id.fab_export_csv);

        // Initialize adapter
        adapter = new EntrantAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(ViewEntrantsViewModel.class);

        // Observe the currently selected entrant list
        mViewModel.getCurrentEntrantList().observe(getViewLifecycleOwner(), entrants -> {
            adapter.setEntrants(entrants);
        });

        // Setup dropdown
        Spinner dropdown = rootView.findViewById(R.id.dropdown_UI);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentListType = parent.getItemAtPosition(position).toString();

                // Pass list type + current event to ViewModel
                if (currentEvent != null) {
                    mViewModel.selectList(currentListType, currentEvent);
                }

                // Show/hide FABs based on list shown
                notifFab.setVisibility(currentListType.equals("Final") ? View.GONE : View.VISIBLE);
                exportFab.setVisibility(currentListType.equals("Final") ? View.VISIBLE : View.GONE);

                adapter.setListType(currentListType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load event from arguments
        currentEvent = (Event) getArguments().getSerializable("selectedEvent");
        if (currentEvent != null) {
            // Optionally, tell ViewModel the event ID for any database ops
            mViewModel.setEventId(currentEvent.getEventId());

            // Load default entrant list
            mViewModel.selectList(currentListType, currentEvent);
        }

        // Notification FAB click
        notifFab.setOnClickListener(v -> showNotificationPopup());

        // Export CSV FAB click
        exportFab.setOnClickListener(v -> {
            // TODO: Implement CSV export logic - part 4
        });

        adapter.setOnCancelClickListener(entrant -> {
            showConfirmPopup(entrant);
        });

        return rootView;
    }

    private void showNotificationPopup() {
        String hintText;
        switch (currentListType) {
            case "Chosen":
                hintText = "You were selected to sign up for this event. Please check event details.";
                break;
            case "WaitingList":
            case "Cancelled":
            default:
                hintText = "Enter text here";
                break;
        }

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint(hintText);

        new AlertDialog.Builder(getContext())
                .setTitle("Send Notification")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString();
                    // TODO: send notification to all entrants in current list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showConfirmPopup(Entrant entrant) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.fragment_confirm_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);

        AlertDialog dialog = builder.create();

        // Set the title
        TextView title = popupView.findViewById(R.id.popupTitleTextView);
        title.setText("Cancel Entrant");

        // Set the confirmation text
        TextView confirmText = popupView.findViewById(R.id.textView3);
        confirmText.setText("Are you sure you want to cancel the entrant?");

        MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
        MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

        confirmBtn.setOnClickListener(v -> {

            mViewModel.cancelEntrant(currentEvent, entrant);
            dialog.dismiss();
        });

        confirmBtn.setOnClickListener(v -> {
            mViewModel.cancelEntrant(currentEvent, entrant);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
