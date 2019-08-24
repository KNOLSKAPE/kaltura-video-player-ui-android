package com.knolskape.kalturavideoplayer.kaltura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.knolskape.kalturavideoplayer.R;


public class KalturaOptionVH extends RecyclerView.ViewHolder {
    TextView name;
    TextView value;
    View rootView;

    public KalturaOptionVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.option_name);
        value = itemView.findViewById(R.id.option_value);
        rootView = itemView.findViewById(R.id.video_option_root);
    }
    public static KalturaOptionVH create(ViewGroup parent, int viewType){
        View rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kaltura_option_vh, parent, false);
        return new KalturaOptionVH(rootView);
    }

    public void bind(KalturaOption option, KalturaOptionsFragment.KalturaOptionsSelectionListener listener){
        name.setText(option.name);
        value.setText(option.selectionName);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(option);
            }
        });
    }
}
