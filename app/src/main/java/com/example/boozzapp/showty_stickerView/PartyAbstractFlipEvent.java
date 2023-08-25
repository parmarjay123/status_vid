package com.example.boozzapp.showty_stickerView;

import android.view.MotionEvent;

public abstract class PartyAbstractFlipEvent implements PartyStickerIconEvent {

    @Override
    public void onActionDown(PartyStickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(PartyStickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionUp(PartyStickerView stickerView, MotionEvent event) {
        stickerView.flipCurrentSticker(getFlipDirection());
    }

    @PartyStickerView.Flip
    protected abstract int getFlipDirection();
}
