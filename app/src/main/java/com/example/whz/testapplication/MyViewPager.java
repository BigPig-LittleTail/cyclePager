package com.example.whz.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.security.acl.LastOwnerException;


public class MyViewPager extends ViewGroup {


    private Scroller mScroller;
    private State mState;



    public MyViewPager(Context context, AttributeSet attr){
        super(context,attr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        View mBody;
//        View mLeft;
//        View mRight;
//        mLeft = new View(context);
//        mLeft.setBackgroundColor(Color.RED);
//        mBody = new View(context);
//        mBody.setBackgroundColor(Color.BLACK);
//        mRight = new View(context);
//        mRight.setBackgroundColor(Color.BLUE);
//        addView(mLeft);
//        addView(mBody);
//        addView(mRight);


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

      //  addView(view2copy);
        addView(view0);
        addView(view1);
        addView(view2);
       // addView(view0copy);


        mScroller = new Scroller(context);

        k = 0;
        mOffset = 0;
        mState = State.RESET;
        mIsDragging = false;
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
                case OVER_TO_RIGHT_PAGE:{
                    mIsDragging = false;
                    mOffset = 0;
                    mTotalOffset  = -getScrollX();
                    mState = State.RESET;
                    break;
                }
                case OVER_TO_LEFT_PAGE:{
                    mIsDragging = false;
                    mOffset = 0;
                    mTotalOffset = -getScrollX();
                    mState = State.RESET;
                    break;
                }
                case OVER_RESET:
                    mTotalOffset = -getScrollX();
                    mIsDragging = false;
                    mOffset = 0;
                    mState = State.RESET;
                    break;
            }
        }
    }


    @Override
    public void onMeasure(int withMeasureSpec,int heightMeasureSpec){
        //Log.e(TAG,"onMeasure");
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
        Log.e(TAG,"mState"+mState);
        if(mState == State.RESET || mState == State.OVER_TO_RIGHT_PAGE || mState == State.OVER_TO_LEFT_PAGE || mState == State.OVER_RESET)
            for(int i = 0;i<getChildCount();i++){
                View child = getChildAt(i);
                child.layout(childLeft + (k + i-1)*with,childTop,(k + i-1)*with+childRight,childBottom);
                Log.e(TAG,"viewId"+child);
            }
    }

    private static final String TAG  = "MyViewPager";
    private float mDownX;
    private float mMotionX;
    private int mTouchSlop;
    private float mOffset;
    private float mTotalOffset = 0;
    private int k;
    private int mActivePointerId;
    private boolean mIsDragging;




    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        Log.e(TAG,"onInterceptTouchEvent");

        int action =  event.getActionMasked();
        int pointerIndex;

        Log.e(TAG,"mScroller.isFinished"+mScroller.isFinished());
//        if(!mScroller.isFinished())
//            mScroller.abortAnimation();

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
                mOffset = -getScrollX() + k *getWidth();
                Log.e(TAG,"mOffset"+mOffset);
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
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e(TAG,"onTouchEvent");
        int action = event.getActionMasked();
        int pointIndex;

        Log.e(TAG,"mScroller.isFinished"+mScroller.isFinished());
//        if(!mScroller.isFinished()){
//            mScroller.abortAnimation();
//            mTotalOffset = -getScrollX();
//        }


        switch (action){
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "ACTION_MOVE");
                pointIndex = event.findPointerIndex(mActivePointerId);
                //Log.e(TAG,"mActivePointerId"+mActivePointerId);
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
                    mState = (x - mMotionX) + mOffset < 0 ? State.PULL_LEFT : State.PULL_RIGHT;
                    float over = (x - mMotionX) + mTotalOffset;
                    scrollTo(-(int) over, 0);

                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "ACTION_UP");
                if (mIsDragging) {
                    //Log.e(TAG,"getWidth"+getWidth());
                    pointIndex = event.findPointerIndex(mActivePointerId);
                    float x = event.getX(pointIndex);
                    mOffset =(x - mMotionX) + mOffset;
                    Log.e(TAG,"mOffset"+mOffset);
                    if (Math.abs(mOffset) > getWidth() / 2) {
                        if (mState == State.PULL_LEFT) {
                            View tempView = getChildAt(0);
                            removeViewAt(0);
                            k++;
                            mState = State.OVER_TO_RIGHT_PAGE;
                            addView(tempView,2);
                            mScroller.startScroll(getScrollX(), 0, getWidth() - (int) Math.abs(mOffset), 0);

                        } else {
                            View tempView = getChildAt(2);
                            removeViewAt(2);
                            k--;
                            mState = State.OVER_TO_LEFT_PAGE;
                            addView(tempView,0);
                            mScroller.startScroll(getScrollX(), 0, -(getWidth() - (int) Math.abs(mOffset)), 0);
                        }
                        invalidate();
                    } else {
                        mState = State.OVER_RESET;
                        requestLayout();
                        mScroller.startScroll(getScrollX(), 0, (int) mOffset, 0);
                        invalidate();
                    }
                }
                return true;
            }
        }
        return true;
    }


}
