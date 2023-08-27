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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.WorkerThread;

import com.example.boozzapp.utils.gallery_custom.PartyAlbumFolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YanZhenjie on 2017/8/15.
 */
public class PartyMediaReader {

    private Context mContext;

    private PartyFilter<Long> mSizeFilter;
    private PartyFilter<String> mMimeFilter;
    private PartyFilter<Long> mDurationFilter;
    private boolean mFilterVisibility;

    public PartyMediaReader(Context context, PartyFilter<Long> sizeFilter, PartyFilter<String> mimeFilter, PartyFilter<Long> durationFilter, boolean filterVisibility) {
        this.mContext = context;

        this.mSizeFilter = sizeFilter;
        this.mMimeFilter = mimeFilter;
        this.mDurationFilter = durationFilter;
        this.mFilterVisibility = filterVisibility;
    }

    /**
     * Image attribute.
     */
    private static final String[] IMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.SIZE
    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanImageFile(Map<String, PartyAlbumFolder> albumFolderMap, PartyAlbumFolder allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGES,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);

                PartyAlbumFile imageFile = new PartyAlbumFile();
                imageFile.setMediaType(PartyAlbumFile.TYPE_IMAGE);
                imageFile.setPath(path);
                imageFile.setBucketName(bucketName);
                imageFile.setMimeType(mimeType);
                imageFile.setAddDate(addDate);
                imageFile.setLatitude(latitude);
                imageFile.setLongitude(longitude);
                imageFile.setSize(size);

                if (mSizeFilter != null && mSizeFilter.filter(size)) {
                    if (!mFilterVisibility) continue;
                    imageFile.setDisable(true);
                }
                if (mMimeFilter != null && mMimeFilter.filter(mimeType)) {
                    if (!mFilterVisibility) continue;
                    imageFile.setDisable(true);
                }

                allFileFolder.addAlbumFile(imageFile);
                PartyAlbumFolder partyAlbumFolder = albumFolderMap.get(bucketName);

