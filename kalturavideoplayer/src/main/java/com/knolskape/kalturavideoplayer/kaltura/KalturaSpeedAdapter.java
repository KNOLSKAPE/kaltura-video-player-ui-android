package com.knolskape.kalturavideoplayer.kaltura;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KalturaSpeedAdapter extends RecyclerView.Adapter<KalturaSpeedOptionVH> {
    KalturaVideoPlayer.KalturaVideoSpeed[] availableSpeeds;
    KalturaSpeedSelectionFragment.KalturaSpeedSelectionListener listener;
    KalturaVideoPlayer.KalturaVideoSpeed selectedSpeed;
    public KalturaSpeedAdapter(KalturaSpeedSelectionFragment.KalturaSpeedSelectionListener listener,
                               KalturaVideoPlayer.KalturaVideoSpeed selectedSpeed){
        availableSpeeds = KalturaVideoPlayer.KalturaVideoSpeed.values();
        this.listener = listener;
        this.selectedSpeed = selectedSpeed;
    }
    @NonNull
    @Override
    public KalturaSpeedOptionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return KalturaSpeedOptionVH.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull KalturaSpeedOptionVH kalturaSpeedOptionVH, int i) {
        boolean isSelected = false;
        if(availableSpeeds[i].getSpeed() == selectedSpeed.getSpeed()){
            isSelected = true;
        }else{
            isSelected = false;
        }
        kalturaSpeedOptionVH.bind(availableSpeeds[i], listener, isSelected);
    }

    @Override
    public int getItemCount() {
        return KalturaVideoPlayer.KalturaVideoSpeed.values().length;
    }
}
