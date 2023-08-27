package com.example.boozzapp.rateView.rateView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.boozzapp.R;


public class PartyStarCheckView extends View {

    /* renamed from: a */
    private Bitmap f18682a;

    /* renamed from: b */
    private Bitmap f18683b;

    /* renamed from: c */
    private Paint f18684c;

    /* renamed from: d */
    private Paint f18685d;

    /* renamed from: e */
    private boolean f18686e = false;
    /* access modifiers changed from: private */

    /* renamed from: f */
    public ValueAnimator f18687f;
    /* access modifiers changed from: private */

    /* renamed from: g */
    public ValueAnimator f18688g;
    /* access modifiers changed from: private */

    /* renamed from: h */
    public ValueAnimator f18689h;
    /* access modifiers changed from: private */

    /* renamed from: i */
    public C6051a f18690i;

    /* renamed from: j */
    private int f18691j = 0;

    /* renamed from: com.zjsoft.rate.view.StarCheckView$a */
    public interface C6051a {
        void onAnimationEnd(Animator animator);
    }

    public PartyStarCheckView(Context context) {
        super(context);
        m22814a();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        super.onDraw(canvas);
        if (getWidth() > 0 && getHeight() > 0) {
            float width = (float) (getWidth() / 2);
            float height = (float) (getHeight() / 2);
            ValueAnimator valueAnimator = this.f18688g;
            if (valueAnimator != null) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (getWidth() > getHeight()) {
                    i2 = getHeight() / 2;
                } else {
                    i2 = getWidth() / 2;
                }
                float f = ((float) i2) * floatValue;
                this.f18685d.setAlpha(((int) (((1.2f - floatValue) / 1.2f) * 255.0f)) * 2);
                this.f18685d.setShader(new RadialGradient(width, height, f, new int[]{1728043553, 1728043553, -855647711}, (float[]) null, Shader.TileMode.CLAMP));
                canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), f, this.f18685d);
            }
            boolean z = false;
            ValueAnimator valueAnimator2 = this.f18689h;
            int i3 = 255;
            if (valueAnimator2 != null) {
                float floatValue2 = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                i = (int) (((float) 255) * floatValue2);
                canvas.save();
                canvas.scale(floatValue2, floatValue2, width, height);
                z = true;
            } else {
                i = 255;
            }
            m22815a(canvas, this.f18682a, i);
            if (z) {
                canvas.restore();
            }
            ValueAnimator valueAnimator3 = this.f18687f;
            if (valueAnimator3 != null) {
                float floatValue3 = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                i3 = (int) (((float) 255) * floatValue3);
                canvas.scale(floatValue3, floatValue3, width, height);
            }
            if (this.f18686e) {
                m22815a(canvas, this.f18683b, i3);
            }
        }
    }

    public void setCheck(boolean z) {
        mo27705a(z, false);
    }

    public void setOnAnimationEnd(C6051a aVar) {
        this.f18690i = aVar;
    }

    public void setPosition(int i) {
        this.f18691j = i;
    }

    /* renamed from: b */
    private void m22817b() {
        this.f18687f = ValueAnimator.ofFloat(new float[]{0.4f, 1.0f});
        this.f18687f.addUpdateListener(new PartyC6057e(this));
        this.f18687f.setDuration(1200);
        this.f18687f.addListener(new PartyC6058f(this));
        this.f18687f.setInterpolator(new OvershootInterpolator(2.0f));
        this.f18687f.start();
        this.f18689h = ValueAnimator.ofFloat(new float[]{1.0f, 0.4f});
        this.f18689h.setDuration(400);
        this.f18689h.addListener(new PartyC6059g(this));
        this.f18689h.setInterpolator(new OvershootInterpolator(2.0f));
        this.f18689h.start();
        this.f18688g = ValueAnimator.ofFloat(new float[]{0.4f, 1.2f});
        this.f18688g.setDuration(1200);
        this.f18688g.addListener(new PartyC6060h(this));
        this.f18688g.setInterpolator(new AccelerateDecelerateInterpolator());
        this.f18688g.start();
    }

    /* renamed from: a */
    private void m22814a() {
        this.f18682a = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lib_rate_star);
        this.f18683b = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lib_rate_star_on);
        this.f18684c = new Paint();
        this.f18685d = new Paint();
        this.f18685d.setAntiAlias(true);
        this.f18685d.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public PartyStarCheckView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m22814a();
    }

    public PartyStarCheckView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m22814a();
    }

    /* renamed from: a */
    public void mo27705a(boolean z, boolean z2) {
        this.f18686e = z;
        if (!z || !z2) {
            ValueAnimator valueAnimator = this.f18687f;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.f18687f = null;
            }
            ValueAnimator valueAnimator2 = this.f18689h;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.f18689h = null;
            }
            ValueAnimator valueAnimator3 = this.f18688g;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
                this.f18688g = null;
            }
            postInvalidate();
            return;
        }
        m22817b();
    }

    /* renamed from: a */
    private void m22815a(Canvas canvas, Bitmap bitmap, int i) {
        if (bitmap != null && canvas != null) {
            if (i > 255) {
                i = 255;
            }
            this.f18684c.setAlpha(i);
            canvas.drawBitmap(bitmap, (float) ((getWidth() - bitmap.getWidth()) / 2), (float) ((getHeight() - bitmap.getHeight()) / 2), this.f18684c);
        }
    }
}
