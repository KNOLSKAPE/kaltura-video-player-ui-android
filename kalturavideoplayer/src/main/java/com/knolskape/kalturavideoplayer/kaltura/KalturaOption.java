package com.knolskape.kalturavideoplayer.kaltura;

public class KalturaOption {
    String name;
    String selectionName;

    public KalturaOption(String name, String selectionName) {
        this.name = name;
        this.selectionName = selectionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }
}
