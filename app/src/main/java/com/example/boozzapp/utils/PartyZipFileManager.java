package com.example.boozzapp.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PartyZipFileManager {
    public static void  unzip(String zipFileUrl, String fileLocation) {
        try {
            File f = new File(fileLocation);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFileUrl))) {
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
//                    Log.e("UnZipFILE", "Unzipping....");
                    String path = fileLocation + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        try (FileOutputStream fout = new FileOutputStream(path, false)) {
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = zin.read(buffer)) != -1) {
                                fout.write(buffer, 0, read);
                            }
                            zin.closeEntry();
                        }
                    }
                }
            }
        } catch (Exception e) {

            Log.e("UnZipException", Log.getStackTraceString(e));
        }
    }
}
