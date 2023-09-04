package com.example.boozzapp.utils.gallery_custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boozzapp.R;
import com.example.boozzapp.databinding.ActivityGalleryAlbumBinding;
import com.example.boozzapp.utils.PartyMyGridLayoutManager;
import com.example.boozzapp.utils.crop.PartyCropImageActivity;
import com.example.boozzapp.utils.gallery_custom.adapters.PartyAdapter_Gallery_Albums;
import com.example.boozzapp.utils.gallery_custom.adapters.PartyAdapter_Gallery_Images;
import com.example.boozzapp.utils.gallery_custom.adapters.PartyAdapter_Selected_Gallery_Images;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyAlbumFile;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyFilePathFromUri;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyFilter;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyMediaReadTask;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyMediaReader;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyModelCommandImages;
import com.example.boozzapp.utils.gallery_custom.gallery.PhonePhoto;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class PartyGalleryAlbumActivity extends AppCompatActivity implements PartyMediaReadTask.Callback {

    private ActivityGalleryAlbumBinding mBinding;

    private Activity mContext;
    private ArrayList<PartyModelCommandImages> arrayList = new ArrayList<>();
    private ArrayList<PartyAlbumFolder> albums = new ArrayList<>();
    private Vector<Object> photos = new Vector<>();
    private Vector<Object> originalPhotos = new Vector<>();
    Vector<PhonePhoto> allphotos = new Vector<>();
    private PartyAdapter_Gallery_Images adapterGalleryImages;
    private PartyAdapter_Selected_Gallery_Images adapterSelectedGalleryImages;

    private PartyAdapter_Gallery_Albums adapterGalleryAlbums;

    public int selectedHeight, selectedWidth;
    public boolean flagChange = false;


    private PartyMediaReadTask mMediaReadTask;

//    LoadingAnimationUtils loadingAnimationUtils;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gallery_album);
        mContext = this;

//        loadingAnimationUtils = new LoadingAnimationUtils(this);

        setupAd();
        Type type = new TypeToken<ArrayList<PartyModelCommandImages>>() {
        }.getType();
        String json = getIntent().getStringExtra("ImageList");
        int pos = getIntent().getIntExtra("ImagePos", 0);
        arrayList = new Gson().fromJson(json, type);
        if (arrayList.size() > 0) {
            if (arrayList.get(arrayList.size() - 1).getImgName().equals("text"))
                arrayList.remove(arrayList.size() - 1);
        }

        if (arrayList.size() > 1)
            mBinding.tvSelection.setText("Please select images");
        else
            mBinding.tvSelection.setText("Please select an image");

        mBinding.ivBack.setOnClickListener(v -> {

            onBackPressed();
        });

        if (arrayList.size() > 0) {
            selectedWidth = arrayList.get(0).getImgWidth();
            selectedHeight = arrayList.get(0).getImgHeight();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        PartyMyGridLayoutManager gridLayoutManager = new PartyMyGridLayoutManager(mContext, 3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapterGalleryImages.getItemViewType(position) == 5)
                    return 1;
                else
                    return 3;

            }
        });
        adapterGalleryImages = new PartyAdapter_Gallery_Images(photos, mContext);
        adapterSelectedGalleryImages = new PartyAdapter_Selected_Gallery_Images(arrayList, mContext, pos);
        adapterGalleryAlbums = new PartyAdapter_Gallery_Albums(albums, mContext);

        mBinding.rvPhotos.setLayoutManager(gridLayoutManager);
        mBinding.rvPhotos.setAdapter(adapterGalleryImages);
        mBinding.rvSelectedPhotos.setLayoutManager(linearLayoutManager);

        mBinding.rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                   /* if(originalPhotos.size()>0)
                        addDataWithNativeAd();*/

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                //int[] lastVisibleItem = layoutManagerList.findLastVisibleItemPositions(null);
                int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                //if (firstVisibleItem[0] > 5)
                if (firstVisibleItem > 15)
                    mBinding.fab.show();
                else
                    mBinding.fab.hide();

                if (dy > 0) {

                  /*  if(originalPhotos.size()>0) {
                        if (gridLayoutManager.findLastVisibleItemPosition() >= (photos.size() - 1)) {
                            addDataWithNativeAd();
                        }
                    }*/

                }
            }
        });
        mBinding.fab.setOnClickListener(v -> {

            mBinding.rvPhotos.smoothScrollToPosition(0);
        });
        mBinding.rvSelectedPhotos.setAdapter(adapterSelectedGalleryImages);
        mBinding.rvAlbums.setLayoutManager(layoutManager);
        mBinding.rvAlbums.setAdapter(adapterGalleryAlbums);
