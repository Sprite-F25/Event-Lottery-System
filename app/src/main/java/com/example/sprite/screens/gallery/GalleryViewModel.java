package com.example.sprite.screens.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the gallery fragment.
 * 
 * <p>This ViewModel manages the text content displayed in the gallery fragment.
 * Currently provides placeholder text for the gallery screen.</p>
 */
public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructs a new GalleryViewModel.
     * Initializes the text content with a default message.
     */
    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
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