                if (partyAlbumFolder != null)
                    partyAlbumFolder.addAlbumFile(imageFile);
                else {
                    partyAlbumFolder = new PartyAlbumFolder();
                    partyAlbumFolder.setName(bucketName);
                    partyAlbumFolder.addAlbumFile(imageFile);

                    albumFolderMap.put(bucketName, partyAlbumFolder);
                }
            }
            cursor.close();
        }
    }

    /**
     * Video attribute.
     */
    private static final String[] VIDEOS = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.LATITUDE,
            MediaStore.Video.Media.LONGITUDE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
    };

    /**
     * Video attribute.
     */
    private static final String[] WHATSAPPSTATUSES = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.LATITUDE,
            MediaStore.Video.Media.LONGITUDE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanVideoFile(Map<String, PartyAlbumFolder> albumFolderMap, PartyAlbumFolder allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEOS,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);
                long duration = cursor.getLong(7);

                PartyAlbumFile videoFile = new PartyAlbumFile();
                videoFile.setMediaType(PartyAlbumFile.TYPE_VIDEO);
                videoFile.setPath(path);
                videoFile.setBucketName(bucketName);
                videoFile.setMimeType(mimeType);
                videoFile.setAddDate(addDate);
                videoFile.setLatitude(latitude);
                videoFile.setLongitude(longitude);
                videoFile.setSize(size);
                videoFile.setDuration(duration);

                if (mSizeFilter != null && mSizeFilter.filter(size)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }
                if (mMimeFilter != null && mMimeFilter.filter(mimeType)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }
                if (mDurationFilter != null && mDurationFilter.filter(duration)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }

                allFileFolder.addAlbumFile(videoFile);
                PartyAlbumFolder partyAlbumFolder = albumFolderMap.get(bucketName);

                if (partyAlbumFolder != null)
                    partyAlbumFolder.addAlbumFile(videoFile);
                else {
                    partyAlbumFolder = new PartyAlbumFolder();
                    partyAlbumFolder.setName(bucketName);
                    partyAlbumFolder.addAlbumFile(videoFile);

                    albumFolderMap.put(bucketName, partyAlbumFolder);
                }
            }
            cursor.close();
        }
    }

    /**
     * Scan the list of pictures in the library.
     */
    @WorkerThread
    public ArrayList<PartyAlbumFolder> getAllImage() {
        Map<String, PartyAlbumFolder> albumFolderMap = new HashMap<>();
        PartyAlbumFolder allFileFolder = new PartyAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName("All");

        scanImageFile(albumFolderMap, allFileFolder);

        ArrayList<PartyAlbumFolder> partyAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
        partyAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, PartyAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            PartyAlbumFolder partyAlbumFolder = folderEntry.getValue();
            Collections.sort(partyAlbumFolder.getAlbumFiles());
            partyAlbumFolders.add(partyAlbumFolder);
        }
        return partyAlbumFolders;
    }

    /**
     * Scan the list of videos in the library.
     */
    @WorkerThread
    public ArrayList<PartyAlbumFolder> getAllVideo() {
        Map<String, PartyAlbumFolder> albumFolderMap = new HashMap<>();
        PartyAlbumFolder allFileFolder = new PartyAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName("All Videos");

        scanVideoFile(albumFolderMap, allFileFolder);

        ArrayList<PartyAlbumFolder> partyAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
        partyAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, PartyAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            PartyAlbumFolder partyAlbumFolder = folderEntry.getValue();
            Collections.sort(partyAlbumFolder.getAlbumFiles());
            partyAlbumFolders.add(partyAlbumFolder);
        }
        return partyAlbumFolders;
    }

    /**
     * Get all the multimedia files, including videos and pictures.
     */
    @WorkerThread
    public ArrayList<PartyAlbumFolder> getAllMedia() {
        Map<String, PartyAlbumFolder> albumFolderMap = new HashMap<>();
        PartyAlbumFolder allFileFolder = new PartyAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName("All Images/Videos");

        scanImageFile(albumFolderMap, allFileFolder);
        scanVideoFile(albumFolderMap, allFileFolder);

        ArrayList<PartyAlbumFolder> partyAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
        partyAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, PartyAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            PartyAlbumFolder partyAlbumFolder = folderEntry.getValue();
            Collections.sort(partyAlbumFolder.getAlbumFiles());
            partyAlbumFolders.add(partyAlbumFolder);
        }
        return partyAlbumFolders;
    }

    @WorkerThread
    public ArrayList<PartyAlbumFile> getstatuses(Context mContext) {
        Map<String, PartyAlbumFolder> albumFolderMap = new HashMap<>();
        ArrayList<PartyAlbumFile> partyAlbumFiles = new ArrayList<>();
        //  AlbumFolder allFileFolder = new AlbumFolder();
        //  allFileFolder.setChecked(true);
        //   allFileFolder.setName("All Images/Videos");

        // scanImageFile(albumFolderMap, allFileFolder);
        // scanWhatsAppStatus(albumFolderMap, albumFiles);
        scanWhatsAppStatusImages(albumFolderMap, partyAlbumFiles);

        return partyAlbumFiles;
    }

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanWhatsAppStatus(Map<String, PartyAlbumFolder> albumFolderMap, ArrayList<PartyAlbumFile> allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();

        //  String[] selectionArgs = new String[] {"%/WhatsApp/Media/.Statuses%"};
        String[] selectionArgs = new String[]{"%Boo%"};

        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEOS,
                MediaStore.Video.Media.DATA + " like?",
                selectionArgs,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);
                long duration = cursor.getLong(7);

                PartyAlbumFile videoFile = new PartyAlbumFile();
                videoFile.setMediaType(PartyAlbumFile.TYPE_VIDEO);
                videoFile.setPath(path);
                videoFile.setBucketName(bucketName);
                videoFile.setMimeType(mimeType);
                videoFile.setAddDate(addDate);
                videoFile.setLatitude(latitude);
                videoFile.setLongitude(longitude);
                videoFile.setSize(size);
                videoFile.setDuration(duration);

                if (mSizeFilter != null && mSizeFilter.filter(size)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }
                if (mMimeFilter != null && mMimeFilter.filter(mimeType)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }
                if (mDurationFilter != null && mDurationFilter.filter(duration)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }

                allFileFolder.add(videoFile);
                PartyAlbumFolder partyAlbumFolder = albumFolderMap.get(bucketName);

                if (partyAlbumFolder != null)
                    partyAlbumFolder.addAlbumFile(videoFile);
                else {
                    partyAlbumFolder = new PartyAlbumFolder();
                    partyAlbumFolder.setName(bucketName);
                    partyAlbumFolder.addAlbumFile(videoFile);

                    albumFolderMap.put(bucketName, partyAlbumFolder);
                }
            }
            cursor.close();
        }
    }

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanWhatsAppStatusImages(Map<String, PartyAlbumFolder> albumFolderMap, ArrayList<PartyAlbumFile> allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] selectionArgs = new String[]{"%Boo/Booinsta%"};

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGES,
                MediaStore.Images.Media.DATA + " like?",
                selectionArgs,
                null);


        if (cursor != null) {
            while (cursor.moveToNext()) {

                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);
                // long duration = cursor.getLong(7);

                PartyAlbumFile videoFile = new PartyAlbumFile();
                videoFile.setMediaType(PartyAlbumFile.TYPE_VIDEO);
                videoFile.setPath(path);
                videoFile.setBucketName(bucketName);
                videoFile.setMimeType(mimeType);
                videoFile.setAddDate(addDate);
                videoFile.setLatitude(latitude);
                videoFile.setLongitude(longitude);
                videoFile.setSize(size);
                //videoFile.setDuration(duration);

             /*   if (mSizeFilter != null && mSizeFilter.filter(size)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }*/
               /* if (mMimeFilter != null && mMimeFilter.filter(mimeType)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }*/
               /* if (mDurationFilter != null && mDurationFilter.filter(duration)) {
                    if (!mFilterVisibility) continue;
                    videoFile.setDisable(true);
                }*/

                allFileFolder.add(videoFile);
                PartyAlbumFolder partyAlbumFolder = albumFolderMap.get("all");

                if (partyAlbumFolder != null)
                    partyAlbumFolder.addAlbumFile(videoFile);
                else {
                    partyAlbumFolder = new PartyAlbumFolder();
                    partyAlbumFolder.setName("all");
                    partyAlbumFolder.addAlbumFile(videoFile);

                    albumFolderMap.put("all", partyAlbumFolder);
                }
            }
            cursor.close();
        }
    }


}