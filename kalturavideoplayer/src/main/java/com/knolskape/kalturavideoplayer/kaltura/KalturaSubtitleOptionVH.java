package com.knolskape.kalturavideoplayer.kaltura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaltura.playkit.player.TextTrack;
import com.knolskape.kalturavideoplayer.R;


public class KalturaSubtitleOptionVH extends RecyclerView.ViewHolder {
    TextView name;
    ImageView tick;
    View rootView;
    public KalturaSubtitleOptionVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.subtitle_option_name);
        tick = itemView.findViewById(R.id.subtitle_option_tick);
        rootView = itemView.findViewById(R.id.subtitle_option_root);
    }

    public static KalturaSubtitleOptionVH create(ViewGroup parent, int viewType){
        View rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kaltura_subtitle_option_vh, parent, false);
        return new KalturaSubtitleOptionVH(rootView);
    }



    public void bind(TextTrack option,
                     KalturaSubtitleSelectionFragment.KalturaSubtitleSelectionListener listener,
                     boolean isSelected){

        name.setText(option.getLabel());
        if(isSelected){
            name.setTextColor(Colors.primaryColor);
            tick.setVisibility(View.VISIBLE);
        }else{
            tick.setVisibility(View.INVISIBLE);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectSubtitle(option);
            }
        });
    }
}
