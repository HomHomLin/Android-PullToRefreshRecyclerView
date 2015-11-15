package com.lhh.ptrrv.library.footer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lhh.ptrrv.library.R;

/**
 * Created by Linhh on 15/11/15.
 */
public class LoadMoreFooter extends BaseFooter {

    private Paint paint;
    private RectF oval;

    private int mCircleSize = 25;

    private int mLoadMorePadding = 100;//给loadmore预留一点空间

    private int mProgress = 30;//圆圈比例

    private int mCircleOffset = 50;

    private Context mContext;

    public LoadMoreFooter(Context context) {
        mContext = context;
        paint = new Paint();
        oval = new RectF();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
//        final int childCount = parent.getChildCount();
//        drawLoadmore(c, parent);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        drawLoadmore(c, parent);
    }

//    final int left = parent.getPaddingLeft() ;
//    final int right = parent.getMeasuredWidth() - parent.getPaddingRight() ;
//    final int childSize = parent.getChildCount() ;
//    for(int i = 0 ; i < childSize ; i ++){
//        final View child = parent.getChildAt( i ) ;
//        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
//        final int top = child.getBottom() + layoutParams.bottomMargin ;
//        final int bottom = top + 100 ;
//        c.drawRect(left,top,right,bottom,mPaint);
//    }


    public void drawLoadmore(Canvas c, RecyclerView parent) {
        //这里的画图简直要画死人(=.=#)
        //以后考虑换成动画形式

        final int left = parent.getPaddingLeft() ;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() ;
        final int childSize = parent.getChildCount() ;
        final View child = parent.getChildAt( childSize - 1 ) ;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int top = child.getBottom() + layoutParams.bottomMargin ;
        final int bottom = top + mLoadMorePadding/2 ;
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
        paint.setStrokeWidth(3);// 再次设置画笔的宽度
        paint.setTextSize(40);// 设置文字的大小
        paint.setColor(Color.BLACK);// 设置画笔颜色
        c.drawText(mContext.getString(R.string.loading), (right - left) / 2, bottom + 10, paint);

    }


    /**
     * 过时方法，但是不得不用
     * @param outRect
     * @param itemPosition
     * @param parent
     */
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if(itemPosition == parent.getAdapter().getItemCount() - 1) {
            outRect.set(0, 0, 0, mLoadMorePadding);
        }
    }
}
