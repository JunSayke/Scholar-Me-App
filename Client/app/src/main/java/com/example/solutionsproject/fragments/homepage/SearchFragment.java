package com.example.solutionsproject.fragments.homepage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentSearchBinding;
import com.example.solutionsproject.fragments.homepage.CoursesFragmentDirections;
import com.example.solutionsproject.model.gson.data.CourseGson;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private final String TAG = "Search_Fragment";
    private MainFacade mainFacade;
    private FragmentSearchBinding binding;
    private List<CourseGson> listingList, activeListingList, courseGsonList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        courseGsonList = new ArrayList<CourseGson>();
        activeListingList = new ArrayList<CourseGson>();
        listingList = new ArrayList<CourseGson>();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                //Log.d(TAG, "Hello");
                if(!data.isEmpty()) binding.searchTxtNoCourses.setVisibility(View.GONE);
                initSearchBar();
                listingList = data;
                updateCourseScrollViewItems(data);
                binding.searchListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };
        mainFacade.getCourses(responseListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initSearchBar() {
        binding.searchEttSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG, "onTextChanged: " + s);
                updateItemListings(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // -- SEARCHING METHODS
    private void updateCourseScrollViewItems(List<CourseGson> data){
        binding.searchListCourses.setAdapter(new CourseListRecyclerViewAdapter(
                mainFacade.getMainActivity().getApplicationContext(),
                data,
                itemId -> {
                    SearchFragmentDirections.ActionMenuSearchToCourseDemoFragment action =
                            SearchFragmentDirections.actionMenuSearchToCourseDemoFragment(Integer.parseInt(itemId));
                    action.setCourseId(Integer.parseInt(itemId));
                    mainFacade.getHomepageNavController().navigate(action);
                }, mainFacade
        ));
    }

    private void updateItemListings(CharSequence s) {
        activeListingList.clear();

        for(CourseGson items : listingList) {
            if(items.getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
                Log.d(TAG, items.getTitle());
                activeListingList.add(items);
            }
            if(activeListingList.isEmpty()){
                binding.searchTxtNoCourses.setVisibility(View.VISIBLE);
            }else{
                binding.searchTxtNoCourses.setVisibility(View.GONE);
            }
        }
        updateCourseScrollViewItems(activeListingList);
    }
}