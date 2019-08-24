package com.knolskape.kalturavideoplayer.kaltura;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.AudioTrack;

import java.util.List;

public class KalturaAudioAdapter extends RecyclerView.Adapter<KalturaAudioOptionVH> {
    List<AudioTrack> tracks;
    KalturaAudioSelectionFragment.KalturaAudioSelectionListener listener;
    String selectedTrack;

    public KalturaAudioAdapter(List<AudioTrack> tracks,
                               KalturaAudioSelectionFragment.KalturaAudioSelectionListener listener,
                               String track){
        this.tracks = tracks;
        this.listener = listener;
        this.selectedTrack = track;
    }

    @NonNull
    @Override
    public KalturaAudioOptionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return KalturaAudioOptionVH.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull KalturaAudioOptionVH vh, int i) {
        boolean isSelected = false;
        if(selectedTrack == null && tracks.get(i).isAdaptive()){
            isSelected = true;
        }else if(selectedTrack != null && (tracks.get(i).getUniqueId().equals(selectedTrack))){
            isSelected = true;
        }
        vh.bind(tracks.get(i), listener, isSelected);
    }

    @Override
    public int getItemCount() {
        return tracks != null?tracks.size():0;
    }
}
