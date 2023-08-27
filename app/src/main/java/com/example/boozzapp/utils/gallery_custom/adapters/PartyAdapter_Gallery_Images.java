package com.example.boozzapp.utils.gallery_custom.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.boozzapp.R;
import com.example.boozzapp.databinding.RowLayoutGalleryImagesBinding;
import com.example.boozzapp.utils.crop.PartyCropImageActivity;
import com.example.boozzapp.utils.gallery_custom.PartyGalleryAlbumActivity;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyAlbumFile;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.io.File;
import java.util.Vector;

public class PartyAdapter_Gallery_Images extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int REQUEST_SELECT_PICTURE = 999;
    public static final int REQUEST_CAPTURE_IMAGE = 989;
    private Vector<Object> photos;
    private Activity mContext;
    int width = 540;
    int height = 300;
    public int GOOGLE_AD_VIEW_TYPE = 4;
    ;
    public int FB_AD_VIEW_TYPE = 3;
    public int TYPE_ITEM = 5;

    public PartyAdapter_Gallery_Images(Vector<Object> photos, Activity mContext) {
        this.photos = photos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowLayoutGalleryImagesBinding mBinding = RowLayoutGalleryImagesBinding.inflate(LayoutInflater.from(mContext));
        return new GalleryImagesViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GalleryImagesViewHolder) {

            RowLayoutGalleryImagesBinding mBinding = ((GalleryImagesViewHolder) holder).mBinding;

            if (position == 0) {
                if (!mContext.isFinishing()) {
                    try {

                        Glide.with(mContext)
                                .load(R.drawable.img_camera_light)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(mBinding.ivImage);
                    } catch (Exception e) {

                    }

                }

                mBinding.ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        {
                            Intent intent =new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());

                            mContext.startActivityForResult(Intent.createChooser(intent, "Capture Image"), REQUEST_SELECT_PICTURE);

                        }else {*/
                        ImagePicker.Companion.with(mContext)
                                .cameraOnly()
                                .galleryMimeTypes(new String[]{
                                        "image/png",
                                        "image/jpg",
                                        "image/jpeg",
                                        "image/webp",
                                        "image/bmp"})
                                .start();
                        //}
                    }
                });

            } else if (position == 1) {

                try {

                    Glide.with(mContext)
                            .load(R.drawable.img_gallery_light)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(mBinding.ivImage);


                    mBinding.ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg", "image/webp",
                                    "image/bmp"};
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                            mContext.startActivityForResult(Intent.createChooser(intent, "Choose from Gallery"), REQUEST_SELECT_PICTURE);


                        }
                    });


                } catch (Exception e) {
                }

            } else {
                PartyAlbumFile photo = (PartyAlbumFile) photos.get(position);
                try {
                    Glide.with(mContext)
                            //.load(Uri.parse(photo.getPhotoPath()))
                            .load(photo.getPath())
                            .format(DecodeFormat.PREFER_RGB_565)
                            .signature(getFileSignature(photo.getPath()))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mBinding.ivImage);
                } catch (Exception e) {
                }

                mBinding.ivImage.setOnClickListener(v -> {
                    if (holder.getAdapterPosition() != -1) {

                        Intent intent = new Intent(mContext, PartyCropImageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("ImagePath", photo.getPath());
                        intent.putExtra("ImageHeight", ((PartyGalleryAlbumActivity) mContext).selectedHeight);
                        intent.putExtra("ImageWidth", ((PartyGalleryAlbumActivity) mContext).selectedWidth);
                        mContext.startActivityForResult(intent, 101);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = photos.get(position);

        if (recyclerViewItem instanceof NativeAdView) {
            return GOOGLE_AD_VIEW_TYPE;
        } else if (recyclerViewItem instanceof PartyAlbumFile) {
            return TYPE_ITEM;
        } else {
            return GOOGLE_AD_VIEW_TYPE;
        }
    }

    static class GalleryImagesViewHolder extends RecyclerView.ViewHolder {
        RowLayoutGalleryImagesBinding mBinding;

        GalleryImagesViewHolder(RowLayoutGalleryImagesBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

    private ObjectKey getFileSignature(String path) {
        return new ObjectKey(new File(path).getAbsolutePath() + new File(path).lastModified());
    }


}
