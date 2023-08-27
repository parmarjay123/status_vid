package com.example.boozzapp.utils.gallery_custom.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PartySquareLayout extends LinearLayout {

    public PartySquareLayout(Context context) {
        super(context);
    }

    public PartySquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = width;
        this.setLayoutParams(params);
        this.setMeasuredDimension(width, width);
        super.onLayout(changed, l, t, r, t + width);
    }
}
