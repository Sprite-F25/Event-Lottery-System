package com.example.sprite.screens.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EventAdapter;
import com.example.sprite.Adapters.ImagesEventAdapter;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.example.sprite.screens.eventsList.EventsListViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
* Fragment for admins to view and delete images.
*
 * <p>This fragment allows administrators to view a all images, and delete images.
 *   It displays all images as a list, and provides a delete button
 *  with confirmation dialog when images are clicked.</p>
 */

public class ManageImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImagesEventAdapter adapter;
    private ManageImagesViewModel mViewModel;
    private TextView emptyTextView;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_images, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_manage_images);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ImagesEventAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        emptyTextView = view.findViewById(R.id.tv_empty);

        mViewModel = new ViewModelProvider(this).get(ManageImagesViewModel.class);
        mViewModel.loadAllEvents();

        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events == null ) {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnItemClickListener(event -> {
            LayoutInflater popupInflater = LayoutInflater.from(requireContext());
            View popupView = popupInflater.inflate(R.layout.fragment_confirm_popup, null);


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(popupView);

            AlertDialog dialog = builder.create();

            TextView title = popupView.findViewById(R.id.popupTitleTextView);
            title.setText("Delete Image");

            TextView confirmText = popupView.findViewById(R.id.popup_dialog);
            confirmText.setText("Are you sure you want to delete this image? \nThis action cannot be undone.");

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
            MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

            confirmBtn.setOnClickListener(view1 -> {
                mViewModel.removeImage(event);
                mViewModel.loadAllEvents();
                dialog.dismiss();
            });

            cancelBtn.setOnClickListener(view12 -> dialog.dismiss());
            dialog.show();
        });


    };
}
