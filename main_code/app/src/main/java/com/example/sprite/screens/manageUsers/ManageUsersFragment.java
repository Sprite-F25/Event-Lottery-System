package com.example.sprite.screens.manageUsers;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.UsersAdapter;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ManageUsersFragment extends Fragment {

    private MaterialButton entrantsButton, organizersButton, adminButton;
    private UsersAdapter adapter;
    private ManageUsersViewModel viewModel;
    private FilterType currentFilter = FilterType.ALL; // Track current filter

    private enum FilterType {
        ALL, ENTRANTS, ORGANIZERS, ADMIN
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_manage_users, container, false);

        viewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);
        viewModel.loadAllUsers();

        entrantsButton = view.findViewById(R.id.entrants_button);
        organizersButton = view.findViewById(R.id.organizers_button);
        adminButton = view.findViewById(R.id.admin_button);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_users);
        adapter = new UsersAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // apply current filter
        viewModel.getAllUsers().observe(getViewLifecycleOwner(), list -> applyCurrentFilter());
        viewModel.getEntrants().observe(getViewLifecycleOwner(), list -> applyCurrentFilter());
        viewModel.getOrganizers().observe(getViewLifecycleOwner(), list -> applyCurrentFilter());
        viewModel.getAdmin().observe(getViewLifecycleOwner(), list -> applyCurrentFilter());

        // filter based on entrant
        entrantsButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Entrants selected", Toast.LENGTH_SHORT).show();
            currentFilter = FilterType.ENTRANTS;
            updateButtonColors();
            displayEntrants();
        });

        // filter based on organizer
        organizersButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Organizers selected", Toast.LENGTH_SHORT).show();
            currentFilter = FilterType.ORGANIZERS;
            updateButtonColors();
            displayOrganizers();
        });

        // filter based on admin
        adminButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Admin selected", Toast.LENGTH_SHORT).show();
            currentFilter = FilterType.ADMIN;
            updateButtonColors();
            displayAdmin();
        });

        // delete user
        adapter.setOnUserClickListener(user -> showDeletePopup(user));

        return view;
    }

    private void applyCurrentFilter() {
        switch (currentFilter) {
            case ENTRANTS:
                displayEntrants();
                break;
            case ORGANIZERS:
                displayOrganizers();
                break;
            case ADMIN:
                displayAdmin();
                break;
            case ALL:
            default:
                displayUsers();
                break;
        }
    }

    private void updateButtonColors() {
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.dark_grey);
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.green3);

        entrantsButton.setBackgroundTintList(ColorStateList.valueOf(
                currentFilter == FilterType.ENTRANTS ? selectedColor : unselectedColor));
        organizersButton.setBackgroundTintList(ColorStateList.valueOf(
                currentFilter == FilterType.ORGANIZERS ? selectedColor : unselectedColor));
        adminButton.setBackgroundTintList(ColorStateList.valueOf(
                currentFilter == FilterType.ADMIN ? selectedColor : unselectedColor));
    }

    public void displayUsers(){
        adapter.submitList(viewModel.getAllUsers().getValue());
    }
    public void displayEntrants(){
        adapter.submitList(viewModel.getEntrants().getValue());
    }
    public void displayOrganizers(){
        adapter.submitList(viewModel.getOrganizers().getValue());
    }
    public void displayAdmin(){
        adapter.submitList(viewModel.getAdmin().getValue());
    }

    private void showDeletePopup(User user) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_delete_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        MaterialButton cancelButton = popupView.findViewById(R.id.cancel_button);
        MaterialButton confirmButton = popupView.findViewById(R.id.confirm_delete_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.deleteUser(user);
        });

        dialog.show();
    }
}
