package com.example.boozzapp.rateView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.boozzapp.R;


public class PartyStarShiningView extends View {

    /* renamed from: a */
    private Bitmap f18692a;

    /* renamed from: b */
    private Bitmap f18693b;

    /* renamed from: c */
    private Paint f18694c;

    /* renamed from: d */
    private boolean f18695d;

    public PartyStarShiningView(Context context) {
        super(context);
        m22820a();
    }

    /* renamed from: a */
    private void m22820a() {
        this.f18692a = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lib_rate_star);
        this.f18693b = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lib_rate_shining_right);
        this.f18694c = new Paint();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() > 0 && getHeight() > 0) {
            float width = (((float) getWidth()) / 2.0f) + (((float) this.f18692a.getWidth()) / 2.0f);
            float height = (((float) getHeight()) / 2.0f) - (((float) this.f18692a.getHeight()) / 2.0f);
            if (((float) this.f18693b.getWidth()) + width > ((float) getWidth())) {
                width = (float) (getWidth() - this.f18693b.getWidth());
            }
            if (height - ((float) this.f18693b.getHeight()) < 0.0f) {
                height = (float) this.f18693b.getHeight();
            }
            if (width - (((float) getWidth()) / 2.0f) > (((float) getHeight()) / 2.0f) - height) {
                width = ((((float) getWidth()) / 2.0f) + (((float) getHeight()) / 2.0f)) - height;
            } else {
                height = (((float) getHeight()) / 2.0f) - (width - (((float) getWidth()) / 2.0f));
            }
            Bitmap bitmap = this.f18693b;
            canvas.drawBitmap(bitmap, width, height - ((float) bitmap.getHeight()), this.f18694c);
        }
    }

    public void setRtl(boolean z) {
        this.f18695d = z;
    }

    public PartyStarShiningView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m22820a();
    }

    public PartyStarShiningView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m22820a();
    }
}
