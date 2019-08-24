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
import com.kaltura.playkit.player.AudioTrack;
import com.knolskape.kalturavideoplayer.R;

import java.util.List;

public class KalturaAudioSelectionFragment extends BottomSheetDialogFragment {
    RecyclerView optionsRV;
    private KalturaAudioSelectionListener listener;
    private List<AudioTrack> tracks;
    private String selectedTrack;
    private BottomSheetBehavior mBehavior;

    public static KalturaAudioSelectionFragment
    getInstance(List<AudioTrack> tracks, KalturaAudioSelectionListener listener,
                String selectedTrack){
        KalturaAudioSelectionFragment fragment = new KalturaAudioSelectionFragment();
        fragment.setListener(listener);
        fragment.setTracks(tracks);
        fragment.setSelectedTrack(selectedTrack);
        return fragment;
    }

    public void setListener(KalturaAudioSelectionListener listener) {
        this.listener = listener;
    }

    public void setTracks(List<AudioTrack> tracks) {
        this.tracks = tracks;
    }

    public void setSelectedTrack(String selectedTrack) {
        this.selectedTrack = selectedTrack;
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
        optionsRV.setAdapter(new KalturaAudioAdapter(tracks, listener, selectedTrack));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public interface KalturaAudioSelectionListener {
        public void onSelectAudio(AudioTrack track);
    }
}
