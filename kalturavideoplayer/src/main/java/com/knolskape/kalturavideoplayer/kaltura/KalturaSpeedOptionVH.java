package com.knolskape.kalturavideoplayer.kaltura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.knolskape.kalturavideoplayer.R;


public class KalturaSpeedOptionVH extends RecyclerView.ViewHolder {
    TextView name;
    ImageView tick;
    View rootView;

    public KalturaSpeedOptionVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.speed_option_name);
        tick = itemView.findViewById(R.id.speed_option_tick);
        rootView = itemView.findViewById(R.id.speed_option_root);
    }
    public static KalturaSpeedOptionVH create(ViewGroup parent, int viewType){
        View rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kaltura_speed_option_vh, parent, false);
        return new KalturaSpeedOptionVH(rootView);
    }

    public void bind(KalturaVideoPlayer.KalturaVideoSpeed option,
                     KalturaSpeedSelectionFragment.KalturaSpeedSelectionListener listener,
                     boolean isActive){
        name.setText(option.getDisplayName());
        if(isActive){
            name.setTextColor(Colors.primaryColor);
            tick.setVisibility(View.VISIBLE);
        }else{
            tick.setVisibility(View.INVISIBLE);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectSpeed(option.getSpeed());
            }
        });
    }
}
