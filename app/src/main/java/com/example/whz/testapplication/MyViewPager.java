package com.example.whz.testapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Scroller;

import java.util.zip.CheckedOutputStream;

public class MyViewPager extends ViewGroup {


    private Scroller mScroller;
    private State mState;

    private View view0;
    private View view1;
    private View view2;


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

        view0 = new View(context);
        view0.setBackgroundColor(Color.RED);
        view1 = new View(context);
        view1.setBackgroundColor(Color.BLACK);
        view2 = new View(context);
        view2.setBackgroundColor(Color.BLUE);

        addView(view0);
        addView(view1);
        addView(view2);



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
                case OVER_TO_NEXT_PAGER:{
                    mTotalOffset -= getWidth();
                    View tempView = getChildAt(0);
                    removeViewAt(0);
                    k++;
                    mState = State.RESET;
                    addView(tempView,2);
                }
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
                if (x - mDownX > mTouchSlop && !mIsDragging) {
                    mMotionX = mDownX + mTouchSlop;
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
        //Log.e(TAG,"dispatchDraw");
        //super.dispatchDraw(canvas);
        //nextView.draw(canvas);

        super.dispatchDraw(canvas);
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
                    mMotionX = mDownX - mTouchSlop;
                    mIsDragging = true;
                }

                if(mIsDragging){
                    mState = State.PULL_RIGHT;
                    Log.e(TAG,"mMotionX"+mMotionX);
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
                        if(mOffset < 0){
                            mState = State.OVER_TO_NEXT_PAGER;
                            mScroller.startScroll(getScrollX(),0,getWidth()+(int)mOffset,0);
                            invalidate();
                        }
                        else{
//                            View tempView = getChildAt(2);
//                            removeViewAt(2);
//                            addView(tempView,0);
                        }
                    }
                    else{
                        //requestLayout();
                    }
                }
                return true;
        }
        return true;
    }


}
