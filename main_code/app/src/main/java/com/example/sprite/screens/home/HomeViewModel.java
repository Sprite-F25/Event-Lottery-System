package com.example.sprite.screens.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the home fragment.
 * 
 * <p>This ViewModel manages the text content displayed in the home fragment.
 * Currently provides placeholder text for the home screen.</p>
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructs a new HomeViewModel.
     * Initializes the text content with a default message.
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * Gets the text content as LiveData.
     * 
     * @return LiveData containing the text to display
     */
    public LiveData<String> getText() {
        return mText;
    }
}