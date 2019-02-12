package com.sj.http_practice;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 标签布局
 *
 * Created by eunice on 2018/12/30.
 */

public class TagLayout extends ViewGroup{

    private ArrayList<Rect> mChildBounds = new ArrayList<>();

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //1:逐个遍历子view 并measure child
        //2:记录下子view 的 所占的矩形大小
        //3:根据计算出的所有子view 的宽高信息，再根据自定义布局的样式决定自己的宽高

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int usedWidth = 0;
        int usedHeight = 0;
        int currentMaxHeight = 0;

        int childCount = this.getChildCount();
        for(int i = 0; i < childCount; i++){
            View child = this.getChildAt(i);

            //1:
            this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //this.measureChildWithMargins(child,
            //        widthMeasureSpec, 0,
            //        heightMeasureSpec, 0);

            int measureChildWidth = child.getMeasuredWidth();
            int measureChildHeight = child.getMeasuredHeight();

            Log.e("===Layout", "childMeasureW:" + measureChildWidth + ", childMeasureHeight:" + measureChildHeight);

            //2:
            Rect childBound = null;
            if(this.mChildBounds.size() <= i){
                childBound = new Rect();
                this.mChildBounds.add(childBound);
            } else {
                childBound = this.mChildBounds.get(i);
            }

            //2.1
            if(usedWidth + measureChildWidth > measureWidth){
                usedWidth = 0;
                currentMaxHeight = Math.max(currentMaxHeight, measureChildHeight);
                usedHeight += currentMaxHeight;

                childBound.set(usedWidth, usedHeight, usedWidth + measureChildWidth, usedHeight + measureChildHeight);

                usedWidth += measureChildWidth;
            } else {
                childBound.set(usedWidth, usedHeight, usedWidth + measureChildWidth, usedHeight + measureChildHeight);

                usedWidth += measureChildWidth;
                currentMaxHeight = Math.max(currentMaxHeight, measureChildHeight);
            }
        }

        //3:
        this.setMeasuredDimension(measureWidth, usedHeight + currentMaxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = this.getChildCount();
        for(int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            Rect childBound = this.mChildBounds.get(i);

            child.layout(childBound.left, childBound.top, childBound.right, childBound.bottom);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Log.e("===Layout", "generateLayoutParams()...");

        return new MarginLayoutParams(this.getContext(), attrs);
    }


    @Override
    public LayoutParams getLayoutParams() {
        Log.e("===Layout", "getLayoutParams()...");

        return super.getLayoutParams();
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        Log.e("===Layout", "addView() params:" + (params == null ? " is null" : " not null"));

        super.addView(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
        Log.e("===Layout", "addViewInLayout()...");

        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }
}
