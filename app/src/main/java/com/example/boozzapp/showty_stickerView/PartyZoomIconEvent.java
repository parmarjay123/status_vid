package com.example.boozzapp.showty_stickerView;

import android.view.MotionEvent;

public class PartyZoomIconEvent implements PartyStickerIconEvent {
    @Override
    public void onActionDown(PartyStickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(PartyStickerView stickerView, MotionEvent event) {
        stickerView.zoomAndRotateCurrentSticker(event);
    }

    @Override
    public void onActionUp(PartyStickerView stickerView, MotionEvent event) {
        if (stickerView.getOnStickerOperationListener() != null) {
            if (stickerView.getCurrentSticker() != null) {
                stickerView.getOnStickerOperationListener()
                        .onStickerZoomFinished(stickerView.getCurrentSticker());
            }
        }
    }
}
