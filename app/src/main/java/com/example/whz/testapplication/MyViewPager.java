package com.example.whz.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.security.acl.LastOwnerException;




public class MyViewPager extends ViewGroup {


    public enum  State {
        RESET,PULL_LEFT,PULL_RIGHT,OVER_TO_RIGHT_PAGE,OVER_TO_LEFT_PAGE,OVER_RESET
    }

    private Scroller mScroller;
    private State mState;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;

    private static final String TAG  = "MyViewPager";
    private float mDownX;
    private float mMotionX;
    private int mTouchSlop;
    private float mCurPageOffset;
    private float mTotalOffset = 0;
    private float mPointerOffset;

    private int k;
    private int mActivePointerId;
    private boolean mIsDragging;

    private MyPagerAdapter mAdapter;

    public MyViewPager(Context context, AttributeSet attr){
        super(context,attr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        View view0;
        View view1;
        View view2;
        View view0copy;
        View view2copy;
        view0 = new View(context);
        view0.setBackgroundColor(Color.RED);
        view1 = new View(context);
        view1.setBackgroundColor(Color.BLACK);
        view2 = new View(context);
        view2.setBackgroundColor(Color.BLUE);
        view2copy = new View(context);
        view2copy.setBackgroundColor(Color.BLUE);
        view0copy = new View(context);
        view0copy.setBackgroundColor(Color.RED);
        addView(view0);
        addView(view1);
        addView(view2);

        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);

        k = 0;
        mCurPageOffset = 0;
        mState = State.RESET;
        mIsDragging = false;
    }


    public void setAdapter(MyPagerAdapter pagerAdapter){
        this.mAdapter = pagerAdapter;
    }

    @Override
    public void computeScroll(){
        Log.e(TAG,"mState"+mState);
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
        else{
            Log.e(TAG,"GetScrollX"+getScrollX());
            switch(mState){
                case OVER_TO_RIGHT_PAGE:
                case OVER_TO_LEFT_PAGE:
                case OVER_RESET:
                    mIsDragging = false;
                    mCurPageOffset = 0;
                    mTotalOffset  = -getScrollX();
                    mState = State.RESET;
                    break;
            }
        }
    }


    @Override
    public void onMeasure(int withMeasureSpec,int heightMeasureSpec){
        //Log.e(TAG,"onMeasure");
        super.onMeasure(withMeasureSpec,heightMeasureSpec);
        if(mState == State.RESET || mState == State.OVER_TO_RIGHT_PAGE || mState == State.OVER_TO_LEFT_PAGE || mState == State.OVER_RESET)
        for(int i = 0;i<getChildCount();i++){
            View child = getChildAt(i);
            measureChild(child,
                    MeasureSpec.makeMeasureSpec(withMeasureSpec - getPaddingLeft() - getPaddingRight(),MeasureSpec.EXACTLY)
                    ,MeasureSpec.makeMeasureSpec(heightMeasureSpec-getPaddingTop()- getPaddingBottom(),MeasureSpec.EXACTLY));
        }
    }

    @Override
    public void onLayout(boolean b,int left,int top,int right,int bottom){
        Log.e(TAG,"onLayout");
        int with = getMeasuredWidth();
        int height = getMeasuredHeight();

        final int childLeft = getPaddingLeft();
        final int childRight = with - getPaddingRight();
        final int childBottom = height - getPaddingBottom();
        final int childTop = getPaddingTop();
        Log.e(TAG,"mState"+mState);
        //if(mState == State.RESET || mState == State.OVER_TO_RIGHT_PAGE || mState == State.OVER_TO_LEFT_PAGE || mState == State.OVER_RESET)
            for(int i = 0;i<getChildCount();i++){
                View child = getChildAt(i);
                child.layout(childLeft + (k + i-1)*with,childTop,(k + i-1)*with+childRight,childBottom);
                Log.e(TAG,"viewId"+child);
            }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        Log.e(TAG,"onInterceptTouchEvent");

        int action =  event.getActionMasked();
        int pointerIndex;

        Log.e(TAG,"mScroller.isFinished"+mScroller.isFinished());

        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
            mTotalOffset = -getScrollX();
        }


