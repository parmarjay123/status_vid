package com.example.boozzapp.utils.gallery_custom.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boozzapp.R;
import com.example.boozzapp.databinding.RowLayoutGalleryAlbumsBinding;
import com.example.boozzapp.utils.gallery_custom.PartyAlbumFolder;
import com.example.boozzapp.utils.gallery_custom.PartyGalleryAlbumActivity;

import java.util.ArrayList;

public class PartyAdapter_Gallery_Albums extends RecyclerView.Adapter<PartyAdapter_Gallery_Albums.AlbumsViewHolder> {
    private ArrayList<PartyAlbumFolder> albums;
    private Activity mContext;

    public PartyAdapter_Gallery_Albums(ArrayList<PartyAlbumFolder> albums, Activity mContext) {
        this.albums = albums;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AlbumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowLayoutGalleryAlbumsBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.row_layout_gallery_albums, parent, false);
        return new AlbumsViewHolder(mBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AlbumsViewHolder holder, int position) {
        RowLayoutGalleryAlbumsBinding mBinding = holder.mBinding;

        try {
            Glide.with(mContext)
                    .load(albums.get(position).getAlbumFiles().get(0).getPath())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mBinding.ivAlbum);
        } catch (Exception e) {

        }

        mBinding.tvTitle.setText(albums.get(position).getName());

        mBinding.tvCount.setText(albums.get(position).getAlbumFiles().size() + "");

        mBinding.llAlbum.setOnClickListener(v -> {
            if (position != -1) {

                ((PartyGalleryAlbumActivity) mContext).setAlbumImages(albums.get(position).getAlbumFiles(),
                        albums.get(position).getName());
                ((PartyGalleryAlbumActivity) mContext).hideAlbums();
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class AlbumsViewHolder extends RecyclerView.ViewHolder {
        RowLayoutGalleryAlbumsBinding mBinding;

        AlbumsViewHolder(RowLayoutGalleryAlbumsBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}
