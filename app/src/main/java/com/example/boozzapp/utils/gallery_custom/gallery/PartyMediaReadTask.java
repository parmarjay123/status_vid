/*
 * Copyright 2017 Yan Zhenjie.
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
package com.example.boozzapp.utils.gallery_custom.gallery;

import android.content.Context;
import android.os.AsyncTask;

import com.example.boozzapp.utils.gallery_custom.PartyAlbumFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Image scan task.</p>
 */
public class PartyMediaReadTask extends AsyncTask<Void, Void, PartyMediaReadTask.ResultWrapper> {

    public interface Callback {
        /**
         * Callback the results.
         *
         * @param partyAlbumFolders album folder list.
         */
        void onScanCallback(ArrayList<PartyAlbumFolder> partyAlbumFolders, ArrayList<PartyAlbumFile> checkedFiles);

        void onScanCallbackWhatsApp(ArrayList<PartyAlbumFile> whatsAppStatusFiles);
    }

    static class ResultWrapper {
        private ArrayList<PartyAlbumFolder> mPartyAlbumFolders;
        private ArrayList<PartyAlbumFile> mPartyAlbumFiles;
        private ArrayList<PartyAlbumFile> mWhatsAppStatusFiles;
    }

    private int mFunction;
    private List<PartyAlbumFile> mCheckedFiles;
    private PartyMediaReader mMediaReader;
    private Callback mCallback;
    private Context mContext;

    public PartyMediaReadTask(Context mContext, int function, List<PartyAlbumFile> checkedFiles, PartyMediaReader mediaReader, Callback callback) {
        this.mContext = mContext;
        this.mFunction = function;
        this.mCheckedFiles = checkedFiles;
        this.mMediaReader = mediaReader;
        this.mCallback = callback;
    }

    @Override
    protected ResultWrapper doInBackground(Void... params) {
        ArrayList<PartyAlbumFolder> partyAlbumFolders;
        ArrayList<PartyAlbumFile> mWhatsAppStatusFiles;
        ResultWrapper wrapper = new ResultWrapper();
        ArrayList<PartyAlbumFile> checkedFiles = new ArrayList<>();

        switch (mFunction) {
            case 0:
                partyAlbumFolders = mMediaReader.getAllImage();

                if (mCheckedFiles != null && !mCheckedFiles.isEmpty()) {
                    List<PartyAlbumFile> partyAlbumFiles = partyAlbumFolders.get(0).getAlbumFiles();
                    for (PartyAlbumFile checkPartyAlbumFile : mCheckedFiles) {
                        for (int i = 0; i < partyAlbumFiles.size(); i++) {
                            PartyAlbumFile partyAlbumFile = partyAlbumFiles.get(i);
                            if (checkPartyAlbumFile.equals(partyAlbumFile)) {
                                partyAlbumFile.setChecked(true);
                                checkedFiles.add(partyAlbumFile);
                            }
                        }
                    }
                }
                wrapper.mPartyAlbumFolders = partyAlbumFolders;
                wrapper.mPartyAlbumFiles = checkedFiles;
                break;
            case 1:
                mWhatsAppStatusFiles = mMediaReader.getstatuses(mContext);
                wrapper.mWhatsAppStatusFiles = mWhatsAppStatusFiles;
                break;
        }

        return wrapper;
    }

    @Override
    protected void onPostExecute(ResultWrapper wrapper) {
        switch (mFunction) {
            case 0:
                mCallback.onScanCallback(wrapper.mPartyAlbumFolders, wrapper.mPartyAlbumFiles);
                break;
            case 1:
                mCallback.onScanCallbackWhatsApp(wrapper.mWhatsAppStatusFiles);
                break;
        }

    }
}