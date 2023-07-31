package com.example.boozzapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import com.example.boozzapp.BuildConfig
import java.io.*


object FilePathUtil {
    fun getImagePathFromInputStreamUri(activity: Activity, uri: Uri): File? {
        var inputStream: InputStream? = null
        var f: File? = null
        //        uri = compressImage(activity,uri.toString()).;
        if (uri.authority != null) {
            try {
                inputStream = activity.contentResolver.openInputStream(uri) // context xneeded
                f = createTemporalFileFrom(activity, inputStream)
            } catch (e: FileNotFoundException) {
                // log
            } catch (e: IOException) {
                // log
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return f
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(activity: Activity, inputStream: InputStream?): File? {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = File(activity.externalCacheDir, "tempFile" + System.currentTimeMillis() + ".jpg") // context needed
            val outputStream: OutputStream = FileOutputStream(targetFile)
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return targetFile
    }

    fun getRealPath(context: Context, fileUri: Uri): String? {
        val realPath: String?
        // SDK < API11
        realPath = if (Build.VERSION.SDK_INT < 11) {
            getRealPathFromURI_BelowAPI11(context, fileUri)
        } else if (Build.VERSION.SDK_INT < 19) {
            getRealPathFromURI_API11to18(context, fileUri)
        } else {
            getRealPathFromURI_API19(context, fileUri)
        }
        return realPath
    }

    @SuppressLint("NewApi")
    private fun getRealPathFromURI_API11to18(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor = cursorLoader.loadInBackground()
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
        }
        return result
    }

    private fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        var column_index = 0
        var result = ""
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
            return result
        }
        return result
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    private fun getRealPathFromURI_API19(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)

                if (!TextUtils.isEmpty(id)) {
                    return if (id.startsWith("raw:")) {
                        id.replaceFirst("raw:", "")
                    } else try {
                        val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                        getDataColumn(context, contentUri, null, null)
                    } catch (e: NumberFormatException) {
                        null
                    }
                }

            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return uri.path
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    fun getRealPathFromURI(activity: Activity, contentURI: String?): String {
        val contentUri = Uri.parse(contentURI)
        val cursor = activity.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path!!
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }


    fun copyUriToExternalFilesDir(activity: Activity, uri: Uri, fileName: String): File? {
//        thread {
            val inputStream = activity.contentResolver.openInputStream(uri)
            val tempDir = activity.getExternalFilesDir("temp")
            if (inputStream != null && tempDir != null) {
                val file = File("$tempDir/$fileName")
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val bos = BufferedOutputStream(fos)
                val byteArray = ByteArray(1024)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                fos.close()
                Log.e("in",""+file)
                return file

            }else{
                return null
            }
//        }
    }

    fun getFileNameByUri(activity: Activity, uri: Uri): String {
        var fileName = System.currentTimeMillis().toString()
        val cursor = activity.contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            cursor.close()
        }
        return fileName
    }

    fun getBitmapToCacheFileUri(mContext: Context, bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val file = File(mContext.externalCacheDir, "share_image_" + System.currentTimeMillis() + ".png")

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file)
                bmpUri = uri
            } else {
                bmpUri = Uri.fromFile(file)
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}