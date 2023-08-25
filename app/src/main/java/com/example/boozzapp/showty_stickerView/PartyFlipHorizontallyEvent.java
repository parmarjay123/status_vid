package com.example.boozzapp.showty_stickerView;

public class PartyFlipHorizontallyEvent extends PartyAbstractFlipEvent {

    @Override
    @PartyStickerView.Flip
    protected int getFlipDirection() {
        return PartyStickerView.FLIP_HORIZONTALLY;
    }
}
