package com.knolskape.kalturavideoplayer.kaltura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.knolskape.kalturavideoplayer.R;
import java.io.Serializable;
import java.util.ArrayList;


public class KalturaVideoFragment extends Fragment {

    private static final String CURRENT_POS="curr_pos";
    private static final String IS_PLAYING="playing";
    private static final String ENTRY_ID="entryId";
    private static final String UNIQUE_IDS="uniqueIds";
    private static final String TEXT_TRACK="textTrack";
    private static final String VIDEO_TRACK="videoTrack";
    private static final String AUDIO_TRACK="audioTrack";
    private static final String TEXT_TRACK_LABEL="textTrackLabel";
    private static final String VIDEO_TRACK_LABEL="videoTrackLabel";
    private static final String AUDIO_TRACK_LABEL="audioTrackLabel";
    private static final String VIDEO_SPEED="videoSpeed";
    private static final String MARK_READ="markRead";
    private Long startPosition = 0L; // position tp start playback in seconds.
    private String sourceUrl = null;
    private String thumbnailUrl = null;
    private String mediaSourceId = null;
    private String partnerId = null;
    private String subPartnerId = null;
    private int thumbnailHeight = 768;
    private int thumbnailWidth = 1024;

    KalturaVideoPlayer videoPlayer;
    View optoinsMenu;
    View nav;
    View backBtn;
    private long currentPosition = -1;
    private String selectedVideoTrack;
    private String selectedTextTrack;
    private String selectedAudioTrack;
    private String selectedVideoTrackLabel;
    private String selectedTextTrackLabel;
    private String selectedAudioTrackLabel;
    private KalturaVideoPlayer.KalturaVideoSpeed displayedSpeed;
    ArrayList<String> uniqueIds = new ArrayList<String>();
    private boolean isPlaying = false;
    private String entryId = "0_4uba18mu";
    private boolean autoplay;
    private boolean markedAsComplete = false;
    KalturaOptionsFragment.KalturaOptionsSelectionListener optionsSelectionListener;
    KalturaSpeedSelectionFragment.KalturaSpeedSelectionListener speedSelectionListener;
    KalturaQualitySelectionFragment.KalturaQualitySelectionListener qualitySelectionListener;
    KalturaSubtitleSelectionFragment.KalturaSubtitleSelectionListener subtitleSelectionListener;
    KalturaAudioSelectionFragment.KalturaAudioSelectionListener audioSelectionListener;

    public static KalturaVideoFragment getInstance(KalturaVideoPlayer.KalturaPlayerConfig config){
        Bundle b = new Bundle();
        b.putString("entryId", config.getEntryId());
        b.putBoolean("autoplay", config.isAutoplay());
        b.putString("sourceUrl", config.getSourceUrl());
        b.putString("mediaSourceId", config.getMediaSourceId());
        b.putLong("startPosition", config.getStartPosition());
        b.putString("partnerId", config.getPartnerId());
        b.putString("subPartnerId", config.getSubPartnerId());
        b.putInt("thumbnailHeight", config.getThumbnailHeight());
        b.putInt("thumbnailWidth", config.getThumbnailWidth());

        KalturaVideoFragment f = new KalturaVideoFragment();
        f.setArguments(b);
        return f;
    }