        switch (action){
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG,"I_ACTION_DOWN");
                mActivePointerId = event.getPointerId(0);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownX = event.getX(pointerIndex);
                //mIsDragging = false;
                mTotalOffset = -getScrollX();
                mCurPageOffset = -getScrollX() + k *getWidth();
                mPointerOffset = 0;
                Log.e(TAG,"mCurPageOffset"+mCurPageOffset);
                mState = State.RESET;
                if(mIsDragging)
                    mMotionX = mDownX;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG,"I_ACTION_MOVE");
                pointerIndex = event.findPointerIndex(mActivePointerId);
                //Log.e(TAG,"pointerIndex"+pointerIndex);
                if (pointerIndex < 0)
                    return false;
                float x = event.getX(pointerIndex);
                Log.e(TAG,"x"+x);
                if (Math.abs(x - mDownX) > mTouchSlop && !mIsDragging) {
                    mMotionX = x- mDownX < 0 ?mDownX - mTouchSlop : mDownX + mTouchSlop;
                    mIsDragging = true;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                pointerIndex = event.getActionIndex();
                if(pointerIndex < 0)
                    return false;
                mActivePointerId = event.getPointerId(pointerIndex);

                mDownX = event.getX(pointerIndex);
                if(mIsDragging)
                    mMotionX = mDownX;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                onSecondPointerUp(event);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mActivePointerId = -1;
                mIsDragging = false;
                break;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        return mIsDragging;
    }

    @Override
    public void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
    }

    private void onSecondPointerUp(MotionEvent event){
        int pointIndex = event.getActionIndex();
        int pointId = event.getPointerId(pointIndex);
        if(pointId == mActivePointerId){
            int nowPointIndex = pointIndex == 0 ? 1:0;
            mActivePointerId = event.getPointerId(nowPointIndex);

            mTotalOffset += mPointerOffset;
            mCurPageOffset += mPointerOffset;
            mPointerOffset = 0;
            mMotionX = mDownX = event.getX(nowPointIndex);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e(TAG,"onTouchEvent");
        int action = event.getActionMasked();
        int pointIndex;

        Log.e(TAG,"mScroller.isFinished"+mScroller.isFinished());

        if(mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "ACTION_MOVE");
                pointIndex = event.findPointerIndex(mActivePointerId);
                //Log.e(TAG,"mActivePointerId"+mActivePointerId);
                if(pointIndex < 0)
                    return false;

                float x = event.getX(pointIndex);
                Log.e(TAG, "x" + x);
                Log.e(TAG, "mDownX" + mDownX);
                if (Math.abs(x - mDownX) > mTouchSlop && !mIsDragging) {
                    mMotionX = x - mDownX < 0 ? mDownX - mTouchSlop : mDownX + mTouchSlop;
                    mIsDragging = true;
                }

                if (mIsDragging) {
                    Log.e(TAG, "mTotalOffset" + mTotalOffset);
                    Log.e(TAG,"mMotionX"+mMotionX);
                    mPointerOffset = (x - mMotionX);
                    mState = mPointerOffset + mCurPageOffset < 0 ? State.PULL_LEFT : State.PULL_RIGHT;
                    float over = (x - mMotionX) + mTotalOffset;
                    scrollTo(-(int) over, 0);
                    if(over + k*getWidth() > getWidth()/2 && over > 0) {
                        View tempView = getChildAt(2);
                        removeViewAt(2);
                        k--;
                        addView(tempView,0);
                    }
                    else if(over + k*getWidth() < -getWidth()/2 && over < 0){
                        View tempView = getChildAt(0);
                        removeViewAt(0);
                        k++;
                        addView(tempView,2);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                Log.e(TAG,"ACTION_POINTER_DOWN");
                pointIndex = event.getActionIndex();
                if(pointIndex < 0)
                    return false;
                mActivePointerId = event.getPointerId(pointIndex);
                
                float x = event.getX(pointIndex);

                mTotalOffset += mPointerOffset;
                mCurPageOffset += mPointerOffset;

                mDownX = mMotionX = x;
                Log.e(TAG, "mTotalOffset" + mTotalOffset);
                Log.e(TAG,"mMotionX"+mMotionX);
                Log.e(TAG,"mCurPageOffset"+mCurPageOffset);
                break;

            }
            case MotionEvent.ACTION_POINTER_UP:{
                Log.e(TAG,"ACTION_POINTER_UP");
                onSecondPointerUp(event);
                Log.e(TAG, "mTotalOffset" + mTotalOffset);
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "ACTION_UP");
                if (mIsDragging) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
                    int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);
                    Log.e(TAG,"initialVelocity"+initialVelocity);

                    pointIndex = event.findPointerIndex(mActivePointerId);
                    float x = event.getX(pointIndex);
                    mCurPageOffset = mPointerOffset + mCurPageOffset;
                    float over = (x - mMotionX) + mTotalOffset;
                    Log.e(TAG,"mCurPageOffset"+mCurPageOffset);
                    if (Math.abs(mCurPageOffset) > getWidth() / 2) {
                        if (mState == State.PULL_LEFT) {

                            mCurPageOffset = over + k*getWidth();
                            mScroller.startScroll(getScrollX(), 0, (int)mCurPageOffset, 0);

                        } else {
//                            View tempView = getChildAt(2);
//                            removeViewAt(2);
//                            k--;
//                            mState = State.OVER_TO_LEFT_PAGE;
//                            addView(tempView,0);
                            mCurPageOffset = over + k*getWidth();
                            mScroller.startScroll(getScrollX(), 0, (int)mCurPageOffset, 0);
                        }
                        invalidate();
                    } else {
                        if(Math.abs(initialVelocity) > 4000){
                            if (mState == State.PULL_LEFT) {
                                View tempView = getChildAt(0);
                                removeViewAt(0);
                                k++;
                                mState = State.OVER_TO_RIGHT_PAGE;
                                addView(tempView,2);
                                mScroller.startScroll(getScrollX(), 0, getWidth() - (int) Math.abs(mCurPageOffset), 0);

                            } else {
                                View tempView = getChildAt(2);
                                removeViewAt(2);
                                k--;
                                mState = State.OVER_TO_LEFT_PAGE;
                                addView(tempView,0);
                                mScroller.startScroll(getScrollX(), 0, -(getWidth() - (int) Math.abs(mCurPageOffset)), 0);
                            }
                            invalidate();
                        }
                        else {
                            mState = State.OVER_RESET;
                            //requestLayout();
                            mScroller.startScroll(getScrollX(), 0, (int) mCurPageOffset, 0);
                            invalidate();
                        }
                    }
                    velocityTracker.clear();
                }
                break;
            }
        }
        return true;
    }


}
