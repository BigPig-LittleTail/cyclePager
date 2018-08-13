package com.example.whz.testapplication;

import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;


public class TestPager extends ViewGroup {


    private Scroller mScroller;
    private State mState;

    private static final int WHATEVER_PULL_LEFT = 0;
    private static final int WHATEVER_PULL_RIGHT = 1;
    private static final int NORMAL_PULL_LEFT = 2;
    private static final int NORMAL_PULL_RIGHT = 3;
    private static final int NORMAL_PULL = 4;
    private static final int WHATEVER_PULL = 5;


    private int mLastPullState;
    private int mThisPullState;
    

    public TestPager(Context context, AttributeSet attr){
        super(context,attr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        View view0 = new View(context);
        view0.setBackgroundColor(Color.RED);
        View view1 = new View(context);
        view1.setBackgroundColor(Color.BLACK);
        View view2 = new View(context);
        view2.setBackgroundColor(Color.BLUE);

        addView(view0);
        addView(view1);
        addView(view2);

        mScroller = new Scroller(context);
        mState = State.RESET;

        mLastPullState = WHATEVER_PULL_RIGHT;
    }




    @Override
    public void onMeasure(int withMeasureSpec,int heightMeasureSpec){
        Log.e(TAG,"onMeasure");
        super.onMeasure(withMeasureSpec,heightMeasureSpec);
        if(mState == State.RESET)
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
        if(mState == State.RESET && (mLastPullState == WHATEVER_PULL_RIGHT || mLastPullState == WHATEVER_PULL_LEFT)) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.layout(childLeft + (i - 1) * with, childTop, (i - 1) * with + childRight, childBottom);
                Log.e(TAG, "viewId" + child);
            }
        }
        else if(mState == State.RESET && mLastPullState == NORMAL_PULL_LEFT){
            for(int i = 0;i<getChildCount();i++){
                View child = getChildAt(i);
                if(i < getChildCount() -1)
                    child.layout(childLeft,childTop,childRight,childBottom);
                else
                    child.layout(childLeft+with,childTop,with+childRight,childBottom);
                Log.e(TAG,"view"+child);
            }
        }
        else if(mState == State.RESET && mLastPullState == NORMAL_PULL_RIGHT){
            for(int i = 0;i<getChildCount();i++){
                View child = getChildAt(i);
                if(i != 0)
                    child.layout(childLeft + with,childTop,with+childRight,childBottom);
                else
                    child.layout(childLeft,childTop,with,childBottom);
            }
        }


    }

    private static final String TAG  = "TestPager";

    private float mDownX;
    private float mMotionX;
    private int mTouchSlop;
    private float mOffset;
    private float mTotalOffset = 0;

    private int mActivePointerId;
    private boolean mIsDragging;




    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        Log.e(TAG,"onInterceptTouchEvent");

        int action =  event.getActionMasked();
        int pointerIndex;
        switch (action){
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG,"I_ACTION_DOWN");
                mActivePointerId = event.getPointerId(0);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownX = event.getX(pointerIndex);
                mIsDragging = false;
                mOffset = 0;
                mState = State.RESET;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG,"I_ACTION_MOVE");
                pointerIndex = event.findPointerIndex(mActivePointerId);
                Log.e(TAG,"pointerIndex"+pointerIndex);
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
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mActivePointerId = -1;
                mIsDragging = false;
                break;
        }
        return mIsDragging;
    }

    @Override
    public void dispatchDraw(Canvas canvas){
        if(mLastPullState == NORMAL_PULL_LEFT){
            switch (mState){
                case PULL_LEFT:
                case OVER_TO_RIGHT_PAGE:
                    getChildAt(0).draw(canvas);
                    getChildAt(2).draw(canvas);
                    break;
                case PULL_RIGHT:
                case OVER_TO_LEFT_PAGE:
                    getChildAt(1).draw(canvas);
                    getChildAt(2).draw(canvas);
                    break;
                case RESET:
                    super.dispatchDraw(canvas);
                    break;
            }

            return;
        }

        super.dispatchDraw(canvas);
    }

    @Override
    public void computeScroll(){
        Log.e(TAG,"mState"+mState);
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
        else{
            switch(mState){
                case OVER_TO_RIGHT_PAGE:{
                    mTotalOffset = -getScrollX();
                    if(mThisPullState == NORMAL_PULL_LEFT){
                        //View tempView = getChildAt(0);
                        //removeViewAt(0);
                        mState = State.RESET;
                        mLastPullState = mThisPullState;
                        requestLayout();
                        //addView(tempView,2);
                    }
                    else{
                        mState = State.RESET;
                        mLastPullState = mThisPullState;
                        requestLayout();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e(TAG,"onTouchEvent");
        int action = event.getActionMasked();
        int pointIndex;
        switch (action){
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"ACTION_MOVE");
                pointIndex = event.findPointerIndex(mActivePointerId);
                Log.e(TAG,"mActivePointerId"+mActivePointerId);
                float x = event.getX(pointIndex);
                Log.e(TAG,"x"+x);
                Log.e(TAG,"mDownX"+mDownX);
                if (Math.abs(x - mDownX) > mTouchSlop && !mIsDragging) {
                    mMotionX = x- mDownX < 0 ?mDownX - mTouchSlop : mDownX + mTouchSlop;
                    mIsDragging = true;
                }
                if(mIsDragging){
                    mState =  x- mDownX < 0 ? State.PULL_LEFT:State.PULL_RIGHT;
                    Log.e(TAG,"mMotionX"+mMotionX);
                    Log.e(TAG,"mTotalOffset"+mTotalOffset);
                    float over;
                    if((mState == State.PULL_LEFT && mLastPullState == NORMAL_PULL_LEFT)
                            || (mState == State.PULL_RIGHT && (mLastPullState == WHATEVER_PULL_RIGHT || mLastPullState == WHATEVER_PULL_LEFT))){
                        over = -(x - mMotionX) + mTotalOffset;
                        mOffset = -(x-mMotionX);
                    }
                    else
                    {
                        over = (x - mMotionX) + mTotalOffset;
                        mOffset = (x-mMotionX);
                    }
                    scrollTo(-(int)over,0);
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"ACTION_UP");
                if(mIsDragging){
                    mIsDragging = false;
                    Log.e(TAG,"getWidth"+getWidth());
                    if(Math.abs(mOffset) > getWidth()/2){
                        if(mState == State.PULL_LEFT)
                        {
                            mState = State.OVER_TO_RIGHT_PAGE;
                            if(mLastPullState == NORMAL_PULL_LEFT)
                            {
                                mThisPullState = WHATEVER_PULL_RIGHT;
                            }
                            else
                                mThisPullState = NORMAL_PULL_LEFT;
                            Log.e(TAG,"mLastPullState"+mLastPullState);
                            Log.e(TAG,"mThisPullState"+mThisPullState);
                        }
                        else{
                            mState = State.OVER_TO_LEFT_PAGE;
                            if(mLastPullState == NORMAL_PULL_RIGHT)
                            {
                                mThisPullState = WHATEVER_PULL_LEFT;
                            }
                            else
                                mThisPullState = NORMAL_PULL_RIGHT;
                        }
                        mScroller.startScroll(getScrollX(),0,getWidth()-(int)Math.abs(mOffset),0);
                        invalidate();
                    }
                    else{
                       mState = State.OVER_RESET;
                       if(mOffset < 0)
                       {
                           mScroller.startScroll(getScrollX(),0,-(int)mOffset,0);
                           invalidate();
                       }
                       else{
                           mScroller.startScroll(getScrollX(),0,(int)mOffset,0);
                           invalidate();
                       }
                    }
                    mOffset = 0;
                }
                return true;
        }
        return true;
    }


}

