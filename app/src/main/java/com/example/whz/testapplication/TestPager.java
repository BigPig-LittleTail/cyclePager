package com.example.whz.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.List;

public class TestPager extends ViewGroup {
    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    private static final String TAG  = "TestPager";
    private float mDownX;
    private float mMotionX;
    private float mPointerOffset;
    private float mCurPageOffset;
    private float mTotalPagerOffset;
    private int mTouchSlop;
    private int mMaxVelocity;
    private int mRagerOffsetNum;
    private int mActivePointerId;
    private int mPagerCount;
    private int mLeftPos;
    private int mRightPos;
    private boolean mIsOvering;
    private boolean mIsDragging;


    private List<View> mViewList;

    public TestPager(Context context, AttributeSet attr){
        super(context,attr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();


        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);

        mRagerOffsetNum = 0;
        mIsOvering = false;
        mIsDragging = false;
        mPointerOffset = 0;
        mCurPageOffset = 0;
        mTotalPagerOffset = 0;
    }




    public void setViewList(List<View> list){
        if(list == null)
            return;
        if(mViewList == null)
            mViewList = list;
        mPagerCount = mViewList.size();
        if(mPagerCount >= 3){
            for(int i = 0;i<3;i++){
                addView(list.get(i));
            }
            mLeftPos = 0;
            mRightPos = 2;
        }
    }


    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
        else{
            if(mIsOvering){
                mIsDragging = false;
                mIsOvering = false;
            }
        }
    }


    @Override
    public void onMeasure(int withMeasureSpec,int heightMeasureSpec){
        super.onMeasure(withMeasureSpec,heightMeasureSpec);
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
        for(int i = 0;i<getChildCount();i++){
            View child = getChildAt(i);
            child.layout(childLeft + (mRagerOffsetNum + i-1)*with,childTop,(mRagerOffsetNum + i-1)*with+childRight,childBottom);
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
        }


        switch (action){
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG,"I_ACTION_DOWN");
                mActivePointerId = event.getPointerId(0);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownX = event.getX(pointerIndex);
                mTotalPagerOffset = -mRagerOffsetNum*getWidth();
                mCurPageOffset =  mIsDragging ? -(getScrollX() + mTotalPagerOffset):getScrollX() + mTotalPagerOffset;
                mPointerOffset = 0;
                mIsOvering = false;
                if(mIsDragging)
                    mMotionX = mDownX;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG,"I_ACTION_MOVE");
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                float x = event.getX(pointerIndex);
                Log.e(TAG,"x"+x);
                startDrag(x);
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
                mActivePointerId = event.getPointerId(0);
                mTotalPagerOffset = -mRagerOffsetNum*getWidth();
                mCurPageOffset =  mIsDragging ? -(getScrollX() + mTotalPagerOffset):getScrollX() + mTotalPagerOffset;
                mPointerOffset = 0;
                mIsOvering = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "ACTION_MOVE");
                pointIndex = event.findPointerIndex(mActivePointerId);
                if (pointIndex < 0)
                    return false;

                final float x = event.getX(pointIndex);
                Log.e(TAG, "x" + x);
                Log.e(TAG, "mDownX" + mDownX);
                startDrag(x);

                if (mIsDragging) {
                    float oldPointerOffset = mPointerOffset;
                    mPointerOffset = x - mMotionX;
                    mCurPageOffset = mPointerOffset - oldPointerOffset + mCurPageOffset;
                    Log.e(TAG, "oldPointerOffset" + oldPointerOffset);
                    Log.e(TAG, "mCurPageOffset" + mCurPageOffset);
                    Log.e(TAG, "mPointerOffset" + mPointerOffset);

                    if (mCurPageOffset > getWidth()) {
                        pullRightChangeViewOrder();
                        mCurPageOffset -= getWidth();
                        mTotalPagerOffset = -mRagerOffsetNum * getWidth();
                    } else if (mCurPageOffset < -getWidth()) {
                        pullLeftChangeViewOrder();
                        mCurPageOffset += getWidth();
                        mTotalPagerOffset = -mRagerOffsetNum * getWidth();
                    }
                    scrollTo(-(int) (mCurPageOffset + mTotalPagerOffset), 0);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                Log.e(TAG,"ACTION_POINTER_DOWN");
                pointIndex = event.getActionIndex();
                if(pointIndex < 0)
                    return false;
                Log.e(TAG,"mActivePointerId"+mActivePointerId);
                mActivePointerId = event.getPointerId(pointIndex);
                Log.e(TAG,"mActivePointerId"+mActivePointerId);
                mDownX = event.getX(pointIndex);
                mPointerOffset = 0;
                if(mIsDragging)
                    mMotionX = mDownX;
                break;

            }
            case MotionEvent.ACTION_POINTER_UP:{
                Log.e(TAG,"ACTION_POINTER_UP");
                onSecondPointerUp(event);
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "ACTION_UP");
                if(mIsDragging){
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
                    int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);

                    if(Math.abs(mCurPageOffset) > getWidth()/2 || Math.abs(initialVelocity) > 4000){
                        if(mCurPageOffset > getWidth()/2 || initialVelocity > 4000) {
                            pullRightChangeViewOrder();
                            mIsOvering = true;
                            mScroller.startScroll(getScrollX(), 0, -(getWidth() - (int)mCurPageOffset), 0);
                            invalidate();
                        }
                        else{
                            pullLeftChangeViewOrder();
                            mIsOvering = true;
                            mScroller.startScroll(getScrollX(),0,getWidth() + (int)mCurPageOffset,0);
                            invalidate();
                        }
                    }
                    else{
                        mIsOvering = true;
                        mScroller.startScroll(getScrollX(),0,(int)mCurPageOffset,0);
                        invalidate();
                    }
                }
                break;
            }
        }
        return true;
    }

    private void startDrag(float x) {
        if (Math.abs(x - mDownX) > mTouchSlop && !mIsDragging) {
            mMotionX = x - mDownX < 0 ? mDownX - mTouchSlop : mDownX + mTouchSlop;
            mIsDragging = true;
        }
    }

    private void pullLeftChangeViewOrder() {
        mRightPos = (mRightPos + 1) % mPagerCount;
        mLeftPos = (mLeftPos + 1) % mPagerCount;
        View view = mViewList.get(mRightPos);
        removeViewAt(0);
        mRagerOffsetNum++;
        addView(view, 2);
    }

    private void pullRightChangeViewOrder() {
        mRightPos = (mRightPos - 1 + mPagerCount) % mPagerCount;
        mLeftPos = (mLeftPos - 1 + mPagerCount) % mPagerCount;
        View view = mViewList.get(mLeftPos);
        removeViewAt(2);
        mRagerOffsetNum--;
        addView(view, 0);
    }


}
