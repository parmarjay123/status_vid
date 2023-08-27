package com.example.boozzapp.utils.gallery_custom.gallery;

import org.json.JSONArray;

public class PartyModelCommandImages {
    private String imgName;
    private int imgHeight;
    private int imgWidth;
    private String imgPath;
    private String imgPathExtra = null;
    private JSONArray prefix;
    private JSONArray postfix;
    private boolean changesOccurs = false;

    public PartyModelCommandImages(String imgName, int imgWidth,
                                   int imgHeight, String imgPath,
                                   JSONArray prefix, JSONArray postfix) {
        this.imgName = imgName;
        this.imgHeight = imgHeight;
        this.imgWidth = imgWidth;
        this.imgPath = imgPath;
        this.prefix = prefix;
        this.postfix = postfix;
    }

    public PartyModelCommandImages(String imgName, JSONArray prefix) {
        this.imgName = imgName;
        this.prefix = prefix;
    }

    public String getImgName() {
        return imgName;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public String getImgPath() {
        return imgPath;
    }

    public JSONArray getPrefix() {
        return prefix;
    }

    public JSONArray getPostfix() {
        return postfix;
    }

    public String getImgPathExtra() {
        return imgPathExtra;
    }

    public void setImgPathExtra(String imgPathExtra) {
        this.imgPathExtra = imgPathExtra;
    }

    public boolean isChangesOccurs() {
        return changesOccurs;
    }

    public void setChangesOccurs(boolean changesOccurs) {
        this.changesOccurs = changesOccurs;
    }
}
