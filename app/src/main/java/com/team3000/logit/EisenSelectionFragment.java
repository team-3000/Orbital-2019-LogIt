package com.team3000.logit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EisenSelectionFragment extends DialogFragment {
    private Spinner importanceSpinner;
    private Spinner urgencySpinner;
    private Button okButton;
    private Button clearButton;
    private onDestroyListener onDestroyListener;
    private String eisenTag;

    public interface onDestroyListener {
        void onDestroy(String eisenTag);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.eisenTag = "Select Priority";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout parentView = (LinearLayout) inflater.inflate(R.layout.fragment_eisen_selection, container, false);
        importanceSpinner = parentView.findViewById(R.id.spinner_importance);
        urgencySpinner = parentView.findViewById(R.id.spinner_urgency);
        okButton = parentView.findViewById(R.id.ok_button);
        clearButton = parentView.findViewById(R.id.clear_button);

        setClickListeners();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.i("EisenSelectionFragment", eisenTag);
        onDestroyListener.onDestroy(eisenTag);
    }

    public EisenSelectionFragment setOnDestroyListener(onDestroyListener listener) {
        EisenSelectionFragment newFragment = new EisenSelectionFragment();
        newFragment.onDestroyListener = listener;

        return newFragment;
    }

    private void setClickListeners() {
        okButton.setOnClickListener(v -> {
            String importance = importanceSpinner.getSelectedItem().toString();
            String urgency = urgencySpinner.getSelectedItem().toString();

            eisenTag = giveEisenTag(importance, urgency);
            EisenSelectionFragment.this.dismiss();
        });

        clearButton.setOnClickListener(v -> {
            eisenTag = "Select Priority";
            EisenSelectionFragment.this.dismiss();
        });
    }

    private String giveEisenTag(String importance, String urgency) {
        String eisenTag;

        if ("Important".equals(importance)) {
            eisenTag = "Urgent".equals(urgency) ? "Do" : "Decide";
        } else {
            eisenTag = "Urgent".equals(urgency) ? "Delegate" : "Eliminate";
        }

        return eisenTag;
    }
}
