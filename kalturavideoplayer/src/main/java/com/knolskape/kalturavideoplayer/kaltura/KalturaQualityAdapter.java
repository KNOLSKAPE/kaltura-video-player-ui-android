package com.knolskape.kalturavideoplayer.kaltura;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.VideoTrack;

import java.util.List;

public class KalturaQualityAdapter extends RecyclerView.Adapter<KalturaQualityOptionVH> {

    List<VideoTrack> tracks;
    KalturaQualitySelectionFragment.KalturaQualitySelectionListener listener;
    String selectedTrack;

    public KalturaQualityAdapter(List<VideoTrack> tracks,
                                 KalturaQualitySelectionFragment.KalturaQualitySelectionListener listener,
                                 String track){
        this.tracks = tracks;
        this.listener = listener;
        this.selectedTrack = track;
    }

    @NonNull
    @Override
    public KalturaQualityOptionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return KalturaQualityOptionVH.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull KalturaQualityOptionVH kalturaQualityOptionVH, int i) {
        boolean isSelected = false;
        if(selectedTrack == null && tracks.get(i).isAdaptive()){
            isSelected = true;
        }else if(selectedTrack != null && (tracks.get(i).getUniqueId().equals(selectedTrack))){
            isSelected = true;
        }
        kalturaQualityOptionVH.bind(tracks.get(i), listener, isSelected);
    }

    @Override
    public int getItemCount() {
        return tracks != null?tracks.size():0;
    }


}