//        mBinding.rvSelectedPhotos.setItemAnimator(new MyItemAnimator());

        mBinding.layoutAlbums.setOnClickListener(null);
        mBinding.btnAlbum.setOnClickListener(v -> {
            if (albums.size() > 0) {

                if (mBinding.layoutAlbums.getVisibility() == View.GONE) {
                    mBinding.rvAlbums.scrollToPosition(0);
                    mBinding.layoutAlbums.setVisibility(View.VISIBLE);
                    mBinding.layoutImages.setVisibility(View.GONE);
                    mBinding.ivArrow.setImageResource(R.drawable.ic_arrow_upward);
                } else {
                    hideAlbums();
                }
            }
        });

        mBinding.cvDone.setOnClickListener(v -> {

            if (flagChange) {
                Intent intent = new Intent("com.example.boozzapp.BR_NEW_IMAGE_CHANGE");
                intent.putExtra("ImageList", new Gson().toJson(arrayList));
                sendBroadcast(intent);
            }
            super.onBackPressed();
        });

        fetchGalleryImages();


    }

    private void setupAd() {
        AdView myVideoBannerAdView = findViewById(R.id.galleryBannerAdView);
        TextView adMyVideoLoadingText = findViewById(R.id.adGalleryLoadingText);
        AdRequest adRequest = new AdRequest.Builder().build();
        myVideoBannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adMyVideoLoadingText.setVisibility(View.INVISIBLE);
                myVideoBannerAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i("TAG", "onAdFailedToLoad: MyVideo" + loadAdError.getMessage());
                Log.i("TAG", "onAdFailedToLoad: MyVideo" + loadAdError.getCode());

                adMyVideoLoadingText.setVisibility(View.VISIBLE);
                myVideoBannerAdView.setVisibility(View.INVISIBLE);
            }
        });
        myVideoBannerAdView.loadAd(adRequest);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
        if (mBinding.layoutAlbums.getVisibility() == View.VISIBLE) {
            hideAlbums();
        } else if (flagChange) {

            Dialog dialog = new Dialog(mContext, R.style.MyAlertDialog);
            dialog.setCancelable(true);

            // Create a LinearLayout as the root view of the dialog
            LinearLayout dialogLayout = new LinearLayout(mContext);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(16, 16, 16, 16);

            // Create TextViews for the dialog content
            TextView tvAlert = new TextView(mContext);
            tvAlert.setText("Save changes?");
            tvAlert.setTextSize(18);

            TextView tvTitle = new TextView(mContext);
            tvTitle.setText("Click YES to save your changes.");
            tvTitle.setTextSize(16);

            // Create Buttons for the dialog
            Button btnNo = new Button(mContext);
            btnNo.setText("NO");
            btnNo.setOnClickListener(v -> {
                dialog.dismiss();
                super.onBackPressed();
            });

            Button btnYes = new Button(mContext);
            btnYes.setText("YES");
            btnYes.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent("com.example.boozzapp.BR_NEW_IMAGE_CHANGE");
                intent.putExtra("ImageList", new Gson().toJson(arrayList));
                sendBroadcast(intent);
                super.onBackPressed();
            });

            // Add views to the dialogLayout
            dialogLayout.addView(tvAlert);
            dialogLayout.addView(tvTitle);
            dialogLayout.addView(btnNo);
            dialogLayout.addView(btnYes);

            // Set dialog content view to the dialogLayout
            dialog.setContentView(dialogLayout);
            dialog.show();

        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        freeMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                arrayList.get(adapterSelectedGalleryImages.selectedItem)
                        .setImgPathExtra(Objects.requireNonNull(data).getStringExtra("FilePath"));
                arrayList.get(adapterSelectedGalleryImages.selectedItem).setChangesOccurs(true);
                //adapterSelectedGalleryImages.notifyItemChanged(adapterSelectedGalleryImages.selectedItem);
                if (adapterSelectedGalleryImages.selectedItem < arrayList.size() - 1) {
                    adapterSelectedGalleryImages.selectedItem = adapterSelectedGalleryImages.selectedItem + 1;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapterSelectedGalleryImages.notifyDataSetChanged();

                        Log.d("Handler", "Running Handler");
                    }
                }, 100);
                flagChange = true;
            }
        } else if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                String filePath = ImagePicker.Companion.getFilePath(data);
                // Log.e("filePath ", filePath);
                Intent intent = new Intent(mContext, PartyCropImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("ImagePath", filePath);
                intent.putExtra("ImageHeight", ((PartyGalleryAlbumActivity) mContext).selectedHeight);
                intent.putExtra("ImageWidth", ((PartyGalleryAlbumActivity) mContext).selectedWidth);
                mContext.startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Selected file format not supported.", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK) {
            if ((requestCode == PartyAdapter_Gallery_Images.REQUEST_CAPTURE_IMAGE || requestCode == PartyAdapter_Gallery_Images.REQUEST_SELECT_PICTURE) && data != null) {
                try {
                    Uri selectedUri = data.getData();

                    if (selectedUri != null) {

                        Intent intent = new Intent(mContext, PartyCropImageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("ImagePath", PartyFilePathFromUri.getPath(mContext, selectedUri));
                        intent.putExtra("ImageHeight", selectedHeight);
                        intent.putExtra("ImageWidth", selectedWidth);
                        mContext.startActivityForResult(intent, 101);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void freeMemory() {
        try {
            Runtime.getRuntime().gc();
            System.gc();
            System.runFinalization();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void hideAlbums() {
        mBinding.layoutAlbums.setVisibility(View.GONE);
        mBinding.layoutImages.setVisibility(View.VISIBLE);
        mBinding.ivArrow.setImageResource(R.drawable.ic_down_arrow);
    }

    public void setAlbumImages(ArrayList<PartyAlbumFile> phonePhotos, String name) {
        mBinding.tvTitle.setText(name);
       /* try {
            for (Object photo : photos) {
                if (photo instanceof UnifiedNativeAd) {
                    ((UnifiedNativeAd) photo).destroy();
                } else if (photo instanceof NativeAd) {
                    ((NativeAd) photo).destroy();
                }
            }
        } catch (Exception e) {
        }*/
        this.photos.clear();
        adapterGalleryImages.notifyDataSetChanged();
        this.originalPhotos.clear();

        this.originalPhotos.addAll(phonePhotos);
        photos.add(new PartyAlbumFile());
        photos.add(new PartyAlbumFile());
        adapterGalleryImages.notifyDataSetChanged();
        mBinding.rvPhotos.scrollToPosition(0);
        this.photos.addAll(originalPhotos);
        adapterGalleryImages.notifyDataSetChanged();
        // addDataWithNativeAd();
    }


    public void setSelectedImageSize(int height, int width) {
        this.selectedHeight = height;
        this.selectedWidth = width;
    }

    public Boolean getInstalled() {
        try {
            mContext.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            try {
                mContext.getPackageManager().getApplicationInfo("com.facebook.lite", 0);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }


    public static PartyFilter<Long> sSizeFilter;
    public static PartyFilter<String> sMimeFilter;
    public static PartyFilter<Long> sDurationFilter;

    private boolean mFilterVisibility = true;


    private void fetchGalleryImages() {

        // ArrayList<AlbumFile> checkedList = getIntent().getParcelableArrayListExtra("KEY_INPUT_CHECKED_LIST");
        ArrayList<PartyAlbumFile> checkedList = new ArrayList<>();
        PartyMediaReader mediaReader = new PartyMediaReader(this, sSizeFilter, sMimeFilter, sDurationFilter, mFilterVisibility);
        mMediaReadTask = new PartyMediaReadTask(PartyGalleryAlbumActivity.this, 0, checkedList, mediaReader, this);
        mMediaReadTask.execute();
    }


    @Override
    public void onScanCallback(ArrayList<PartyAlbumFolder> partyAlbumFolders, ArrayList<PartyAlbumFile> checkedFiles) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.pbLoading.setVisibility(View.GONE);

                mMediaReadTask = null;
                albums.addAll(partyAlbumFolders);
                adapterGalleryAlbums.notifyDataSetChanged();
                setAlbumImages(albums.get(0).getAlbumFiles(), albums.get(0).getName());
            }
        });
    }

    @Override
    public void onScanCallbackWhatsApp(ArrayList<PartyAlbumFile> whatsAppStatusFiles) {
    }

    private int getDeviceHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}