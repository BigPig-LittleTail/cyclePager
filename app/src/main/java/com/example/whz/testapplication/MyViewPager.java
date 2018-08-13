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
        mState = State.RESET;
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
                    mTotalOffset -= getWidth();
                    View tempView = getChildAt(0);
                    removeViewAt(0);
                    k++;
                    mState = State.RESET;
                    addView(tempView,2);
                    break;
                }
                case OVER_TO_LEFT_PAGE:{
                    mTotalOffset += getWidth();
                    View tempView = getChildAt(2);
                    removeViewAt(2);
                    k--;
                    mState = State.RESET;
                    addView(tempView,0);
                    break;
                }
                case OVER_RESET:
                    mState = State.RESET;
                    break;
            }
        }
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
        if(mState == State.RESET)
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
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e(TAG,"onTouchEvent");
        int action = event.getActionMasked();
        int pointIndex;

        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
        }


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
                    Log.e(TAG,"mTotalOffset"+mTotalOffset);
                    mState =  x- mDownX < 0 ? State.PULL_LEFT:State.PULL_RIGHT;
                    float over = (x - mMotionX) + mTotalOffset;
                    scrollTo(-(int)over,0);
                    mOffset = (x-mMotionX);
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"ACTION_UP");
                if(mIsDragging){
                    mIsDragging = false;
                    Log.e(TAG,"getWidth"+getWidth());
                    if(Math.abs(mOffset) > getWidth()/2){
                        if(mState == State.PULL_LEFT){
                            mState = State.OVER_TO_RIGHT_PAGE;
                            mScroller.startScroll(getScrollX(),0,getWidth()-(int)Math.abs(mOffset),0);
                        }
                        else {
                            mState = State.OVER_TO_LEFT_PAGE;
                            mScroller.startScroll(getScrollX(),0,-(getWidth()-(int)Math.abs(mOffset)),0);
                        }
                        invalidate();
                    }
                    else{
                        mState = State.OVER_RESET;
                        mScroller.startScroll(getScrollX(),0,(int)mOffset,0);
                        invalidate();
                    }
                }
                return true;
        }
        return true;
    }


}
