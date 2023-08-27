package com.example.boozzapp.utils.gallery_custom.adapters;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boozzapp.R;
import com.example.boozzapp.databinding.RowLayoutGallerySelectedImagesBinding;
import com.example.boozzapp.utils.gallery_custom.PartyGalleryAlbumActivity;
import com.example.boozzapp.utils.gallery_custom.gallery.PartyModelCommandImages;

import java.util.ArrayList;

public class PartyAdapter_Selected_Gallery_Images extends RecyclerView.Adapter<PartyAdapter_Selected_Gallery_Images.SelectedImagesViewHolder> {
    private ArrayList<PartyModelCommandImages> arrayList;
    private Activity mContext;

    public int selectedItem = 0;

    public PartyAdapter_Selected_Gallery_Images(ArrayList<PartyModelCommandImages> arrayList, Activity activity, int selectedPosition) {
        this.arrayList = arrayList;
        this.mContext = activity;
        selectedItem = selectedPosition;
    }

    @NonNull
    @Override
    public SelectedImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowLayoutGallerySelectedImagesBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.row_layout_gallery_selected_images, parent, false);
        return new SelectedImagesViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesViewHolder holder, int position) {
        RowLayoutGallerySelectedImagesBinding mBinding = holder.mBinding;

        mBinding.layoutBorder.setSelected(selectedItem == position);

        if (selectedItem == position) {
            ((PartyGalleryAlbumActivity) mContext).setSelectedImageSize(arrayList.get(position).getImgHeight(), arrayList.get(position).getImgWidth());
        }

        if (selectedItem == position) {
            // ((GalleryAlbumActivity) mContext).setSelectedImageSize(arrayList.get(position).getImgHeight(), arrayList.get(position).getImgWidth());
            mBinding.layoutBorder.setBackground(mContext.getResources().getDrawable(R.drawable.bg_select_image));
        } else {
            mBinding.layoutBorder.setBackground(mContext.getResources().getDrawable(R.drawable.bg_select_image_transparent));
        }

        if (arrayList.get(position).getImgPathExtra() == null) {
            try {
                Glide.with(mContext)
                        .load(arrayList.get(position).getImgPath())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .thumbnail(Glide.with(mContext).load(arrayList.get(position).getImgPath()))
                        .into(mBinding.ivSelectedPhoto);
            } catch (Exception e) {
            }

            mBinding.ivClose.setVisibility(View.GONE);
            mBinding.ivPlusSelected.setVisibility(View.VISIBLE);
        } else {
            try {
                Glide.with(mContext)
                        .load(arrayList.get(position).getImgPathExtra())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mBinding.ivSelectedPhoto);
            } catch (Exception e) {
            }

            mBinding.ivClose.setVisibility(View.VISIBLE);
            mBinding.ivPlusSelected.setVisibility(View.GONE);
        }

       /* mBinding.ivClose.setOnClickListener(v -> {
            Globals.playSound(mContext, R.raw.button_tap);
            ((GalleryAlbumActivity) mContext).removeSelectedImage(holder.getAdapterPosition());
            ((GalleryAlbumActivity) mContext).flagChange = true;
        });*/

       /* mBinding.layoutBorder.setOnClickListener(v -> {
            Globals.playSound(mContext, R.raw.button_tap);
            notifyItemChanged(selectedItem);
            selectedItem =position;
            notifyItemChanged(selectedItem);
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class SelectedImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowLayoutGallerySelectedImagesBinding mBinding;
        RelativeLayout layout_border;
        ImageView iv_close;

        SelectedImagesViewHolder(RowLayoutGallerySelectedImagesBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            layout_border = mBinding.layoutBorder;
            iv_close = mBinding.ivClose;
            layout_border.setOnClickListener(this);
            iv_close.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (getAdapterPosition() != -1) {
                if (view.getId() == R.id.layout_border) {

                    selectedItem = getAdapterPosition();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            Log.d("Handler", "Running Handler");
                        }
                    }, 100);

                } else if (view.getId() == R.id.iv_close) {
                    //notifyDataSetChanged();
                    arrayList.get(getAdapterPosition()).setImgPathExtra(null);
                    arrayList.get(getAdapterPosition()).setChangesOccurs(true);
                    // int oldpos=selectedItem;
                    selectedItem = getAdapterPosition();
                    // notifyItemChanged(oldpos);
                    // notifyItemChanged(selectedItem);
                    // ((GalleryAlbumActivity) mContext).flagChange = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            ((PartyGalleryAlbumActivity) mContext).flagChange = true;
                            Log.d("Handler", "Running Handler");
                        }
                    }, 100);

                }
            }
        }
    }

   /* public  int getSelectedPosition()
    {
       return  selectedItem;
    }

    public  void setSelectedPositon(int newPosition)
    {
         selectedItem=newPosition;
    }*/
}
