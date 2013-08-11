package com.suetming.screenrecord;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;

import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.os.CountDownTimer;

import com.suetming.util.Math;

/**
 * Created by SuetMing on 13-7-25.
 */
public class ScreenServer extends Service {
    WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private ImageView safe360;
    private Point szWindow = new Point();
    private GestureDetector gestureDetector;
    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        gestureDetector = new GestureDetector(this,new GestureListener());

        safe360 = new ImageView(this);
        safe360.setImageResource(R.drawable.ic_launcher);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(safe360, params);


        safe360.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        resetPosition();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(safe360, params);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (safe360 != null) windowManager.removeView(safe360);
    }

    private void resetPosition() {
        int w = safe360.getWidth();

        if(params.x == 0 || params.x == szWindow.x - w){

        } else if(params.x + w / 2<= szWindow.x / 2){
            moveToLeft();
        } else if(params.x + w / 2 > szWindow.x / 2){
            moveToRight();
        }
    }

    private void moveToLeft(){
        final int x = params.x;
        new CountDownTimer(500, 5) {
            public void onTick(long t) {
                long step = (500 - t)/5;
                params.x = (int)(double)Math.bounceValue(step,x);
                windowManager.updateViewLayout(safe360, params);
            }
            public void onFinish() {
                params.x = 0;
                windowManager.updateViewLayout(safe360, params);
            }
        }.start();
    }

    private  void moveToRight(){
        final int x = params.x;
        new CountDownTimer(500, 5) {
            public void onTick(long t) {
                long step = (500 - t)/5;
                params.x = szWindow.x + (int)(double)Math.bounceValue(step,x) - safe360.getWidth();
                windowManager.updateViewLayout(safe360, params);
            }
            public void onFinish() {
                params.x = szWindow.x - safe360.getWidth();
                windowManager.updateViewLayout(safe360, params);
            }
        }.start();
    }

    public class GestureListener implements GestureDetector.OnGestureListener{
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return false;
        }
    }
}
