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
import com.kaltura.playkit.player.VideoTrack;
import com.knolskape.kalturavideoplayer.R;

import java.util.List;

public class KalturaQualitySelectionFragment extends BottomSheetDialogFragment {
    RecyclerView optionsRV;
    private KalturaQualitySelectionListener listener;
    private List<VideoTrack> tracks;
    private String selectedTrack;
    private BottomSheetBehavior mBehavior;

    public static KalturaQualitySelectionFragment
    getInstance(List<VideoTrack> tracks, KalturaQualitySelectionListener listener,
                String selectedTrack){
        KalturaQualitySelectionFragment fragment = new KalturaQualitySelectionFragment();
        fragment.setListener(listener);
        fragment.setTracks(tracks);
        fragment.setSelectedTrack(selectedTrack);
        return fragment;
    }

    private void setListener(KalturaQualitySelectionListener listener){
        this.listener = listener;
    }

    private void setTracks(List<VideoTrack> tracks){
        this.tracks = tracks;
    }

    private void setSelectedTrack(String track){
        this.selectedTrack = track;
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
        RecyclerView.LayoutManager manager =
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        optionsRV.setLayoutManager(manager);
        optionsRV.setAdapter(new KalturaQualityAdapter(tracks, listener, selectedTrack));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public interface KalturaQualitySelectionListener {
        public void onSelectQuality(VideoTrack track);
    }
}
