package com.example.sprite.screens.manageUsers;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

public class ManageUsersFragment extends Fragment {

    private SearchView searchView;
    private MaterialButton usersButton, organizersButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_manage_users, container, false);

        searchView = view.findViewById(R.id.search_bar_usersList);
        usersButton = view.findViewById(R.id.users_button);
        organizersButton = view.findViewById(R.id.organizers_button);

        usersButton.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Users selected", Toast.LENGTH_SHORT).show();
                    usersButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.dark_grey)
                    ));
                    organizersButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.green3)
                    ));
                }
        );

        organizersButton.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Organizers selected", Toast.LENGTH_SHORT).show();
                    usersButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.green3)
                    ));
                    organizersButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.dark_grey)
                    ));
                }
        );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // You can react when they press Enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Real-time typing event
                // Later you will filter recycler here
                return false;
            }
        });

        return view;
    }
}
