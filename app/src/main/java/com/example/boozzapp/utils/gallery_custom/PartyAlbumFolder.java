/*
 * Copyright 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.boozzapp.utils.gallery_custom;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.boozzapp.utils.gallery_custom.gallery.PartyAlbumFile;

import java.util.ArrayList;

/**
 * <p>Album folder, contains selected status and pictures.</p>
 * Created by Yan Zhenjie on 2016/10/14.
 */
public class PartyAlbumFolder implements Parcelable {

    /**
     * Folder name.
     */
    private String name;
    /**
     * Image list in folder.
     */
    private ArrayList<PartyAlbumFile> mPartyAlbumFiles = new ArrayList<>();
    /**
     * checked.
     */
    private boolean isChecked;

    public PartyAlbumFolder() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PartyAlbumFile> getAlbumFiles() {
        return mPartyAlbumFiles;
    }

    public void addAlbumFile(PartyAlbumFile partyAlbumFile) {
        mPartyAlbumFiles.add(partyAlbumFile);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    protected PartyAlbumFolder(Parcel in) {
        name = in.readString();
        mPartyAlbumFiles = in.createTypedArrayList(PartyAlbumFile.CREATOR);
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(mPartyAlbumFiles);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PartyAlbumFolder> CREATOR = new Creator<PartyAlbumFolder>() {
        @Override
        public PartyAlbumFolder createFromParcel(Parcel in) {
            return new PartyAlbumFolder(in);
        }

        @Override
        public PartyAlbumFolder[] newArray(int size) {
            return new PartyAlbumFolder[size];
        }
    };
}