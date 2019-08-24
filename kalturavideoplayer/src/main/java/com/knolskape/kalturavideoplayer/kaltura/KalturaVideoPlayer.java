package com.knolskape.kalturavideoplayer.kaltura;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.SubtitleStyleSettings;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.knolskape.kalturavideoplayer.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class KalturaVideoPlayer extends ConstraintLayout {

    private ImageView playPauseBtn;
    private TextView timeLapsed;
    private TextView timeLeft;
    private FrameLayout videoView;
    private String entryId;
    private SeekBar seekBar;
    private ImageView thumbnailImage;
    private ImageView thumbnailPlayBtn;
    private ConstraintLayout videoOverlayControls;
    private boolean isTimerTaskActive = false;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    private long currentPosition = -1;
    private long currentBufferedPosition = -1;
    private Player player;
    private PKMediaConfig mediaConfig;
    private boolean isThumbnailDismissed = false;
    private float playbackRate = 1.0f;
    private ImageView forwardBtn;
    private ConstraintLayout controls;
    private boolean isControlsVisible = true;
    private long controlsVisibiltyTimestamp = -1;
    private ImageView backward10Btn;
    private ImageView forward10Btn;
    private boolean restorePreviousState = false;
    private boolean restoreIsPlaying = false;
    private long restoreSeekPos = -1;
    private OnPlayerStateChangeListener stateChangeListener;
    private List<VideoTrack> videoTracks;
    private List<TextTrack> textTracks;
    private List<AudioTrack> audioTracks;
    private int videoTrackBitrate = 0;
    private ArrayList<String> previousSelections;
    private Float previousSpeed = null;
    private Long startPosition = 0L; // position tp start playback in seconds.
    private String sourceURLFormat = "https://cdnapisec.kaltura.com/p/%s/sp/%s/playManifest/entryId/%s/protocol/https/format/mpegdash/a.mp4";
    private String thumbnailUrlFormat = "https://cdnapisec.kaltura.com/p/%s/thumbnail/entry_id/%s/width/%d/height/%d";
    private String mediaSourceId = "1234";
    private String partnerId = null;
    private String subPartnerId = null;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    private String sourceUrl;
    private int thumbnailHeight = 768;
    private int thumbnailWidth = 1024;

    // Time format for time left and time lapsed


    PeriodFormatter hhmm = new PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .toFormatter();


    final Runnable myRunnable = new Runnable() {
        public void run() {
            seekBar.setProgress((int) currentPosition);
            seekBar.setSecondaryProgress((int) currentBufferedPosition);

            long currentPosDuration = player.getCurrentPosition();

            // update time lapsed and time left

            if(currentPosDuration > 0){
                Duration durTimeLapsed = new Duration(currentPosDuration);
                timeLapsed.setText(hhmm.print(durTimeLapsed.toPeriod()));

                Duration durTimeLeft = new Duration(player.getDuration()
                        - player.getCurrentPosition());
                long timeLeftInSeconds = durTimeLeft.getStandardSeconds();
                if(-2147483648L <= timeLeftInSeconds && timeLeftInSeconds <= 2147483647L){
                    timeLeft.setText(String.format(Locale.getDefault(),
                            getResources().getString(R.string.time_left_format),
                            hhmm.print(durTimeLeft.toPeriod())));
                }

            }

            // put the player in replay mode if the video has reached the end

            if(currentPosition == 100){
                stopTimer();
                playPauseBtn
                        .setImageDrawable(
                                getContext()
                                        .getDrawable(R.drawable.video_replay));
                showControls();
                player.setPlaybackRate(1.0f);
                backward10Btn.setVisibility(View.GONE);
                forward10Btn.setVisibility(View.GONE);
                player.pause();
                if(stateChangeListener != null){
                    stateChangeListener.onPause();
                }
            }

            // automatically close the video controls after 3s
            // unless it is paused (or in replay mode)

            if(currentPosition != 100){
                if(isControlsVisible){

                    if(controlsVisibiltyTimestamp == -1 ||
                            (System.currentTimeMillis() - controlsVisibiltyTimestamp >= 5000)){
                        hideControls();
                        isControlsVisible = false;
                    }
                }
            }
        }
    };


    public void setStartPosition(Long startPosition) {
        this.startPosition = startPosition;
    }

    public void setMediaSourceId(String mediaSourceId) {
        this.mediaSourceId = mediaSourceId;
    }

    public void setSubPartnerId(String subPartnerId) {
        this.subPartnerId = subPartnerId;
    }

    public void setPartnerId(String clientId) {
        this.partnerId = clientId;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }




    public KalturaVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public KalturaVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        View.inflate(context, R.layout.kaltura_video_player, this);
        playPauseBtn = findViewById(R.id.play_pause_btn);
        timeLapsed = findViewById(R.id.time_lapsed);
        timeLeft = findViewById(R.id.time_left);
        videoView = findViewById(R.id.video_view);
        seekBar = findViewById(R.id.seekbar);
        thumbnailImage = findViewById(R.id.thmubnail_view);
        thumbnailPlayBtn = findViewById(R.id.thumbnail_play_btn);
        forwardBtn = findViewById(R.id.forward_btn);
        forward10Btn = findViewById(R.id.forward_10);
        backward10Btn = findViewById(R.id.rewind_10);
        controls = findViewById(R.id.controls);
        videoOverlayControls = findViewById(R.id.video_overlay_controls);
    }

    public void configurePlayer(String entryId,
                                String partnerId,
                                String subPartnerId,
                                String sourceUrl,
                                int thumbnailHeight,
                                int thumbnailWidth){
        setPartnerId(partnerId);
        setSubPartnerId(subPartnerId);
        setThumbnailHeight(thumbnailHeight);
        setThumbnailWidth(thumbnailWidth);
        setSourceUrl(sourceUrl);
        player = PlayKitManager.loadPlayer(getContext(), new PKPluginConfigs());
        this.entryId = entryId;


        OkHttp3Downloader downloader = new OkHttp3Downloader(getContext());
        Picasso picasso = new Picasso.Builder(getContext()).downloader(downloader).build();

        picasso.setLoggingEnabled(true);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

        String thumbailUrl = getThumbnailUrl(entryId, metrics.widthPixels, metrics.heightPixels);

        picasso.load(thumbailUrl).networkPolicy(NetworkPolicy.OFFLINE).into(thumbnailImage, new Callback() {
            @Override
            public void onSuccess() {
                //Offline Cache hit
            }

            @Override
            public void onError(Exception e) {
                picasso.load(thumbailUrl).into(thumbnailImage);
            }
        });

        createMediaConfig();
        player = PlayKitManager.loadPlayer(getContext(), null);

        videoView.addView(player.getView());

        addPlayPauseButton();

        player.prepare(mediaConfig);

        seekBar.getProgressDrawable().setColorFilter(Colors.primaryColor,
                PorterDuff.Mode.SRC_ATOP);

        seekBar.getThumb().setColorFilter(Colors.primaryColor,
                PorterDuff.Mode.SRC_ATOP);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long playerSeekTo = (long) (player.getDuration()*((double)progress/100));
                if(stateChangeListener != null){
                    stateChangeListener.onSeek(playerSeekTo, progress, fromUser);
                }
                if(fromUser){
                    if(player.isPlaying()){
                        player.seekTo(playerSeekTo);
                    }

                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        thumbnailPlayBtn.setOnClickListener(v -> dismissThumbnailAndStartPlaying());

        forwardBtn.setOnClickListener(v -> {
            playbackRate += 0.5f;
            if(playbackRate >= 2.5f){
                playbackRate = 1.0f;
            }

            player.setPlaybackRate(playbackRate);
            Toast.makeText(getContext(), player.getPlaybackRate() +"x", Toast.LENGTH_SHORT).show();
        });

        videoView.setOnClickListener(v -> toggleControlsVisibility());

        controls.setOnClickListener(v -> controlsVisibiltyTimestamp = System.currentTimeMillis());

        backward10Btn.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));

        forward10Btn.setOnClickListener(v -> player.seekTo(player.getCurrentPosition()+10000));

        player.addEventListener(event -> {

            //Cast event to PlayerEvent.TracksAvailable
            PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;

            //Get the tracks data object.
            videoTracks = tracksAvailable.tracksInfo.getVideoTracks();
            textTracks = tracksAvailable.tracksInfo.getTextTracks();
            audioTracks = tracksAvailable.tracksInfo.getAudioTracks();
            if(previousSelections != null){
                for(String uniqueId: previousSelections){
                    player.changeTrack(uniqueId);
                }
                previousSelections = null;
            }
            if(previousSpeed != null){
                player.setPlaybackRate(previousSpeed);
                previousSpeed = null;
            }

        }, PlayerEvent.Type.TRACKS_AVAILABLE);


        player.getSettings().setSubtitleStyle(new SubtitleStyleSettings("AD")
                .setTextColor(Colors.white)
                .setBackgroundColor(Colors.black)
                .setTextSizeFraction(SubtitleStyleSettings.SubtitleTextSizeFraction.SUBTITLE_FRACTION_100));
        player.getSettings().setCea608CaptionsEnabled(true);
        player.getView().showVideoSurface();
        player.getView().showVideoSubtitles();

        if(restorePreviousState){
            restoreStateInternal();
        }



    }

    public void dismissThumbnailAndStartPlaying(){
        if(!isThumbnailDismissed){
            dismissThumbnail();
            handlePlayPause();
        }
    }


    private void dismissThumbnail(){
        isThumbnailDismissed = true;
        thumbnailPlayBtn.setVisibility(GONE);
        thumbnailImage.setVisibility(GONE);
    }

    private void toggleControlsVisibility(){
        if(isControlsVisible){
            hideControls();
        }else{
           showControls();
            controlsVisibiltyTimestamp = System.currentTimeMillis();
        }
        isControlsVisible = !isControlsVisible;
    }

    public void restoreState(long pos, boolean isPlaying,
                             ArrayList<String> previousSelections,
                             float previousSpeed){
        if(pos != -1) {
            restorePreviousState = true;
            restoreIsPlaying = isPlaying;
            restoreSeekPos = pos;
        }
        this.previousSelections = previousSelections;
        this.previousSpeed = previousSpeed;

    }

    public void restoreStateInternal(){

        if(restoreSeekPos != -1) {
            dismissThumbnail();
            if(restoreIsPlaying){
                handlePlayPause();
            }
            player.seekTo(restoreSeekPos);
            restoreSeekPos = -1;
            restoreIsPlaying = false;
            restorePreviousState = false;
        }

    }

    public float getPlaybackRate(){
        return player != null ? player.getPlaybackRate(): 0f;
    }

    public void setPlaybackRate(float rate){
        player.setPlaybackRate(rate);
    }

    public List<VideoTrack> getVideoTracks(){
        return videoTracks;
    }

    public List<TextTrack> getTextTracks(){
        return textTracks;
    }

    public List<AudioTrack> getAudioTracks(){
        return audioTracks;
    }

    public void changeTrack(VideoTrack track){
        player.changeTrack(track.getUniqueId());
    }

    public void changeTrack(AudioTrack track){
        player.changeTrack(track.getUniqueId());
    }

    public void changeTrack(TextTrack track){
        player.changeTrack(track.getUniqueId());
    }

    private void createMediaConfig() {
        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(startPosition);

        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
    }



    private void addPlayPauseButton() {
        //Get reference to the play/pause button.
        //Add clickListener.
        playPauseBtn.setOnClickListener(v -> handlePlayPause());
        player.addEventListener(event -> {
        });
        player.addStateChangeListener(event -> {

        });
    }

    private void handlePlayPause(){
        backward10Btn.setVisibility(View.VISIBLE);
        forward10Btn.setVisibility(View.VISIBLE );
        if (player.isPlaying()) {
            playPauseBtn
                    .setImageDrawable(
                            getContext()
                                    .getDrawable(R.drawable.video_play));
            player.pause();
            if(stateChangeListener != null){
                stateChangeListener.onPause();
            }
            stopTimer();
            showControls();
        } else {
            playPauseBtn
                    .setImageDrawable(
                            getContext()
                                    .getDrawable(R.drawable.video_pause));
            if(currentPosition == 100){
                player.replay();
                if(stateChangeListener != null){
                    stateChangeListener.onReplay();
                }
            }else{
                player.play();
                if(stateChangeListener != null){
                    stateChangeListener.onPlay();
                }
            }
            startTimer();
        }
    }


    private PKMediaEntry createMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(entryId);

        //Set media entry type. It could be Live,Vod or Unknown.
        //In this sample we use Vod.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    private List<PKMediaSource> createMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(mediaSourceId);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(getUrl(entryId));

        //Set the format of the source. In our case it will be hls in case of mpd/wvm formats you have to to call mediaSource.setDrmData method as well
        mediaSource.setMediaFormat(PKMediaFormat.dash);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

    private String getUrl(String id){
        if(sourceUrl != null){
            return sourceUrl;
        }else{
            return String.format(Locale.getDefault(), sourceURLFormat, partnerId, subPartnerId, id);
        }
    }

    public String getThumbnailUrl(String id, int width, int height){
        if(width <= 0){
            width = 400;
        }

        if(height <= 0){
            height = 200;
        }
        return String.format(Locale.getDefault(), thumbnailUrlFormat, partnerId, id, width, height);
    }

    private void startTimer(){
        isTimerTaskActive = true;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                updateUI();
            }
        };
        timer.schedule(timerTask, 0, 50);
    }

    private void updateUI(){
        double durationPercentage = ((double)player.getCurrentPosition()/player.getDuration())*100.0;
        double durationBufferedPercentage = ((double)player.getBufferedPosition()/player.getDuration())*100.0;
        if(durationPercentage < 0 || durationBufferedPercentage < 0){
            return;
        }
        currentPosition = (int) durationPercentage;
        // mark video completed if the duration left is less than 30s or 90% video is over
        long diff = player.getDuration() - player.getCurrentPosition();
        if(diff < 30000 || durationPercentage > 90){
            stateChangeListener.onEnd();
        }
        currentBufferedPosition = (int) durationBufferedPercentage;
        handler.post(myRunnable);
    }

    private void hideControls(){
        controls.animate().alpha(0.0f).setDuration(500);
        videoOverlayControls.animate().alpha(0.0f).setDuration(500);
        if(stateChangeListener != null){
            stateChangeListener.onControlsHide();
        }
    }

    private void showControls(){
        controls.animate().alpha(1.0f).setDuration(500);
        videoOverlayControls.animate().alpha(1.0f).setDuration(500);
        if(stateChangeListener != null){
            stateChangeListener.onControlsShow();
        }
    }

    private void stopTimer(){
        isTimerTaskActive = false;
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    public void destroy(){
        if(player != null){
            player.destroy();
        }
    }

    public void stop(){
        if(player != null){
            player.stop();
        }
    }

    public long getCurrentPosition(){
        return player != null ? player.getCurrentPosition():-1;
    }

    public boolean getIsPlaying(){
        return player.isPlaying();
    }

    public boolean isTimerTaskActive() {
        return isTimerTaskActive;
    }

    public void addStateChangeListener(OnPlayerStateChangeListener listener){
        stateChangeListener = listener;
    }

    public void destroyPlayer(){
        if(player != null){
            player.destroy();
        }
        stopTimer();
    }

    public interface OnPlayerStateChangeListener{
        void onPause();
        void onPlay();
        void onReplay();
        void onSeek(long seekPos, int completionPercentage, boolean fromUser);
        void onControlsShow();
        void onControlsHide();
        void onEnd();
    }

    public static enum KalturaOptions{
        SPEED("Speed"),
        SUBTITLES("Subtitles"),
        QUALITY("Quality"),
        AUDIO("Audio");

        private String displayName;

        public String getDisplayName()
        {
            return this.displayName;
        }
        private KalturaOptions(String displayName) {
            this.displayName = displayName;
        }
    }

    public static enum KalturaVideoSpeed{
        SLOWEST(0.5f, "0.5x"),
        SLOW(0.75f, "0.75x"),
        NORMAL(1f, "1.0x"),
        FAST(1.25f,"1.25x"),
        FASTER(1.5f, "1.5x"),
        FASTEST(2.0f, "2.0x");

        private float speed;

        public float getSpeed() {
            return speed;
        }

        public String getDisplayName() {
            return displayName;
        }

        private String displayName;



        private KalturaVideoSpeed(float speed, String displayName){
            this.speed = speed;
            this.displayName = displayName;
        }
    }
    public static class KalturaPlayerConfig{
        String partnerId;
        String subPartnerId;
        boolean autoplay;
        String mediaSourceId;
        long startPosition;
        String entryId;
        String sourceUrl;
        int thumbnailHeight;
        int thumbnailWidth;

        public KalturaPlayerConfig(String partnerId, String subPartnerId, boolean autoplay, String mediaSourceId, long startPosition, String entryId, String sourceUrl, int thumbnailHeight, int thumbnailWidth) {
            this.partnerId = partnerId;
            this.subPartnerId = subPartnerId;
            this.autoplay = autoplay;
            this.mediaSourceId = mediaSourceId;
            this.startPosition = startPosition;
            this.entryId = entryId;
            this.sourceUrl = sourceUrl;
            this.thumbnailHeight = thumbnailHeight;
            this.thumbnailWidth = thumbnailWidth;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public String getSubPartnerId() {
            return subPartnerId;
        }

        public boolean isAutoplay() {
            return autoplay;
        }

        public String getMediaSourceId() {
            return mediaSourceId;
        }

        public long getStartPosition() {
            return startPosition;
        }

        public String getEntryId() {
            return entryId;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public int getThumbnailHeight() {
            return thumbnailHeight;
        }

        public int getThumbnailWidth() {
            return thumbnailWidth;
        }
    }
}
