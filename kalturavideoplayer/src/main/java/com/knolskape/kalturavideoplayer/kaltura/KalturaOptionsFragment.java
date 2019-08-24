package com.knolskape.kalturavideoplayer.kaltura;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.knolskape.kalturavideoplayer.R;


import java.util.ArrayList;
import java.util.List;


public class KalturaOptionsFragment extends BottomSheetDialogFragment {

    RecyclerView optionsRV;
    String speedSelection;
    String quality;
    String subtitle;
    String audio;

    public boolean isQualityTracksAvailable() {
        return qualityTracksAvailable;
    }

    public void setQualityTracksAvailable(boolean qualityTracksAvailable) {
        this.qualityTracksAvailable = qualityTracksAvailable;
    }

    public boolean isAudioTracksAvailable() {
        return audioTracksAvailable;
    }

    public void setAudioTracksAvailable(boolean audioTracksAvailable) {
        this.audioTracksAvailable = audioTracksAvailable;
    }

    public boolean isSubtitleTracksAvailable() {
        return subtitleTracksAvailable;
    }

    public void setSubtitleTracksAvailable(boolean subtitleTracksAvailable) {
        this.subtitleTracksAvailable = subtitleTracksAvailable;
    }

    boolean qualityTracksAvailable;
    boolean audioTracksAvailable;
    boolean subtitleTracksAvailable;
    KalturaOptionsSelectionListener listener;
    private BottomSheetBehavior mBehavior;

    public static KalturaOptionsFragment getInstance(String speedSelection,
                                                     String quality,
                                                     String subtitle,
                                                     String audio,
                                                     boolean qualityTracksAvailable,
                                                     boolean audioTracksAvailable,
                                                     boolean subtitleTracksAvailable,
                                                     KalturaOptionsSelectionListener listener){

        KalturaOptionsFragment fragment = new KalturaOptionsFragment();
        fragment.setSpeedSelection(speedSelection);
        fragment.setListener(listener);
        fragment.setQuality(quality);
        fragment.setSubtitle(subtitle);
        fragment.setAudio(audio);
        fragment.setAudioTracksAvailable(audioTracksAvailable);
        fragment.setQualityTracksAvailable(qualityTracksAvailable);
        fragment.setSubtitleTracksAvailable(subtitleTracksAvailable);
        return fragment;
    }

    private void setSpeedSelection(String speedSelection){
        this.speedSelection = speedSelection;
    }

    private void setListener(KalturaOptionsSelectionListener listener){
        this.listener = listener;
    }

    private void setQuality(String quality){
        this.quality = quality;
    }

    private void setAudio(String audio){
        this.audio = audio;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.KalturaPlayerSheetDialogTheme);
    }

    @NonNull @Override
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

        List<KalturaOption> options = new ArrayList<KalturaOption>();


        options.add(new KalturaOption(KalturaVideoPlayer.KalturaOptions.SPEED.getDisplayName(), speedSelection));
        if(subtitleTracksAvailable){
            options.add(new KalturaOption(KalturaVideoPlayer.KalturaOptions.SUBTITLES.getDisplayName(),
                    subtitle!=null?subtitle:""));
        }

        if(qualityTracksAvailable){
            options.add(new KalturaOption(KalturaVideoPlayer.KalturaOptions.QUALITY.getDisplayName(),
                    quality != null && !quality.equals("Auto")?quality+"p":"Auto"));
        }

        if(audioTracksAvailable){
            options.add(new KalturaOption(KalturaVideoPlayer.KalturaOptions.AUDIO.getDisplayName(),
                    audio!=null?audio:""));
        }



        KalturaOptionsAdapter adapter = new KalturaOptionsAdapter(options, listener);

        optionsRV.setAdapter(adapter);
        return dialog;
    }

    public interface KalturaOptionsSelectionListener{
        public void onSelect(KalturaOption option);
    }
}
