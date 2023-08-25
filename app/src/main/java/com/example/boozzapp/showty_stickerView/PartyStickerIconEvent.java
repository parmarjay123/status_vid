package com.example.boozzapp.showty_stickerView;

import android.view.MotionEvent;

public interface PartyStickerIconEvent {
    void onActionDown(PartyStickerView stickerView, MotionEvent event);

    void onActionMove(PartyStickerView stickerView, MotionEvent event);

    void onActionUp(PartyStickerView stickerView, MotionEvent event);
}
