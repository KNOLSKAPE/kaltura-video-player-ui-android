package com.knolskape.kalturavideoplayer.kaltura;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KalturaOptionsAdapter extends RecyclerView.Adapter<KalturaOptionVH> {
    private List<KalturaOption> options;
    private KalturaOptionsFragment.KalturaOptionsSelectionListener listener;
    public KalturaOptionsAdapter(List<KalturaOption> options,
                                 KalturaOptionsFragment.KalturaOptionsSelectionListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KalturaOptionVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return KalturaOptionVH.create(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull KalturaOptionVH kalturaOptionVH, int i) {
        kalturaOptionVH.bind(options.get(i),listener);
    }

    @Override
    public int getItemCount() {
        return (options != null)?options.size():0;
    }

}
