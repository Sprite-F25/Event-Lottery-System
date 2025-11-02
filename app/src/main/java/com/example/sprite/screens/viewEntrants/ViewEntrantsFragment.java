//package com.example.sprite.screens.viewEntrants;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.sprite.Adapters.EntrantAdapter;
//import com.example.sprite.Models.Entrant;
//import com.example.sprite.R;
//
//import java.util.List;
//
//public class ViewEntrantsFragment extends Fragment {
//
//    private ViewEntrantsViewModel mViewModel;
//    private RecyclerView recyclerView;
//    private EntrantAdapter adapter;
//    private List<Entrant> entrants;
//
//    public static ViewEntrantsFragment newInstance() {
//        return new ViewEntrantsFragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        // Link UI to view
//        View view = inflater.inflate(R.layout.fragment_view_entrants, container, false);
//        recyclerView = view.findViewById(R.id.recycler_view_entrants);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // set up recyclerView adapter
//        adapter = new EntrantAdapter(entrants);
//        recyclerView.setAdapter(adapter);
//
//        // Set up ViewModel
//        mViewModel = new ViewModelProvider(this).get(ViewEntrantsViewModel.class);
//
//        // observe LiveData - so that it automatically updates
////        mViewModel.getEntrants().observe(getViewLifecycleOwner(), events -> {
////            adapter.setEntrants(events);
////            adapter.notifyDataSetChanged();
////        });
//
//        return view;
//    }
//}