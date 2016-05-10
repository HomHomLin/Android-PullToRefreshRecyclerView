package com.lhh.ptrrv.library.footer.loadmore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lhh.ptrrv.library.R;

/**
 * Created by Linhh on 15/11/15.
 */
public class DefaultLoadMoreView extends BaseLoadMoreView {

    private Paint paint;
    private RectF oval;

    private int mCircleSize = 25;

    private int mProgress = 30;//圆圈比例

    private int mCircleOffset = 70;

    public DefaultLoadMoreView(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        paint = new Paint();
        oval = new RectF();
        setLoadmoreString(context.getString(R.string.loading));
//        mLoadMoreString = context.getString(R.string.loading);
    }

    @Override
    public void onDrawLoadMore(Canvas c, RecyclerView parent) {
        super.onDrawLoadMore(c,parent);
        mProgress = mProgress + 5;
        if(mProgress == 100){
            mProgress = 0;
        }
        final int left = parent.getPaddingLeft() ;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() ;
        final int childSize = parent.getChildCount() ;
        final View child = parent.getChildAt( childSize - 1 ) ;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int top = child.getBottom() + layoutParams.bottomMargin ;
        final int bottom = top + getLoadMorePadding()/2 ;
        paint.setAntiAlias(true);// 抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);// 增强消除锯齿
        paint.setColor(Color.GRAY);// 画笔为灰色
        paint.setStrokeWidth(10);// 画笔宽度
        paint.setStyle(Paint.Style.STROKE);// 中空
        c.drawCircle((right - left) / 2 - mCircleOffset, bottom, mCircleSize, paint);//在中心为（(right - left)/2,bottom）的地方画个半径为mCircleSize的圆，
        paint.setColor(Color.GREEN);// 设置画笔为绿色
        oval.set((right - left) / 2 - mCircleOffset - mCircleSize, bottom - mCircleSize, (right - left) / 2 - mCircleOffset + mCircleSize, bottom + mCircleSize);// 在Circle小于圈圈大小的地方画圆，这样也就保证了半径为mCircleSize
        c.drawArc(oval, -90, ((float) mProgress / 100) * 360, false, paint);// 圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
        paint.reset();// 将画笔重置
        paint.setAntiAlias(true);// 抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);// 增强消除锯齿
        paint.setStrokeWidth(3);// 再次设置画笔的宽度
        paint.setTextSize(40);// 设置文字的大小
        paint.setColor(Color.BLACK);// 设置画笔颜色
        c.drawText(getLoadmoreString(), (right - left) / 2, bottom + 10, paint);
    }

}
