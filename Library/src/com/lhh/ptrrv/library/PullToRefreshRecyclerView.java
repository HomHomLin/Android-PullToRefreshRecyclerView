package com.lhh.ptrrv.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by linhonghong on 2015/11/11.
 */
public class PullToRefreshRecyclerView extends SwipeRefreshLayout implements PrvInterface{

    private static final String TAG = "PTRRV";

    private RecyclerView mRecyclerView;

    private View mRootRelativeLayout;//主view,包含footer，header等

//    private RelativeLayout mEmptyViewRelativeLayout;//内嵌view，包含recyclerview和emptyview等七七八八的东西

    private View mEmptyView;

//    private View mHeaderView;

    private boolean mIsSwipeEnable = false;

//    private View mFooterView;

//    private View mLoadMoreView;//加载更多的view

//    private NestedScrollView mNestedScrollView;

    private Context mContext;

    private LoadMoreFooter mLoadMoreFooter;

    private LinearLayoutManager mLinearLayoutManager;

    private LayoutParams mFullLayoutParams;//全屏型lp

    private PagingableListener mPagingableListener;

    private AdapterObserver mAdapterObserver;

    private boolean isLoading = false;
    private boolean hasMoreItems = false;

    private PullToRefreshRecyclerView.OnScrollListener mOnScrollLinstener;

    private InterOnScrollListener mInterOnScrollListener;

    public interface PagingableListener{
        public void onLoadMoreItems();
    }

    public interface OnScrollListener{
        public void onScrollStateChanged(RecyclerView recyclerView, int newState);
        public void onScrolled(RecyclerView recyclerView, int dx, int dy);

