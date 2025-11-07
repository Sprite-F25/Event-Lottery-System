package com.example.sprite.screens.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the slideshow fragment.
 * 
 * <p>This ViewModel manages the text content displayed in the slideshow fragment.
 * Currently provides placeholder text for the slideshow screen.</p>
 */
public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructs a new SlideshowViewModel.
     * Initializes the text content with a default message.
     */
    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
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