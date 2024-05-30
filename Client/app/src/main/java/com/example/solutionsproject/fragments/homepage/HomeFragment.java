package com.example.solutionsproject.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.UserCourseListRecyclerViewAdapter;
import com.example.solutionsproject.adapter.UserFlashcardSetListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentHomeBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;
import com.example.solutionsproject.model.gson.data.UserGson;

import java.util.List;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;

public class HomeFragment extends Fragment {
	private final String TAG = "Home_Fragment";
	private MainFacade mainFacade;
	private FragmentHomeBinding binding;
	ImageLoader imageLoader;
	private int userCourseCount;
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

		imageLoader = Coil.imageLoader(mainFacade.getMainActivity().getApplicationContext());
		String imageUrl = "http://" + mainFacade.getIpAddress() + ":" + mainFacade.getServerPort() + userGson.getProfilePicUrl();
		ImageRequest request = new ImageRequest.Builder(mainFacade.getMainActivity().getApplicationContext())
				.data(imageUrl)
				.error(R.drawable.vector_wrong_mark)
				.target(binding.homeIvProfile)
				.transformations(new CircleCropTransformation())
				.build();
		imageLoader.enqueue(request);

		return root;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
			@Override
			public void onSuccess(List<CourseGson> data) {
				userCourseCount = data.size();
				if(!data.isEmpty()) binding.homeTxtNoLearningPlan.setVisibility(View.GONE);
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

		mainFacade.getFlashcardSets(new ScholarMeServer.ResponseListener<List<FlashcardSetGson>>() {
			@Override
			public void onSuccess(List<FlashcardSetGson> data) {
				if (!data.isEmpty()){
					binding.homeTxtNoFlashcardset.setVisibility(View.GONE);
				}
				binding.homeListFlashcardsets.setAdapter(new UserFlashcardSetListRecyclerViewAdapter(
						mainFacade.getMainActivity().getApplicationContext(),
						data,
						flashcardSetId -> {
							HomeFragmentDirections.ActionHomeFragmentToFlashcardQuestionCreatorFragment action =
									HomeFragmentDirections.actionHomeFragmentToFlashcardQuestionCreatorFragment(Integer.parseInt(flashcardSetId));
							mainFacade.getHomeNavController().navigate(action);
						}
				));
				binding.homeListFlashcardsets.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
			}

			@Override
			public void onFailure(String message) {
				mainFacade.makeToast(message, Toast.LENGTH_SHORT);
			}
		});

		binding.homeTxtTimeWork.setText(String.valueOf(userCourseCount));

		initActions();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	private void initActions(){
		binding.homeBtnCreateFlashcardset.setOnClickListener(v ->{
			mainFacade.popupCreateFlashcardSet(binding.getRoot());
		});
	}

	private void func(String Id){

	}

}
	
	