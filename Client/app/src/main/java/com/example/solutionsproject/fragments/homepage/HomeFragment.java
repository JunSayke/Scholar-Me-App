package com.example.solutionsproject.fragments.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chinalwb.are.AREditor;
import com.chinalwb.are.android.inner.Html;
import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.adapter.UserCourseListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.general.TimerService;
import com.example.solutionsproject.databinding.FragmentHomeBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.UserGson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeFragment extends Fragment {
	private final String TAG = "Home_Fragment";
	private MainFacade mainFacade;
	private FragmentHomeBinding binding;
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		try{
			mainFacade = MainFacade.getInstance();
		}catch (Exception e){
			throw new RuntimeException(e);
		}

		UserGson userGson = mainFacade.getSessionManager().getUserGson();

		String introduction = "Hi, " + userGson.getFirstName() + "!";
		binding.homeTxtNamePlaceholder.setText(introduction);

		Picasso.get()
				.load(userGson.getProfilePicUrl())
				.placeholder(R.drawable.__aa_default_user_icon)
				.error(R.drawable.__aa_default_user_icon)
				.into(binding.homeIvProfile);

		return root;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
			@Override
			public void onSuccess(List<CourseGson> data) {
				binding.homeListCourses.setAdapter(new UserCourseListRecyclerViewAdapter(
						mainFacade.getMainActivity().getApplicationContext(),
						data,
						itemId -> func(itemId)//TODO: ADD ACTION
				));
				binding.homeListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
			}

			@Override
			public void onFailure(String message) {
				mainFacade.makeToast(message, Toast.LENGTH_SHORT);
			}
		};

		mainFacade.getUserCourses(responseListener);

		initActions();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	private void initActions(){
		binding.homeIvProfile.setOnClickListener(v -> {
			mainFacade.getHomeNavController().navigate(R.id.action_homeFragment_to_accountNavContainer);
		});

		binding.homeBtnAdSearch.setOnClickListener(v ->{
			mainFacade.getHomeNavController().navigate(R.id.action_homeFragment_to_searchFragment);
		});

		binding.homeBtnCreateFlashcard.setOnClickListener(v ->{
			mainFacade.getHomeNavController().navigate(R.id.action_homeFragment_to_flashcardQuestionCreatorFragment);
		});
	}

	private void func(String Id){

	}

}
	
	