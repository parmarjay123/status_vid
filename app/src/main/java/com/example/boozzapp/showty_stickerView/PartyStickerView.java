package com.example.boozzapp.showty_stickerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.example.boozzapp.R;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PartyStickerView extends FrameLayout {

    private final boolean showIcons;
    private final boolean showBorder;
    private final boolean bringToFrontCurrentSticker;

    @IntDef({
            ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON,
            ActionMode.CLICK
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface ActionMode {
        int NONE = 0;
        int DRAG = 1;
        int ZOOM_WITH_TWO_FINGER = 2;
        int ICON = 3;
        int CLICK = 4;
    }

    @IntDef(flag = true, value = {FLIP_HORIZONTALLY, FLIP_VERTICALLY})
    @Retention(RetentionPolicy.SOURCE)
    @interface Flip {
    }

    private static final String TAG = "StickerView";

    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;

    public static final int FLIP_HORIZONTALLY = 1;
    public static final int FLIP_VERTICALLY = 1 << 1;

    private final List<PartySticker> partyStickers = new ArrayList<>();
    private final List<PartyBitmapStickerIcon> icons = new ArrayList<>(4);

    private final Paint borderPaint = new Paint();
    private final RectF stickerRect = new RectF();

    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();

    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    private PointF midPoint = new PointF();
    // endregion
    private final int touchSlop;

    private PartyBitmapStickerIcon currentIcon;
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;
    private int svWidth = 10;
    private int svHeight = 10;

    @ActionMode
    private int currentMode = ActionMode.NONE;

    private PartySticker handlingPartySticker;

    private boolean locked;
    private boolean constrained;

    private OnStickerOperationListener onStickerOperationListener;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    public PartyStickerView(Context context) {
        this(context, null);
    }

    public PartyStickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PartyStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
            bringToFrontCurrentSticker =
                    a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false);

            borderPaint.setAntiAlias(true);
            borderPaint.setColor(a.getColor(R.styleable.StickerView_borderColor, Color.BLACK));
            borderPaint.setAlpha(a.getInteger(R.styleable.StickerView_borderAlpha, 128));

            configDefaultIcons();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public void configDefaultIcons() {
        PartyBitmapStickerIcon deleteIcon = new PartyBitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_close_white_18dp),
                PartyBitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new PartyDeleteIconEvent());
        PartyBitmapStickerIcon zoomIcon = new PartyBitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp),
                PartyBitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new PartyZoomIconEvent());
        PartyBitmapStickerIcon flipIcon = new PartyBitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_flip_white_18dp),
                PartyBitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new PartyFlipHorizontallyEvent());

        icons.clear();
        icons.add(deleteIcon);
        icons.add(zoomIcon);
        icons.add(flipIcon);
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void swapLayers(int oldPos, int newPos) {
        if (partyStickers.size() >= oldPos && partyStickers.size() >= newPos) {
            Collections.swap(partyStickers, oldPos, newPos);
            invalidate();
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void sendToLayer(int oldPos, int newPos) {
        if (partyStickers.size() >= oldPos && partyStickers.size() >= newPos) {
            PartySticker s = partyStickers.get(oldPos);
            partyStickers.remove(oldPos);
            partyStickers.add(newPos, s);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawStickers(canvas);
    }

    protected void drawStickers(Canvas canvas) {
        for (int i = 0; i < partyStickers.size(); i++) {
            PartySticker partySticker = partyStickers.get(i);
            if (partySticker != null) {
                partySticker.draw(canvas);
            }
        }

        if (handlingPartySticker != null && !locked && (showBorder || showIcons)) {

            getStickerPoints(handlingPartySticker, bitmapPoints);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint);
                canvas.drawLine(x1, y1, x3, y3, borderPaint);
                canvas.drawLine(x2, y2, x4, y4, borderPaint);
                canvas.drawLine(x4, y4, x3, y3, borderPaint);
            }

            //draw icons
            if (showIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (int i = 0; i < icons.size(); i++) {
                    PartyBitmapStickerIcon icon = icons.get(i);
                    switch (icon.getPosition()) {
                        case PartyBitmapStickerIcon.LEFT_TOP:

                            configIconMatrix(icon, x1, y1, rotation);
                            break;

                        case PartyBitmapStickerIcon.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            break;

                        case PartyBitmapStickerIcon.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            break;

                        case PartyBitmapStickerIcon.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            break;
                    }
                    icon.draw(canvas, borderPaint);
                }
            }
        }
    }

    protected void configIconMatrix(@NonNull PartyBitmapStickerIcon icon, float x, float y,
                                    float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2f, icon.getHeight() / 2f);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2f, y - icon.getHeight() / 2f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) return super.onInterceptTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = ev.getX();
            downY = ev.getY();

            return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return super.onTouchEvent(event);
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingPartySticker != null && isInStickerArea(handlingPartySticker, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingPartySticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingPartySticker);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }

        return true;
    }

    /**
     * @param event MotionEvent received from {@link #onTouchEvent)
     * @return true if has touch something
     */
    protected boolean onTouchDown(@NonNull MotionEvent event) {
        currentMode = ActionMode.DRAG;

        downX = event.getX();
        downY = event.getY();

        midPoint = calculateMidPoint();
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            currentMode = ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            handlingPartySticker = findHandlingSticker();
        }

        if (handlingPartySticker != null) {
            downMatrix.set(handlingPartySticker.getMatrix());
            if (bringToFrontCurrentSticker) {
                partyStickers.remove(handlingPartySticker);
                partyStickers.add(handlingPartySticker);
            }
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerTouchedDown(handlingPartySticker);
            }
        }

        if (currentIcon == null && handlingPartySticker == null) {
            return false;
        }
        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        long currentTime = SystemClock.uptimeMillis();

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingPartySticker != null) {
            currentIcon.onActionUp(this, event);
        }

        if (currentMode == ActionMode.DRAG
                && Math.abs(event.getX() - downX) < touchSlop
                && Math.abs(event.getY() - downY) < touchSlop
                && handlingPartySticker != null) {
            currentMode = ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(handlingPartySticker);
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(handlingPartySticker);
                }
            }
        }

        if (currentMode == ActionMode.DRAG && handlingPartySticker != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(handlingPartySticker);
            }
        }

        currentMode = ActionMode.NONE;
        lastClickTime = currentTime;
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (currentMode) {
            case ActionMode.NONE:
            case ActionMode.CLICK:
                break;
            case ActionMode.DRAG:
                if (handlingPartySticker != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingPartySticker.setMatrix(moveMatrix);
                    if (constrained) {
                        constrainSticker(handlingPartySticker);
                    }
                }
                break;
            case ActionMode.ZOOM_WITH_TWO_FINGER:
                if (handlingPartySticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                            midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingPartySticker.setMatrix(moveMatrix);
                }

                break;

            case ActionMode.ICON:
                if (handlingPartySticker != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(handlingPartySticker, event);
    }

    public void zoomAndRotateSticker(@Nullable PartySticker partySticker, @NonNull MotionEvent event) {
        if (partySticker != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingPartySticker.setMatrix(moveMatrix);
        }
    }

    protected void constrainSticker(@NonNull PartySticker partySticker) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = svHeight;
        partySticker.getMappedCenterPoint(currentCenterPoint, point, tmp);
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }

        partySticker.getMatrix().postTranslate(moveX, moveY);
    }

    @Nullable
    protected PartyBitmapStickerIcon findCurrentIconTouched() {
        for (PartyBitmapStickerIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched Sticker
     **/
    @Nullable
    protected PartySticker findHandlingSticker() {
        for (int i = partyStickers.size() - 1; i >= 0; i--) {
            if (isInStickerArea(partyStickers.get(i), downX, downY)) {
                return partyStickers.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull PartySticker partySticker, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return partySticker.contains(tmp);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        if (handlingPartySticker == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        handlingPartySticker.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * calculate Distance in two fingers
     **/
    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        for (int i = 0; i < partyStickers.size(); i++) {
            PartySticker partySticker = partyStickers.get(i);
            if (partySticker != null) {
                transformSticker(partySticker);
            }
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    protected void transformSticker(@Nullable PartySticker partySticker) {
        if (partySticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }

        sizeMatrix.reset();

        float stickerWidth = partySticker.getWidth();
        float stickerHeight = partySticker.getHeight();
        //step 1
        float offsetX = (svWidth - stickerWidth) / 2;
        float offsetY = (svHeight - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (svWidth < svHeight) {
            scaleFactor = svWidth / stickerWidth;
        } else {
            scaleFactor = svHeight / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor, scaleFactor, svWidth / 2f, svHeight / 2f);

        partySticker.getMatrix().reset();
        partySticker.setMatrix(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingPartySticker, direction);
    }

    public void flip(@Nullable PartySticker partySticker, @Flip int direction) {
        if (partySticker != null) {
            partySticker.getCenterPoint(midPoint);
            if ((direction & FLIP_HORIZONTALLY) > 0) {
                partySticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                partySticker.setFlippedHorizontally(!partySticker.isFlippedHorizontally());
            }
            if ((direction & FLIP_VERTICALLY) > 0) {
                partySticker.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                partySticker.setFlippedVertically(!partySticker.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(partySticker);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable PartySticker partySticker) {
        return replace(partySticker, true);
    }

    public boolean replace(@Nullable PartySticker partySticker, boolean needStayState) {
        if (handlingPartySticker != null && partySticker != null) {
            float width = svWidth;
            float height = svHeight;
            if (needStayState) {
                partySticker.setMatrix(handlingPartySticker.getMatrix());
                partySticker.setFlippedVertically(handlingPartySticker.isFlippedVertically());
                partySticker.setFlippedHorizontally(handlingPartySticker.isFlippedHorizontally());
            } else {
                handlingPartySticker.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (width - handlingPartySticker.getWidth()) / 2f;
                float offsetY = (height - handlingPartySticker.getHeight()) / 2f;
                partySticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    scaleFactor = width / handlingPartySticker.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = height / handlingPartySticker.getDrawable().getIntrinsicHeight();
                }
                partySticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = partyStickers.indexOf(handlingPartySticker);
            partyStickers.set(index, partySticker);
            handlingPartySticker = partySticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable PartySticker partySticker) {
        if (partyStickers.contains(partySticker)) {
            partyStickers.remove(partySticker);
            if (onStickerOperationListener != null && partySticker != null)
                onStickerOperationListener.onStickerDeleted(partySticker);
            if (handlingPartySticker == partySticker) {
                handlingPartySticker = null;
            }
            invalidate();

            return true;
        } else {
            Log.d(TAG, "remove: the sticker is not in this StickerView");

            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingPartySticker);
    }

    public void removeAllStickers() {
        partyStickers.clear();
        if (handlingPartySticker != null) {
            handlingPartySticker.release();
            handlingPartySticker = null;
        }
        invalidate();
    }

    @NonNull
    public PartyStickerView addSticker(@NonNull PartySticker partySticker) {
        return addSticker(partySticker, PartySticker.Position.CENTER);
    }

    public PartyStickerView addSticker(@NonNull PartySticker partySticker, int svWidth, int svHeight) {
        this.svWidth = svWidth;
        this.svHeight = svHeight;
        return addSticker(partySticker, PartySticker.Position.CENTER);
    }

    public PartyStickerView addSticker(@NonNull final PartySticker partySticker,
                                       final @PartySticker.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(partySticker, position);
        } else {
            post(() -> addStickerImmediately(partySticker, position));
        }
        return this;
    }

    protected void addStickerImmediately(@NonNull PartySticker partySticker, @PartySticker.Position int position) {
        setStickerPosition(partySticker, position);


        float scaleFactor, widthScaleFactor, heightScaleFactor;

        widthScaleFactor = (float) svWidth / partySticker.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) svHeight / partySticker.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;

        partySticker.getMatrix()
                .postScale(scaleFactor, scaleFactor, svWidth / 2f, svHeight / 2f);

        handlingPartySticker = partySticker;
        partyStickers.add(partySticker);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(partySticker);
        }
        invalidate();
    }

    protected void setStickerPosition(@NonNull PartySticker partySticker, @PartySticker.Position int position) {
        float width = svWidth;
        float height = svHeight;
        float offsetX = width - partySticker.getWidth();
        float offsetY = height - partySticker.getHeight();
        if ((position & PartySticker.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & PartySticker.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & PartySticker.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & PartySticker.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }
        partySticker.getMatrix().postTranslate(offsetX, offsetY);
    }

    @NonNull
    public float[] getStickerPoints(@Nullable PartySticker partySticker) {
        float[] points = new float[8];
        getStickerPoints(partySticker, points);
        return points;
    }

    public void getStickerPoints(@Nullable PartySticker partySticker, @NonNull float[] dst) {
        if (partySticker == null) {
            Arrays.fill(dst, 0);
            return;
        }
        partySticker.getBoundPoints(bounds);
        partySticker.getMappedPoints(dst, bounds);
    }

    public void save(@NonNull File file) {
        try {
            PartyStickerUtils.saveImageToGallery(file, createBitmap());
            PartyStickerUtils.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            //
        }
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        handlingPartySticker = null;
        Bitmap bitmap = Bitmap.createBitmap(svWidth, svHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return partyStickers.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    @NonNull
    public PartyStickerView setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
        return this;
    }

    @NonNull
    public PartyStickerView setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
        return this;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    @NonNull
    public PartyStickerView setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
        return this;
    }

    @NonNull
    public PartyStickerView setOnStickerOperationListener(
            @Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @Nullable
    public PartySticker getCurrentSticker() {
        return handlingPartySticker;
    }

    @NonNull
    public List<PartyBitmapStickerIcon> getIcons() {
        return icons;
    }

    public void setIcons(@NonNull List<PartyBitmapStickerIcon> icons) {
        this.icons.clear();
        this.icons.addAll(icons);
        invalidate();
    }

    public interface OnStickerOperationListener {
        void onStickerAdded(@NonNull PartySticker partySticker);

        void onStickerClicked(@NonNull PartySticker partySticker);

        void onStickerDeleted(@NonNull PartySticker partySticker);

        void onStickerDragFinished(@NonNull PartySticker partySticker);

        void onStickerTouchedDown(@NonNull PartySticker partySticker);

        void onStickerZoomFinished(@NonNull PartySticker partySticker);

        void onStickerFlipped(@NonNull PartySticker partySticker);

        void onStickerDoubleTapped(@NonNull PartySticker partySticker);
    }
}
