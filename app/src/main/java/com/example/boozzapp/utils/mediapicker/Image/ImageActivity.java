package com.example.boozzapp.utils.mediapicker.Image;

import static com.example.boozzapp.utils.mediapicker.Image.ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.boozzapp.R;
import com.example.boozzapp.utils.mediapicker.FileProcessing;
import com.example.boozzapp.utils.mediapicker.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alhazmy13 on 10/26/15.
 * MediaPicker
 */
public class ImageActivity extends AppCompatActivity {

    private File destination;
    private Uri mImageUri;
    private ImageConfig mImgConfig;
    private List<String> listOfImgs;
    private AlertDialog alertDialog;
    Context activity;

    public static Intent getCallingIntent(Context activity, ImageConfig imageConfig) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra(ImageTags.Tags.IMG_CONFIG, imageConfig);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ImageActivity.this;
        Intent intent = getIntent();
        if (intent != null) {
            mImgConfig = (ImageConfig) intent.getSerializableExtra(ImageTags.Tags.IMG_CONFIG);
        }

        if (savedInstanceState == null) {
            pickImageWrapper();
            listOfImgs = new ArrayList<>();
        }
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, mImgConfig.toString());
    }

    @Override
    protected void onStop() {
        if (alertDialog != null)
            alertDialog.dismiss();
        super.onStop();
    }

    private void pickImage() {
        Utility.createFolder(mImgConfig.directory);
        destination = new File(mImgConfig.directory, Utility.getRandomString() + mImgConfig.extension.getValue());
        switch (mImgConfig.mode) {
            case CAMERA:
                startActivityFromCamera();
                break;
            case GALLERY:
                if (mImgConfig.allowMultiple && mImgConfig.allowOnlineImages)
                    startActivityFromGalleryMultiImg();
                else
                    startActivityFromGallery();
                break;
            case CAMERA_AND_GALLERY:
                showDialog();
                break;
            default:
                break;
        }
    }


    private void showDialog() {

        Dialog holdDialog = new Dialog(activity);
        holdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        holdDialog.setContentView(R.layout.dialog_camera_gallery);

        // Set the background of the dialog window to transparent
        holdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Calculate the desired height of the dialog (e.g., half of the screen)
        int windowHeight = 2000;
        int dialogHeight = windowHeight / 2;

        // Set the dialog's window layout parameters
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
        holdDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        ImageView llRemoveWaterMark = holdDialog.findViewById(R.id.ivCloseDialog);
        ImageView ivCamera = holdDialog.findViewById(R.id.ivCamera);
        ImageView ivGallery = holdDialog.findViewById(R.id.ivGallery);


        holdDialog.show();

        llRemoveWaterMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holdDialog.dismiss();
                finish();
            }
        });


        ivCamera.setOnClickListener(view -> {
            startActivityFromCamera();
            holdDialog.dismiss();


        });

        ivGallery.setOnClickListener(view -> {
            startActivityFromGallery();
            holdDialog.dismiss();


        });


    }

    private void showFromCameraOrGalleryAlert() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.media_picker_select_from))
                .setPositiveButton(getString(R.string.media_picker_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mImgConfig.debug)
                            Log.d(ImageTags.Tags.TAG, "Alert Dialog - Start From Camera");
                        startActivityFromCamera();
                        alertDialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.media_picker_gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mImgConfig.debug)
                            Log.d(ImageTags.Tags.TAG, "Alert Dialog - Start From Gallery");
                        if (mImgConfig.allowMultiple)
                            startActivityFromGalleryMultiImg();
                        else
                            startActivityFromGallery();
                        alertDialog.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mImgConfig.debug)
                            Log.d(ImageTags.Tags.TAG, "Alert Dialog - Canceled");
                        alertDialog.dismiss();
                        finish();
                    }
                })
                .create();
        if (alertDialog != null)
            alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void startActivityFromGallery() {
        mImgConfig.isImgFromCamera = false;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, !mImgConfig.allowOnlineImages);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Gallery Start with Single Image mode");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startActivityFromGalleryMultiImg() {
        mImgConfig.isImgFromCamera = false;
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, !mImgConfig.allowOnlineImages);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Gallery Start with Multiple Images mode");
    }

    private void startActivityFromCamera() {
        mImgConfig.isImgFromCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", destination);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ImageTags.IntentCode.CAMERA_REQUEST);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "Camera Start");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null) {
            outState.putString(ImageTags.Tags.CAMERA_IMAGE_URI, mImageUri.toString());
            outState.putSerializable(ImageTags.Tags.IMG_CONFIG, mImgConfig);
        }
        outState.putBoolean(ImageTags.Tags.IS_ALERT_SHOWING, (alertDialog == null ? false : alertDialog.isShowing()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(ImageTags.Tags.CAMERA_IMAGE_URI)) {
            mImageUri = Uri.parse(savedInstanceState.getString(ImageTags.Tags.CAMERA_IMAGE_URI));
            destination = new File(mImageUri.getPath());
            mImgConfig = (ImageConfig) savedInstanceState.getSerializable(ImageTags.Tags.IMG_CONFIG);
        }
        if (savedInstanceState.getBoolean(ImageTags.Tags.IS_ALERT_SHOWING, false)) {
            if (alertDialog == null)
                pickImage();
            else
                alertDialog.show();
        }
    }

    private void startCropActivity(String imagePath) {
        Uri imageUri = Uri.fromFile(new File(imagePath));
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "]," +
                    " resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageTags.IntentCode.CAMERA_REQUEST:
                    startCropActivity(destination.getPath());
                    break;

                case ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO:
                    String selectedImagePath = FileProcessing.getPath(this, data.getData());
                    startCropActivity(selectedImagePath);
                    break;

                case ImageTags.IntentCode.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    processOneImage(result.getUri());

                    break;
                default:
                    break;
            }
        } else {
            Intent intent = new Intent();
            intent.setAction("net.alhazmy13.mediapicker.rxjava.image.service");
            intent.putExtra(ImageTags.Tags.PICK_ERROR, "user did not select any image");
            sendBroadcast(intent);
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void processMultiPhoto(Intent data) {
        //Check if the intent contain only one image
        if (data.getClipData() == null) {
            // processOneImage(data);
        } else {
            //intent has multi images
            listOfImgs = ImageProcessing.processMultiImage(this, data);
            if (listOfImgs != null && listOfImgs.size() > 0) {
                new CompressImageTask(listOfImgs, mImgConfig, ImageActivity.this).execute();
            } else {
                //For 'Select pic from Google Photos - app Crash' fix
                String check = data.getClipData().toString();
                if (check != null && check.contains("com.google.android.apps.photos")) {
                    ClipData clipdata = data.getClipData();
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
                        Uri selectedImage = clipdata.getItemAt(i).getUri();
                        String selectedImagePath = FileProcessing.getPath(ImageActivity.this, selectedImage);
                        listOfImgs.add(selectedImagePath);
                    }
                    new CompressImageTask(listOfImgs, mImgConfig, ImageActivity.this).execute();
                }
            }
        }
    }

    public void processOneImage(Uri data) {
        try {
            Uri selectedImage = data;
            if (selectedImage != null) {
                String rawPath = selectedImage.toString();
                //For 'Select pic from Google Drive - app Crash' fix
                if (rawPath.contains("com.google.android.apps.docs.storage")) {
                    String fileTempPath = getCacheDir().getPath();
                    new SaveImageFromGoogleDriveTask(fileTempPath, mImgConfig, selectedImage, ImageActivity.this).execute();
                } else {
                    String selectedImagePath = FileProcessing.getPath(this, selectedImage);
                    new CompressImageTask(selectedImagePath,
                            mImgConfig, ImageActivity.this).execute();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void finishActivity(List<String> path) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ImagePicker.EXTRA_IMAGE_PATH, (Serializable) path);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsList = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= 33) {
                permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES);

            }else{
                if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
            if ((mImgConfig.mode == ImagePicker.Mode.CAMERA || mImgConfig.mode == ImagePicker.Mode.CAMERA_AND_GALLERY) &&
                    !hasPermission(Manifest.permission.CAMERA)
            ) {
                permissionsList.add(Manifest.permission.CAMERA);
            }



            if (!permissionsList.isEmpty()) {
                requestPermissions(
                        permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS
                );
            } else {
                pickImage();
            }
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All required permissions granted, proceed with your logic
                pickImage();
            } else {
                // Some permissions were denied, handle accordingly (show a message, etc.)
            }
        }
    }


   /* private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<>();

            final List<String> permissionsList = new ArrayList<>();
            if ((mImgConfig.mode == ImagePicker.Mode.CAMERA || mImgConfig.mode == ImagePicker.Mode.CAMERA_AND_GALLERY) && !addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add(getString(R.string.media_picker_camera));
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add(getString(R.string.media_picker_read_Write_external_storage));

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    StringBuilder message = new StringBuilder(getString(R.string.media_picker_you_need_to_grant_access_to) + " " + permissionsNeeded.get(0));
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message.append(", ").append(permissionsNeeded.get(i));
                    showMessageOKCancel(message.toString(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("TAG", String.format("onClick: %d", which));
                                    switch (which) {
                                        case BUTTON_POSITIVE:
                                            Log.d("TAG", "onClick: " + permissionsList.size());
                                            ActivityCompat.requestPermissions(ImageActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                                    ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS);
                                            Log.d("TAG", "onClick: requestPermissions");
                                            break;

                                        default:
                                            finish();
                                            break;

                                    }


                                }
                            });
                    return;
                }
                ActivityCompat.requestPermissions(ImageActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                        ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }

            pickImage();
        } else {
            pickImage();
        }
    }*/

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.media_picker_ok), okListener)
                .setNegativeButton(getString(R.string.media_picker_cancel), okListener)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(ImageActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return ActivityCompat.shouldShowRequestPermissionRationale(ImageActivity.this, permission);
        }
        return true;
    }

    /*  @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          switch (requestCode) {
              case ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS:
                  Map<String, Integer> perms = new HashMap<>();
                  // Initial
                  perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                  perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                  // Fill with results
                  for (int i = 0; i < permissions.length; i++)
                      perms.put(permissions[i], grantResults[i]);
                  // Check for ACCESS_FINE_LOCATION
                  if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                          && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                      // All Permissions Granted
                      pickImage();
                  } else {
                      // Permission Denied
                      Toast.makeText(ImageActivity.this, getString(R.string.media_picker_some_permission_is_denied), Toast.LENGTH_SHORT)
                              .show();
                      onBackPressed();
                  }

                  break;
              default:
                  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          }
      }
  */
    private static class CompressImageTask extends AsyncTask<Void, Void, Void> {

        private final ImageConfig mImgConfig;
        private final List<String> listOfImgs;
        private List<String> destinationPaths;
        private WeakReference<ImageActivity> mContext;


        CompressImageTask(List<String> listOfImgs, ImageConfig imageConfig, ImageActivity context) {
            this.listOfImgs = listOfImgs;
            this.mContext = new WeakReference<>(context);
            this.mImgConfig = imageConfig;
            this.destinationPaths = new ArrayList<>();
        }

        CompressImageTask(String absolutePath, ImageConfig imageConfig, ImageActivity context) {
            List<String> list = new ArrayList<>();
            list.add(absolutePath);
            this.listOfImgs = list;
            this.mContext = new WeakReference<>(context);
            this.destinationPaths = new ArrayList<>();
            this.mImgConfig = imageConfig;
        }


        @Override
        protected Void doInBackground(Void... params) {
            for (String mPath : listOfImgs) {
                File file = new File(mPath);
                File destinationFile;
                if (mImgConfig.isImgFromCamera) {
                    destinationFile = file;
                } else {
                    destinationFile = new File(mImgConfig.directory, Utility.getRandomString() + mImgConfig.extension.getValue());
                }
                destinationPaths.add(destinationFile.getAbsolutePath());
                try {
                    Utility.compressAndRotateIfNeeded(file, destinationFile, mImgConfig.compressLevel.getValue(), mImgConfig.reqWidth, mImgConfig.reqHeight);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ImageActivity context = mContext.get();
            if (context != null) {
                context.finishActivity(destinationPaths);
                Intent intent = new Intent();
                intent.setAction("net.alhazmy13.mediapicker.rxjava.image.service");
                intent.putExtra(ImageTags.Tags.IMAGE_PATH, (Serializable) destinationPaths);
                context.sendBroadcast(intent);
            }
        }
    }


    private static class SaveImageFromGoogleDriveTask extends AsyncTask<Void, Void, Void> {

        private final ImageConfig mImgConfig;
        private final List<String> listOfImgs;
        private List<String> destinationPaths;
        private List<Uri> destinationUris;
        private WeakReference<ImageActivity> mContext;


        SaveImageFromGoogleDriveTask(String absolutePath, ImageConfig imageConfig, Uri uri, ImageActivity context) {
            List<String> list = new ArrayList<>();
            list.add(absolutePath);
            this.listOfImgs = list;

            List<Uri> uris = new ArrayList<>();
            uris.add(uri);
            destinationUris = uris;

            this.mContext = new WeakReference<>(context);
            this.destinationPaths = new ArrayList<>();
            this.mImgConfig = imageConfig;
        }


        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < listOfImgs.size(); i++) {
                String path = listOfImgs.get(i);
                Uri uriPath = destinationUris.get(i);
                try {
                    String fileName = "drive_img_" + System.currentTimeMillis() + ".jpg";
                    String fullImagePath = path + "/" + fileName;
                    boolean isFileSaved = saveFile(uriPath, fullImagePath);
                    if (isFileSaved) {
                        destinationPaths.add(fullImagePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new CompressImageTask(destinationPaths, mImgConfig, mContext.get()).execute();
        }

        boolean filenotfoundexecption;

        //For Google Drive
        boolean saveFile(Uri sourceuri, String destination) throws IOException {
            filenotfoundexecption = false;
            int originalsize;
            InputStream input = null;
            try {
                input = mContext.get().getContentResolver().openInputStream(sourceuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                filenotfoundexecption = true;
            }

            try {
                originalsize = input.available();
                BufferedInputStream bis;
                BufferedOutputStream bos;
                try {
                    bis = new BufferedInputStream(input);
                    bos = new BufferedOutputStream(new FileOutputStream(destination, false));
                    byte[] buf = new byte[originalsize];
                    bis.read(buf);
                    do {
                        bos.write(buf);
                    } while (bis.read(buf) != -1);
                } catch (IOException e) {
                    filenotfoundexecption = true;
                    return false;
                }
            } catch (NullPointerException e) {
                filenotfoundexecption = true;
            }
            return true;
        }

    }
}
