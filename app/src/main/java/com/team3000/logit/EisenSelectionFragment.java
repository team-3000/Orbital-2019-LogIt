package com.team3000.logit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EisenSelectionFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout parentView = (LinearLayout) inflater.inflate(R.layout.fragment_eisen_selection, container, false);

        return parentView;
    }

    // Set the size of the dialog window
    @Override
    public void onStart() {
        super.onStart();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }
}
