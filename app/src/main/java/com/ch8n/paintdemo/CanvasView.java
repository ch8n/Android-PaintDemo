package com.ch8n.paintdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Ch810 on 01-01-2017.
 */

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private float posX, posY;
    private static final float TOLERANCE = 5;
    private Context context;
    private float STROKESIZE = 5f;
    private int CURR_COLOR = Color.BLACK;


    private class PathConfig {
        private Path path;
        private Paint paint;

    }

    private ArrayList<PathConfig> pathConfigs = new ArrayList<>();
    private ArrayList<PathConfig> undoPathConfigs = new ArrayList<>();

    public void initPaint(int color, float strokeSize) {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(strokeSize);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint(Color.BLACK, STROKESIZE);
    }

    public void setPaintColor(int color) {
        initPaint(color, STROKESIZE);
        CURR_COLOR=color;
        invalidate();
    }

    public void setStrokeSize(float size) {
        STROKESIZE = size;
        initPaint(CURR_COLOR,STROKESIZE);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < pathConfigs.size(); i++) {
            Path path = pathConfigs.get(i).path;
            Paint paint = pathConfigs.get(i).paint;
            canvas.drawPath(path, paint);
        }

        canvas.drawPath(mPath, mPaint);
    }


    private void onStartTouch(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        posX = x;
        posY = y;

    }

    public void onClickUndo() {
        if (pathConfigs.size() > 0) {
            undoPathConfigs.add(pathConfigs.remove(pathConfigs.size() - 1));
            invalidate();
        }


    }

    public void onClickRedo() {
        if (undoPathConfigs.size() > 0) {
            pathConfigs.add(undoPathConfigs.remove(undoPathConfigs.size() - 1));
            invalidate();
        }

    }

    private void onMoveTouch(float x, float y) {
        float dx = Math.abs(x - posX);
        float dy = Math.abs(y - posY);

        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(posX, posY, (x + posX) / 2, (y + posY) / 2);
            posX = x;
            posY = y;
        }
    }

    public void onCleanCanvas() {
        pathConfigs.clear();
        undoPathConfigs.clear();
        invalidate();
    }

    private void onUpTouch() {

        mPath.lineTo(posX, posY);
        mCanvas.drawPath(mPath, mPaint);
        PathConfig config = new PathConfig();
        config.path = mPath;
        config.paint = mPaint;
        pathConfigs.add(config);
        mPath = new Path();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onMoveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onUpTouch();
                break;
            default:
        }

        return true;

    }
}
