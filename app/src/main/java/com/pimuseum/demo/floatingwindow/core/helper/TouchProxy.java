package com.pimuseum.demo.floatingwindow.core.helper;

import android.view.MotionEvent;
import android.view.View;

/**
 * touch 事件代理 解决点击和触摸事件的冲突
 */
public class TouchProxy {
    private static final int MIN_DISTANCE_MOVE = 4;
    private static final int MIN_TAP_TIME = 1000;

    private OnTouchEventListener mEventListener;
    private int mLastX;
    private int mLastY;
    private int mStartX;
    private int mStartY;

    private TouchState mState = TouchState.STATE_STOP;

    public TouchProxy(OnTouchEventListener eventListener) {
        mEventListener = eventListener;
    }

    private enum TouchState {
        STATE_MOVE,
        STATE_STOP
    }

    private long lastActionDonm = 0;

    public boolean onTouchEvent(View v, MotionEvent event) {
        int distance = ScreenUtils.dp2px(v.getContext(), 1) * MIN_DISTANCE_MOVE;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mStartX = x;
                mStartY = y;
                mLastY = y;
                mLastX = x;
                if (mEventListener != null) {
                    mEventListener.onDown(x, y);
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(x - mStartX) < distance
                        && Math.abs(y - mStartY) < distance) {
                    if (mState == TouchState.STATE_STOP) {
                        break;
                    }
                } else if (mState != TouchState.STATE_MOVE) {
                    mState = TouchState.STATE_MOVE;
                }
                if (mEventListener != null) {
                    mEventListener.onMove(mLastX, mLastY, x - mLastX, y - mLastY);
                }
                mLastY = y;
                mLastX = x;
                mState = TouchState.STATE_MOVE;
            }
            break;
            case MotionEvent.ACTION_UP : {
                if (mEventListener != null) {
                    mEventListener.onUp(x, y);
                }
                if (mState != TouchState.STATE_MOVE
                        && event.getEventTime() - event.getDownTime() < MIN_TAP_TIME) {
                    v.performClick();

                    //防止多次快速区域点击
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastActionDonm > 500) {
                        mEventListener.onAreaClick((int)event.getX(),(int)event.getY());
                        lastActionDonm = currentTimeMillis;
                    }
                }
                mState = TouchState.STATE_STOP;
            }
            break;
            default:
                break;
        }
        return true;
    }


    public interface OnTouchEventListener {
        void onMove(int x, int y, int dx, int dy);

        void onUp(int x, int y);

        void onDown(int x, int y);

        void onAreaClick(int xInParent, int yInParent);
    }
}
