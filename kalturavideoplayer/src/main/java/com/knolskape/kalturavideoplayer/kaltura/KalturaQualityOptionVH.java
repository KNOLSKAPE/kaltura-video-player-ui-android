package com.knolskape.kalturavideoplayer.kaltura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.VideoTrack;
import com.knolskape.kalturavideoplayer.R;


public class KalturaQualityOptionVH extends RecyclerView.ViewHolder {
    TextView name;
    ImageView tick;
    View rootView;

    public KalturaQualityOptionVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.quality_option_name);
        tick = itemView.findViewById(R.id.quality_option_tick);
        rootView = itemView.findViewById(R.id.quality_option_root);
    }
    public static KalturaQualityOptionVH create(ViewGroup parent, int viewType){
        View rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kaltura_quality_option_vh, parent, false);
        return new KalturaQualityOptionVH(rootView);
    }



    public void bind(VideoTrack option,
                     KalturaQualitySelectionFragment.KalturaQualitySelectionListener listener,
                     boolean isSelected){
        String displayName;
        String displayValue;
        if(option.isAdaptive()){
            displayName = "Auto";
            displayValue = "Auto";
        }else{
            displayName = Integer.toString(option.getHeight());
            displayValue = Integer.toString(option.getHeight());
        }
        name.setText(displayName);

        if(isSelected){
            name.setTextColor(Colors.primaryColor);
            tick.setVisibility(View.VISIBLE);
        }else{
            tick.setVisibility(View.INVISIBLE);
        }
        rootView.setOnClickListener(v -> listener.onSelectQuality(option));
    }
}
