package com.knolskape.kalturavideoplayer.kaltura;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.knolskape.kalturavideoplayer.R;


public class KalturaSpeedSelectionFragment extends BottomSheetDialogFragment {
    RecyclerView optionsRV;
    KalturaSpeedSelectionListener listener;
    KalturaSpeedAdapter adapter;
    KalturaVideoPlayer.KalturaVideoSpeed selectedSpeed;
    private BottomSheetBehavior mBehavior;
    public static KalturaSpeedSelectionFragment getInstance(KalturaSpeedSelectionListener listener,
                                                            KalturaVideoPlayer.KalturaVideoSpeed selectedSpeed){
        KalturaSpeedSelectionFragment fragment =  new KalturaSpeedSelectionFragment();
        fragment.listener = listener;
        fragment.selectedSpeed = selectedSpeed;
        return fragment;
    }

    private void setListener(KalturaSpeedSelectionListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.KalturaPlayerSheetDialogTheme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.kaltura_options_layout, null);
        RecyclerView optionsRV = (RecyclerView) view.findViewById(R.id.options_rv);
        optionsRV.setHasFixedSize(true);
        optionsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        optionsRV.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        adapter = new KalturaSpeedAdapter(listener, selectedSpeed);

        optionsRV.setAdapter(adapter);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public interface KalturaSpeedSelectionListener{
        public void onSelectSpeed(float speed);
    }
}
