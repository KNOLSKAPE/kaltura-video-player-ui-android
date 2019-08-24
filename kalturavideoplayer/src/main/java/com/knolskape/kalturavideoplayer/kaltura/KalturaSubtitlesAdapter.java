package com.knolskape.kalturavideoplayer.kaltura;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.TextTrack;

import java.util.List;

public class KalturaSubtitlesAdapter extends RecyclerView.Adapter<KalturaSubtitleOptionVH> {
    List<TextTrack> tracks;
    KalturaSubtitleSelectionFragment.KalturaSubtitleSelectionListener listener;
    String selectedTrack;
    public KalturaSubtitlesAdapter(List<TextTrack> tracks, KalturaSubtitleSelectionFragment.KalturaSubtitleSelectionListener listener, String selectedTrack){
        this.tracks = tracks;
        this.listener = listener;
        this.selectedTrack = selectedTrack;
    }

    @NonNull
    @Override
    public KalturaSubtitleOptionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return KalturaSubtitleOptionVH.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull KalturaSubtitleOptionVH kalturaSubtitleOptionVH, int i) {
        boolean isSelected = false;
        if(selectedTrack == null && tracks.get(i).isAdaptive()){
            isSelected = true;
        }else if(selectedTrack != null && tracks.get(i).getUniqueId().equals(selectedTrack)){
            isSelected = true;
        }
        kalturaSubtitleOptionVH.bind(tracks.get(i), listener, isSelected);
    }

    @Override
    public int getItemCount() {
        return tracks!=null?tracks.size():0;
    }
}