        //old-method,仿造listview做的,这里应该没什么用处：by linhonghong 2015.10.29
        public void onScroll(RecyclerView recyclerView, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public PullToRefreshRecyclerView(Context context) {
        super(context);
        this.setup(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup(context);
    }

    public void release(){

    }

    /**
     * 全局入口
     */
    private void setup(Context context){
        setupExtra(context);
        initView();
        setLinster();
    }

    private void initView(){
        //初始化布局
        mRootRelativeLayout = LayoutInflater.from(mContext).inflate(R.layout.ptrrv_root_view, null);

        this.addView(mRootRelativeLayout, mFullLayoutParams);

        this.setColorSchemeResources(R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright,
                R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright);

        //初始化loadmoreview

//        mNestedScrollView = (NestedScrollView)mRootRelativeLayout.findViewById(R.id.nsv);
//
//        mEmptyViewRelativeLayout = (RelativeLayout)mRootRelativeLayout.findViewById(R.id.rlEmpty);

        mRecyclerView = (RecyclerView)mRootRelativeLayout.findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if(!mIsSwipeEnable) {
            this.setEnabled(false);
        }

    }

    /**
     * 初始化七七八八的东西
     * @param context
     */
    private void setupExtra(Context context){
        mContext = context;
        isLoading = false;
        hasMoreItems = false;
        //初始化一些七七八八的东西
        if(mFullLayoutParams == null) {
            mFullLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        }

    }

    private void setLinster(){
        mInterOnScrollListener = new InterOnScrollListener();
        mRecyclerView.addOnScrollListener(mInterOnScrollListener);
    }

    @Override
    public void setOnRefreshComplete() {
        this.setRefreshing(false);
    }

    @Override
    public void setOnLoadMoreComplete() {
        setHasMoreItems(false);
    }

    @Override
    public void setPagingableListener(PullToRefreshRecyclerView.PagingableListener pagingableListener) {
        mPagingableListener = pagingableListener;
    }

    @Override
    public void setEmptyView(View emptyView) {
//        if(mEmptyView != null) {
//            //先把它移出去
//            mEmptyViewRelativeLayout.removeView(mEmptyView);
//        }
        mEmptyView = emptyView;
//        if(mEmptyView != null) {
//            mEmptyViewRelativeLayout.addView(mEmptyView, mFullLayoutParams);
//        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        if(mAdapterObserver == null){
            mAdapterObserver = new AdapterObserver();
        }
        if(adapter != null){
            adapter.registerAdapterDataObserver(mAdapterObserver);
            mAdapterObserver.onChanged();
        }
    }

    @Override
    public void setHeader(View view) {
        //空实现，我不做处理这里。请在外部adapter做处理
    }

    @Override
    public void setFooter(View view) {
        //空实现，我不做处理这里。请在外部adapter做处理
    }

    @Override
    public void addOnScrollListener(PullToRefreshRecyclerView.OnScrollListener onScrollLinstener) {
        mOnScrollLinstener = onScrollLinstener;
    }

    @Override
    public LinearLayoutManager getLinearLayoutManager() {
        return mLinearLayoutManager;
    }

    @Override
    public void onFinishLoading(boolean hasMoreItems, boolean needSetSelection) {
        //当一页数据太少的时候，不用显示loadingview
        //临时修改，当一页数据太少的时候，不用显示loadingview
        if(mLinearLayoutManager == null){
            return;
        }
        if (mLinearLayoutManager.getItemCount() < 10)
            hasMoreItems = false;

        setHasMoreItems(hasMoreItems);

        isLoading = false;

        if (needSetSelection) {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            mRecyclerView.scrollToPosition(--first);
        }
    }

    @Override
    public void setSwipeEnable(boolean enable) {
        //just like extra setEnable(boolean).这里和外部一样的，不过这里控制更方便super.setenable
        mIsSwipeEnable = enable;
        this.setEnabled(mIsSwipeEnable);
    }

    @Override
    public boolean isSwipeEnable() {
        return mIsSwipeEnable;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    private void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
        if(mLoadMoreFooter == null){
            mLoadMoreFooter = new LoadMoreFooter(mContext);
        }
        if(!this.hasMoreItems) {
            Log.i(TAG, "remove loadmore");
            mRecyclerView.removeItemDecoration(mLoadMoreFooter);
        } else {
            Log.i(TAG,"add loadmore");
            mRecyclerView.removeItemDecoration(mLoadMoreFooter);
            mRecyclerView.addItemDecoration(mLoadMoreFooter);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private class InterOnScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //在向下回调之前做super处理
            if(mOnScrollLinstener != null){
                mOnScrollLinstener.onScrollStateChanged(recyclerView,newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在向下回调之前做处理

            int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;
            visibleItemCount = mLinearLayoutManager.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if(mIsSwipeEnable) {
                if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    //如果不是第一条可见就不让下拉，要不然会出现很严重的到处都能下拉的问题
                    PullToRefreshRecyclerView.this.setEnabled(false);
                } else {
                    PullToRefreshRecyclerView.this.setEnabled(true);
                }
            }

            if(totalItemCount < 10){
                setHasMoreItems(false);
                isLoading = false;
            }else if (!isLoading && hasMoreItems && ((lastVisibleItem + 1) == totalItemCount)) {
                Log.i(TAG,"loadmore");
                if (mPagingableListener != null) {
                    isLoading = true;
                    mPagingableListener.onLoadMoreItems();
                }

            }
            //如果totalItemCount = adapter的最大就下拉刷新

            if(mOnScrollLinstener != null){
                mOnScrollLinstener.onScrolled(recyclerView,dx,dy);
                mOnScrollLinstener.onScroll(recyclerView, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

    }

    private class AdapterObserver extends RecyclerView.AdapterDataObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            Log.d("recycler","onchanged");
            //数据变化,外部对adapter进行了数据刷新会直接调用到这里的代码
            if(mRecyclerView == null){
                //这里出错了，rv怎么可能是空的？？？
                return;
            }

            //判断并显示emptyview
            RecyclerView.Adapter<?> adapter =  mRecyclerView.getAdapter();
            if(adapter != null && mEmptyView != null) {
                if(adapter.getItemCount() == 0) {
                    if(mIsSwipeEnable) {
                        PullToRefreshRecyclerView.this.setEnabled(false);
                    }
                    mEmptyView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    if(mIsSwipeEnable) {
                        PullToRefreshRecyclerView.this.setEnabled(true);
                    }
                    mEmptyView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}

class LoadMoreFooter extends RecyclerView.ItemDecoration {

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

interface PrvInterface{
    public void setOnRefreshComplete();
    public void setOnLoadMoreComplete();//onFinishLoading,加载更多完成
    public void setPagingableListener(PullToRefreshRecyclerView.PagingableListener pagingableListener);
    public void setEmptyView(View emptyView);
    public void setAdapter(RecyclerView.Adapter adapter);
    public void setHeader(View view);
    public void setFooter(View view);
    public void addOnScrollListener(PullToRefreshRecyclerView.OnScrollListener onScrollLinstener);
    public LinearLayoutManager getLinearLayoutManager();
    public void onFinishLoading(boolean hasMoreItems, boolean needSetSelection);
    public void setSwipeEnable(boolean enable);//设置是否可以下拉
    public boolean isSwipeEnable();//返回当前组件是否可以下拉
    public RecyclerView getRecyclerView();
    public void release();
}
