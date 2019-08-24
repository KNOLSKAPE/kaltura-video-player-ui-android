package com.knolskape.kalturavideoplayer.kaltura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.AudioTrack;
import com.knolskape.kalturavideoplayer.R;


public class KalturaAudioOptionVH extends RecyclerView.ViewHolder {
    TextView name;
    ImageView tick;
    View rootView;
    public KalturaAudioOptionVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.audio_option_name);
        tick = itemView.findViewById(R.id.audio_option_tick);
        rootView = itemView.findViewById(R.id.audio_option_root);
    }

    public static KalturaAudioOptionVH create(ViewGroup parent, int viewType){
        View rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kaltura_audio_option_vh, parent, false);
        return new KalturaAudioOptionVH(rootView);
    }



    public void bind(AudioTrack option,
                     KalturaAudioSelectionFragment.KalturaAudioSelectionListener listener,
                     boolean isSelected){
        String trackName = option.getLabel();
        name.setText(trackName!=null?trackName:"Default");
        if(isSelected){
            name.setTextColor(Colors.primaryColor);
            tick.setVisibility(View.VISIBLE);
        }else{
            tick.setVisibility(View.INVISIBLE);
        }
        rootView.setOnClickListener(v -> listener.onSelectAudio(option));
    }
}