    public KalturaVideoFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kaltura_demo, container, false);

        videoPlayer = view.findViewById(R.id.video_player);
        View optoinsMenu = view.findViewById(R.id.options_menu);
        View nav = view.findViewById(R.id.video_nav);
        View backBtn = view.findViewById(R.id.back_btn);

        entryId = getArguments().getString("entryId");
        autoplay = getArguments().getBoolean("autoplay", true);
        sourceUrl = getArguments().getString("sourceUrl", null);
        mediaSourceId = getArguments().getString("mediaSourceId");
        entryId = getArguments().getString("entryId");
        startPosition = getArguments().getLong("startPosition");
        partnerId = getArguments().getString("partnerId");
        subPartnerId = getArguments().getString("subPartnerId");
        thumbnailHeight = getArguments().getInt("thumbnailHeight", 768);
        thumbnailWidth = getArguments().getInt("thumbnailWidth", 1024);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });


        speedSelectionListener = speed -> {
            videoPlayer.setPlaybackRate(speed);
            Toast.makeText(getContext(), "Speed changed to "+speed, Toast.LENGTH_SHORT).show();
            Fragment fragment = getFragmentManager().findFragmentByTag("VideoOptions");
            if(fragment instanceof BottomSheetDialogFragment){
                ((BottomSheetDialogFragment) fragment).dismiss();
            }
        };

        qualitySelectionListener = videoTrack -> {
            videoPlayer.changeTrack(videoTrack);
            selectedVideoTrack = videoTrack.getUniqueId();
            selectedVideoTrackLabel = String.valueOf(videoTrack.getHeight());
            Fragment fragment = getFragmentManager().findFragmentByTag("QualityOptions");
            if(fragment instanceof BottomSheetDialogFragment){
                ((BottomSheetDialogFragment) fragment).dismiss();
            }
        };

        subtitleSelectionListener = textTrack -> {
            videoPlayer.changeTrack(textTrack);
            selectedTextTrack = textTrack.getUniqueId();
            selectedTextTrackLabel = textTrack.getLabel();
            Fragment fragment = getFragmentManager().findFragmentByTag("SubtitleOptions");
            if(fragment instanceof BottomSheetDialogFragment){
                ((BottomSheetDialogFragment) fragment).dismiss();
            }
        };

        audioSelectionListener = audioTrack ->{
            videoPlayer.changeTrack(audioTrack);
            selectedAudioTrack = audioTrack.getUniqueId();
            selectedAudioTrackLabel = audioTrack.getLabel();
            Fragment fragment = getFragmentManager().findFragmentByTag("AudioOptions");
            if(fragment instanceof BottomSheetDialogFragment){
                ((BottomSheetDialogFragment) fragment).dismiss();
            }
        };


        optionsSelectionListener = option -> {
            if(option.name.equals(KalturaVideoPlayer.KalturaOptions.SPEED.getDisplayName())){
                KalturaSpeedSelectionFragment fragment =
                        KalturaSpeedSelectionFragment.getInstance(speedSelectionListener, displayedSpeed);
                fragment.show(getFragmentManager(), "VideoOptions");

            }else if(option.name.equals(KalturaVideoPlayer.KalturaOptions.QUALITY.getDisplayName())){
                KalturaQualitySelectionFragment fragment =
                        KalturaQualitySelectionFragment.getInstance(videoPlayer.getVideoTracks(),
                                qualitySelectionListener, selectedVideoTrack);
                fragment.show(getFragmentManager(),"QualityOptions" );
            }else if(option.name.equals(KalturaVideoPlayer.KalturaOptions.SUBTITLES.getDisplayName())){
                KalturaSubtitleSelectionFragment fragment =
                        KalturaSubtitleSelectionFragment.getInstance(videoPlayer.getTextTracks(),
                                subtitleSelectionListener, selectedTextTrack);
                fragment.show(getFragmentManager(),"SubtitleOptions" );
            }else if(option.name.equals(KalturaVideoPlayer.KalturaOptions.AUDIO.getDisplayName())){
                KalturaAudioSelectionFragment fragment =
                        KalturaAudioSelectionFragment.getInstance(videoPlayer.getAudioTracks(),
                                audioSelectionListener, selectedAudioTrack);
                fragment.show(getFragmentManager(),"AudioOptions" );
            }
            Fragment fragment = getFragmentManager().findFragmentByTag("Options");
            if(fragment instanceof BottomSheetDialogFragment){
                ((BottomSheetDialogFragment) fragment).dismiss();
            }
        };


        videoPlayer.configurePlayer(entryId, partnerId,
                subPartnerId, sourceUrl, thumbnailHeight, thumbnailWidth);
        if(savedInstanceState != null){
            long currentPos = savedInstanceState.getLong(CURRENT_POS, -1);
            boolean isPlaying = savedInstanceState.getBoolean(IS_PLAYING, false);
            ArrayList<String> ids = savedInstanceState.getStringArrayList(UNIQUE_IDS);
            selectedTextTrackLabel = savedInstanceState.getString(TEXT_TRACK_LABEL, null);
            selectedVideoTrackLabel = savedInstanceState.getString(VIDEO_TRACK_LABEL, null);
            selectedAudioTrackLabel = savedInstanceState.getString(AUDIO_TRACK_LABEL, null);
            selectedTextTrack = savedInstanceState.getString(TEXT_TRACK, null);
            selectedAudioTrack = savedInstanceState.getString(AUDIO_TRACK, null);
            selectedVideoTrack = savedInstanceState.getString(VIDEO_TRACK, null);
            markedAsComplete = savedInstanceState.getBoolean(MARK_READ, false);
            Serializable speed = savedInstanceState.getSerializable(VIDEO_SPEED);
            displayedSpeed = (speed!=null)?(KalturaVideoPlayer.KalturaVideoSpeed)speed:null;

            if(currentPos != -1){
                videoPlayer.restoreState(currentPos, isPlaying, ids, displayedSpeed.getSpeed());
            }
        }

        videoPlayer.addStateChangeListener(new KalturaVideoPlayer.OnPlayerStateChangeListener() {
            @Override
            public void onPause() {

            }

            @Override
            public void onPlay() {
                nav.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReplay() {

            }

            @Override
            public void onSeek(long seekPos, int completionPercentage, boolean fromUser) {

            }

            @Override
            public void onControlsShow() {
                nav.animate().alpha(1.0f).setDuration(500);
            }

            @Override
            public void onControlsHide() {
                nav.animate().alpha(0.0f).setDuration(500);
            }

            @Override
            public void onEnd() {

            }
        });

        optoinsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float playbackRate = videoPlayer.getPlaybackRate();
                displayedSpeed = getDisplayedSpeed(playbackRate);
                KalturaOptionsFragment fragment = KalturaOptionsFragment
                        .getInstance(displayedSpeed.getDisplayName(),
                                selectedVideoTrackLabel!=null?selectedVideoTrackLabel:"Auto",
                                selectedTextTrackLabel!=null?selectedTextTrackLabel:"",
                                selectedAudioTrackLabel!=null?selectedAudioTrackLabel:"Default",
                                videoPlayer.getVideoTracks() != null
                                        && videoPlayer.getVideoTracks().size() > 0,
                                videoPlayer.getAudioTracks() != null
                                        && videoPlayer.getAudioTracks().size() > 0,
                                videoPlayer.getTextTracks() != null
                                        && videoPlayer.getTextTracks().size() > 0,
                                optionsSelectionListener
                                );
                fragment.show(getFragmentManager(), "Options");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        if(autoplay){
            videoPlayer.dismissThumbnailAndStartPlaying();
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.destroyPlayer();
    }

    private KalturaVideoPlayer.KalturaVideoSpeed getDisplayedSpeed(float playbackRate){
        KalturaVideoPlayer.KalturaVideoSpeed speed = KalturaVideoPlayer.KalturaVideoSpeed.NORMAL;
        if(playbackRate == 1.0f){
            speed = KalturaVideoPlayer.KalturaVideoSpeed.NORMAL;
        }else if(playbackRate == 0.5f){
            speed = KalturaVideoPlayer.KalturaVideoSpeed.SLOWEST;
        }else if(playbackRate == 1.5f){
            speed = KalturaVideoPlayer.KalturaVideoSpeed.FASTER;
        } else if (playbackRate == 2.0f) {
            speed = KalturaVideoPlayer.KalturaVideoSpeed.FASTEST;
        }else if(playbackRate == 0.75f){
            speed = KalturaVideoPlayer.KalturaVideoSpeed.SLOW;
        }else if(playbackRate == 1.25f){
            speed = KalturaVideoPlayer.KalturaVideoSpeed.FAST;
        }
        return speed;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        videoPlayer.restoreStateInternal();
        uniqueIds.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_POS, currentPosition);
        outState.putBoolean(IS_PLAYING, isPlaying);

        if(selectedTextTrack != null){
            uniqueIds.add(selectedTextTrack);
        }
        if(selectedAudioTrack != null){
            uniqueIds.add(selectedTextTrack);
        }
        if(selectedVideoTrack != null){
            uniqueIds.add(selectedVideoTrack);
        }

        outState.putStringArrayList(UNIQUE_IDS, uniqueIds);
        outState.putString(TEXT_TRACK_LABEL, selectedTextTrackLabel);
        outState.putString(VIDEO_TRACK_LABEL, selectedVideoTrackLabel);
        outState.putString(AUDIO_TRACK_LABEL, selectedAudioTrackLabel);
        outState.putString(TEXT_TRACK, selectedTextTrack);
        outState.putString(VIDEO_TRACK, selectedVideoTrack);
        outState.putString(AUDIO_TRACK, selectedAudioTrack);
        outState.putBoolean(MARK_READ, markedAsComplete);
        outState.putSerializable(VIDEO_SPEED, getDisplayedSpeed(videoPlayer.getPlaybackRate()));
    }


    @Override
    public void onPause() {
        currentPosition = videoPlayer.getCurrentPosition();
        isPlaying = videoPlayer.getIsPlaying();
        videoPlayer.stop();
        super.onPause();
    }

    @Override
    public void onStop() {
        final int flags = View.VISIBLE;

        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
        super.onStop();
    }
}
