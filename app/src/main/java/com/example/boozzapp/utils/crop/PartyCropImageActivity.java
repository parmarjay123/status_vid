package com.example.boozzapp.utils.crop;

import static com.facebook.ads.AdSize.BANNER_HEIGHT_50;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PartyCropImageActivity extends AppCompatActivity {
    ActivityCropImageBinding mBinding;

    Activity mContext;

    int avialWidth = 0;
    int avialHeight = 0;
    int targetHeight = 0;
    int targetWidth = 0;
    int width;
    int height;
    int tabPos;
    boolean flagStickerAdded = false;
    String path = "";


    private AdView adView;


    private final String strBRLoadImage = "com.statusmaker.showty.BRLoadImage";
    private BroadcastReceiver brLoadImage;
    com.facebook.ads.AdView facebookadview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_crop_image);
        mContext = this;


        mBinding.ibBack.setOnClickListener(v -> {
          
            onBackPressed();
        });

        path = getIntent().getStringExtra("ImagePath");

        width = getIntent().getIntExtra("ImageWidth", 512);
        height = getIntent().getIntExtra("ImageHeight", 512);
        Log.i("TAG", "onCreate: wwwww" + width);
        Log.i("TAG", "onCreate: hhhhh" + height);
        mBinding.llMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.llMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                avialWidth = mBinding.llMainLayout.getMeasuredWidth();
                avialHeight = mBinding.llMainLayout.getMeasuredHeight();

                if (height > width) {
                    double aspectRatio = (double) width / (double) height;
                    targetHeight = avialHeight;
                    targetWidth = (int) (targetHeight * aspectRatio);

                    if (targetWidth > avialWidth) {
                        targetWidth = avialWidth;
                        aspectRatio = (double) height / (double) width;
                        targetHeight = (int) (targetWidth * aspectRatio);
                    }

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(targetWidth, targetHeight);
                    layoutParams.gravity = Gravity.CENTER;
                    mBinding.llContainerLayout.setLayoutParams(layoutParams);
                } else {
                    double aspectRatio = (double) height / (double) width;
                    targetHeight = (int) (avialWidth * aspectRatio);
                    targetWidth = avialWidth;
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(avialWidth, targetHeight);
                    layoutParams.gravity = Gravity.CENTER;
                    mBinding.llContainerLayout.setLayoutParams(layoutParams);
                }
            }
        });

        try {
            mBinding.cropView.setImageFilePath(path);
            mBinding.cropView.setAspectRatio(width, height);
        } catch (IllegalArgumentException e) {
            Toast.makeText(mContext, "File not valid!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        mBinding.layoutLoad.setOnClickListener(null);

        setCurrentCrop(1);

        mBinding.tvCrop.setOnClickListener(v -> {
          
            setCurrentCrop(1);
        });

        mBinding.tvNoCrop.setOnClickListener(v -> {
          
            setCurrentCrop(2);
        });

        mBinding.ivDone.setOnClickListener(v -> {
          
            if (tabPos == 2)
                saveAndUseImage(mBinding.llContainerLayout);
            else {
                Bitmap bitmap = mBinding.cropView.getCroppedImage();
                if (bitmap != null)
                    saveAndUseImage(bitmap);
            }
        });

        brLoadImage = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.ivDone.setVisibility(View.VISIBLE);
                mBinding.pbLoading.setVisibility(View.GONE);
//                loadingAnimationUtils.dismiss();
            }
        };
        registerReceiver(brLoadImage, new IntentFilter(strBRLoadImage));


        //  showAdsSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (adView != null)
                adView.destroy();

            unregisterReceiver(brLoadImage);
        } catch (Exception ignored) {

        }
        freeMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    //region Ad zone...
   /* private void showAdsSettings() {
        if (PartyAppPreferences.isAdShow(PartyCropImageActivity.this)) {
            if (PartyAppPreferences.isisgooglebanner(this)) {
                requestSimpleBannerAd();
            } else {
                secondrequestFBNativeAsBanner();
            }
        }
    }*/

   /* private void requestSimpleBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.gl_PartyCropImageActivity_banner));
        adView.setAdSize(AdSize.BANNER);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mBinding.adZone.removeAllViews();
                mBinding.adZone.addView(adView);
                mBinding.separator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i("TAG", "onAdFailedToLoad: "+loadAdError.getMessage());
                mBinding.separator.setVisibility(View.GONE);
                mBinding.adZone.removeAllViews();
                requestNativeAsBanner();
            }
        });
    }*/

    /*private void requestNativeAsBanner() {
        mBinding.nativeTemplate.setTemplateType(R.layout.gnt_template_view_50);
        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.gl_PartyCropImageActivity_Native_banner));
        builder.forNativeAd(unifiedNativeAd -> {
            PartyNativeTemplateStyle styles = new PartyNativeTemplateStyle.Builder().build();
            mBinding.nativeTemplate.setStyles(styles);
            mBinding.nativeTemplate.setNativeAd(unifiedNativeAd);
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError errorCode) {
                mBinding.nativeTemplate.setVisibility(View.GONE);
                mBinding.separator.setVisibility(View.GONE);
//                requestFBNativeAsBanner();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mBinding.nativeTemplate.setVisibility(View.VISIBLE);
                mBinding.separator.setVisibility(View.VISIBLE);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }*/

  /*  private void requestFacebookBannerAd() {
        com.facebook.ads.AdView facebookadview = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_PartyCropImageActivity_banner), BANNER_HEIGHT_50);
        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                mBinding.separator.setVisibility(View.GONE);
                mBinding.adZone.removeAllViews();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mBinding.separator.setVisibility(View.VISIBLE);
                mBinding.adZone.removeAllViews();
                mBinding.adZone.addView(facebookadview);
                mBinding.adZone.setVisibility(View.VISIBLE);
                mBinding.nativeTemplate.setVisibility(View.GONE);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };
        facebookadview.loadAd(facebookadview.buildLoadAdConfig().withAdListener(adListener).build());
    }*/

  /*  private void requestFBNativeAsBanner() {
        NativeBannerAd nativeBannerAd = new NativeBannerAd(this, getResources().getString(R.string.fb_PartyCropImageActivity_Native_banner));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                requestFacebookBannerAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd != ad) {
                    return;
                }
                mBinding.nativeBannerAdContainer.setVisibility(View.VISIBLE);
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };
        // load the ad
        nativeBannerAd.loadAd(
                nativeBannerAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());

    }*/

   /* private void secondrequestFacebookBannerAd() {
        com.facebook.ads.AdView facebookadview = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_PartyCropImageActivity_banner), BANNER_HEIGHT_50);
        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                mBinding.separator.setVisibility(View.GONE);
                mBinding.adZone.removeAllViews();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mBinding.separator.setVisibility(View.VISIBLE);
                mBinding.adZone.removeAllViews();
                mBinding.adZone.addView(facebookadview);
                mBinding.adZone.setVisibility(View.VISIBLE);
                mBinding.nativeTemplate.setVisibility(View.GONE);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };
        facebookadview.loadAd(facebookadview.buildLoadAdConfig().withAdListener(adListener).build());
    }*/

   /* private void secondrequestFBNativeAsBanner() {
        NativeBannerAd nativeBannerAd = new NativeBannerAd(this, getResources().getString(R.string.fb_PartyCropImageActivity_Native_banner));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                secondrequestFacebookBannerAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd != ad) {
                    return;
                }
                mBinding.nativeBannerAdContainer.setVisibility(View.VISIBLE);
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };
        // load the ad
        nativeBannerAd.loadAd(
                nativeBannerAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());

    }*/

   /* private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();
        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(this);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.fb_template_view_50, mBinding.nativeBannerAdContainer, false);

        mBinding.nativeBannerAdContainer.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeBannerAd, mBinding.nativeBannerAdContainer);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView body = adView.findViewById(R.id.body);
        MediaView nativeAdIconView = adView.findViewById(R.id.ad_media);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setAllCaps(true);
        nativeAdCallToAction.setVisibility(nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdHeadline());
        body.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }*/

    private int getDeviceHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.heightPixels;
    }

    //region Set zone...
    private void setCurrentCrop(int pos) {
        tabPos = pos;
        switch (pos) {
            case 1:
                mBinding.cropView.setVisibility(View.VISIBLE);
                mBinding.llContainerLayout.setVisibility(View.GONE);
                mBinding.tvCrop.setTextColor(Color.WHITE);
                mBinding.tvCrop.setBackground(getResources().getDrawable(R.drawable.border_active_tab));
                mBinding.tvNoCrop.setTextColor(getResources().getColor(R.color.grey));
                mBinding.tvNoCrop.setBackground(getResources().getDrawable(R.drawable.border_inactive_tab));
                break;
            case 2:
                mBinding.cropView.setVisibility(View.GONE);
                mBinding.llContainerLayout.setVisibility(View.VISIBLE);
                mBinding.tvCrop.setTextColor(getResources().getColor(R.color.grey));
                mBinding.tvCrop.setBackground(getResources().getDrawable(R.drawable.border_inactive_tab));
                mBinding.tvNoCrop.setTextColor(Color.WHITE);
                mBinding.tvNoCrop.setBackground(getResources().getDrawable(R.drawable.border_active_tab));
                if (!flagStickerAdded) {
                    flagStickerAdded = true;
                    mBinding.pbSticker.setVisibility(View.VISIBLE);
//                    loadingAnimationUtils.show();
                    setFullImage();
                }
                break;
        }
    }

    private void setFullImage() {
        try {
            Glide.with(mContext)
                    .load(path)
                    .override(targetWidth, targetHeight)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ImageCrop>>>", Log.getStackTraceString(e));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (!isFinishing()) {
                                mBinding.pbSticker.setVisibility(View.GONE);
//                                loadingAnimationUtils.dismiss();

                                PartyDrawableSticker sticker = new PartyDrawableSticker(resource);
                                mBinding.stickerView.addSticker(sticker, targetWidth, targetHeight);

                                try {
                                    Bitmap bitmap = BlurImage.with(mContext).load(drawableToBitmap(resource)).intensity(10f).Async(true).getImageBlur();
                                    mBinding.ivBlurImage.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }
                    })
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mBinding.ivFake);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void saveAndUseImage(View view) {
        if (view.getHeight() > 0 && view.getWidth() > 0) {
            Bitmap mainBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mainBitmap);
            view.draw(canvas);
            new SaveImage(this, mainBitmap).execute();
        }
    }

    private void saveAndUseImage(Bitmap cropedBitmap) {
        new SaveImage(this, cropedBitmap).execute();
    }

    private static class SaveImage extends AsyncTask<Void, Void, File> {
        WeakReference<PartyCropImageActivity> weakReference;
        Bitmap bitmap;

        SaveImage(PartyCropImageActivity activity, Bitmap bitmap) {
            weakReference = new WeakReference<>(activity);
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PartyCropImageActivity activity = weakReference.get();
            if (activity != null) {
                activity.mBinding.ivDone.setVisibility(View.GONE);
                activity.mBinding.pbLoading.setVisibility(View.VISIBLE);
//                loadingAnimationUtils.show();
            }
        }

        @Override
        protected File doInBackground(Void... voids) {
            PartyCropImageActivity activity = weakReference.get();
            if (activity != null) {
                try {
                    String fileName = "PictureCrop" + System.currentTimeMillis() / 1000 + ".jpg";
                    File file = new File(activity.getCacheDir(), fileName);
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    return file;
                } catch (Exception e) {
                    Log.e("ImageCrop>>>", Log.getStackTraceString(e));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            PartyCropImageActivity activity = weakReference.get();
            if (activity != null) {
                activity.mBinding.ivDone.setVisibility(View.VISIBLE);
                activity.mBinding.pbLoading.setVisibility(View.GONE);
                if (file != null)
                    activity.setResults(file);
            }
        }
    }

    private void setResults(File file) {
        Intent data = new Intent();
        data.putExtra("FilePath", file.getAbsolutePath());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
    //endregion
}
