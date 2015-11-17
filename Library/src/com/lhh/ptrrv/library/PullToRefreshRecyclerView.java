package com.lhh.ptrrv.library;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.lhh.ptrrv.library.footer.BaseFooter;
import com.lhh.ptrrv.library.footer.LoadMoreFooter;
import com.lhh.ptrrv.library.header.Header;
import com.lhh.ptrrv.library.impl.PrvInterface;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by linhonghong on 2015/11/11.
 */
public class PullToRefreshRecyclerView extends SwipeRefreshLayout implements PrvInterface {

    private static final String TAG = "PTRRV";

    private RecyclerView mRecyclerView;

    private Header mRootHeader;//留白header

    private RelativeLayout mRootRelativeLayout;//主view,包含footer，header等

    private View mHeader;//header

    private View mEmptyView;

    private int mLoadMoreCount = 10;//default = 10

    private int mCurScroll;

    private boolean mIsSwipeEnable = false;

    private Context mContext;

    private BaseFooter mLoadMoreFooter;

//    private RecyclerView.LayoutManager mLayoutManger;

//    private LinearLayoutManager mLinearLayoutManager;

//    private LayoutParams mFullLayoutParams;//全屏型lp

    private PagingableListener mPagingableListener;

//    private int[] mSpanItem;

//    private static final int SPAN_SIZE = 1;

    private AdapterObserver mAdapterObserver;

    private boolean isLoading = false;
    private boolean hasMoreItems = false;

    private PullToRefreshRecyclerView.OnScrollListener mOnScrollLinstener;

    private InterOnScrollListener mInterOnScrollListener;

    public interface PagingableListener{
        void onLoadMoreItems();
    }

    public interface OnScrollListener{
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        //old-method,仿造listview做的,这里应该没什么用处：by linhonghong 2015.10.29
        void onScroll(RecyclerView recyclerView, int firstVisibleItem, int visibleItemCount, int totalItemCount);
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
        mRootRelativeLayout = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.ptrrv_root_view, null);

        this.addView(mRootRelativeLayout);

        this.setColorSchemeResources(R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright,
                R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright);

        //初始化loadmoreview

        mRecyclerView = (RecyclerView)mRootRelativeLayout.findViewById(R.id.recycler_view);

//        mLinearLayoutManager = new LinearLayoutManager(mContext);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
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
//        mSpanItem = new int[SPAN_SIZE];
        //初始化一些七七八八的东西
//        if(mFullLayoutParams == null) {
//            mFullLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        }

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
        mEmptyView = emptyView;
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
    public void addHeaderView(View view) {
        //2015.11.17实现
        if(mHeader != null){
            mRootRelativeLayout.removeView(mHeader);
        }

        mHeader = view;

        if(mHeader == null){
            return;
        }

        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                if(getRecyclerView() == null || mHeader == null){
                    return;
                }
                if(mRootHeader == null){
                    mRootHeader = new Header();
                }
                mRootHeader.setHeight(mHeader.getMeasuredHeight());
                getRecyclerView().removeItemDecoration(mRootHeader);
                getRecyclerView().addItemDecoration(mRootHeader);
                getRecyclerView().getAdapter().notifyDataSetChanged();
            }
        });

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mRootRelativeLayout.addView(mHeader);
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
    public RecyclerView.LayoutManager getLayoutManager() {
        if(mRecyclerView != null) {
            return mRecyclerView.getLayoutManager();
        }
        return null;
    }

    @Override
    public void onFinishLoading(boolean hasMoreItems, boolean needSetSelection) {
        //当一页数据太少的时候，不用显示loadingview
        //临时修改，当一页数据太少的时候，不用显示loadingview
        if(getLayoutManager() == null){
            return;
        }
        if(!hasMoreItems){
            //如果是最后一行
            mCurScroll = mCurScroll - 100;//减去多出的loadmore的高度
        }
        if (getLayoutManager().getItemCount() < mLoadMoreCount)
            hasMoreItems = false;

        setHasMoreItems(hasMoreItems);

        isLoading = false;

        if (needSetSelection) {
            int first = findFirstVisibleItemPosition();
            mRecyclerView.scrollToPosition(--first);
        }
    }

    public int findFirstVisibleItemPosition(){
        if(getLayoutManager() != null) {

            if (getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            }

            if (getLayoutManager() instanceof GridLayoutManager) {
                return ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            }

//            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                return (((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(mSpanItem))[0];
//            }

        }
        return RecyclerView.NO_POSITION;
    }

    public int findLastVisibleItemPosition(){
        if(getLayoutManager() != null) {

            if (getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            }

            if (getLayoutManager() instanceof GridLayoutManager) {
                return ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            }

//            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                return (((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(mSpanItem))[0];
//            }

        }
        return RecyclerView.NO_POSITION;
    }

    public int findFirstCompletelyVisibleItemPosition(){
        if(getLayoutManager() != null) {

            if (getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            }

            if (getLayoutManager() instanceof GridLayoutManager) {
                return ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            }

//            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                return (((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(mSpanItem))[0];
//            }

        }
        return RecyclerView.NO_POSITION;
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

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if(mRecyclerView != null){
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void setLoadMoreCount(int count) {
        mLoadMoreCount = count;
    }

    private void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
        if(mLoadMoreFooter == null){
            mLoadMoreFooter = new LoadMoreFooter(mContext);
        }
        if(!this.hasMoreItems) {
            //remove loadmore
            mRecyclerView.removeItemDecoration(mLoadMoreFooter);
        } else {
            //add loadmore
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
            //在向下回调之前做处理,do before callback
            if(getLayoutManager() == null){
                //here layoutManager is null
                return;
            }

            mCurScroll = dy + mCurScroll;
            if(mHeader != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    mHeader.setTranslationY(-mCurScroll);
                }else {
                    ViewHelper.setTranslationY(mHeader, -mCurScroll);
                }
            }

            int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;
            visibleItemCount = getLayoutManager().getChildCount();
            totalItemCount = getLayoutManager().getItemCount();
            firstVisibleItem = findFirstVisibleItemPosition();
            lastVisibleItem = findLastVisibleItemPosition();//有可能最后一项实在太大了，没办法完全显示
//            lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if(mIsSwipeEnable) {
                if (findFirstCompletelyVisibleItemPosition() != 0) {
                    //这里还有个bug，如果用findFirstCompletelyVisibleItemPosition会产生如果第一条太大，没办法完全显示则无法下啦刷新,但如果不用又会导致没拉到顶就可以下拉
                    //如果不是第一条可见就不让下拉，要不然会出现很严重的到处都能下拉的问题
                    PullToRefreshRecyclerView.this.setEnabled(false);
                } else {
                    PullToRefreshRecyclerView.this.setEnabled(true);
                }
            }

            if(totalItemCount < mLoadMoreCount){
                setHasMoreItems(false);
                isLoading = false;
            }else if (!isLoading && hasMoreItems && ((lastVisibleItem + 1) == totalItemCount)) {
                if (mPagingableListener != null) {
                    isLoading = true;
                    mPagingableListener.onLoadMoreItems();
                }

            }
            //如果totalItemCount = adapter的最大就下拉刷新

            if(mOnScrollLinstener != null){
                mOnScrollLinstener.onScrolled(recyclerView, dx, dy);
                mOnScrollLinstener.onScroll(recyclerView, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

    }

    private class AdapterObserver extends RecyclerView.AdapterDataObserver{
        @Override
        public void onChanged() {
            super.onChanged();
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
