package com.example.boozzapp.utils.gallery_custom.gallery;

import java.util.Vector;

public class PhoneAlbum {
    private String name;
    private String coverUri;
    private Vector<PhonePhoto> albumPhotos;

    public PhoneAlbum() {
    }

    public PhoneAlbum(String name, String coverUri, Vector<PhonePhoto> albumPhotos) {
        this.name = name;
        this.coverUri = coverUri;
        this.albumPhotos = albumPhotos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public Vector<PhonePhoto> getAlbumPhotos() {
        if (albumPhotos == null)
            albumPhotos = new Vector<>();
        return albumPhotos;
    }
}
