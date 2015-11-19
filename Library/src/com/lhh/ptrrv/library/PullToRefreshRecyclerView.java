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

    //root header
    private Header mRootHeader;

    //main view,contain footer，header etc.
    private RelativeLayout mRootRelativeLayout;

    private View mHeader;//header

    private View mEmptyView;

    //default = 10
    private int mLoadMoreCount = 10;

    private int mCurScroll;

    private boolean mIsSwipeEnable = false;

    private Context mContext;

    private BaseFooter mLoadMoreFooter;

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

        //old-method, like listview 's onScroll ,but it's no use ,right ? by linhonghong 2015.10.29
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
     * Init
     * @param context
     */
    private void setupExtra(Context context){
        mContext = context;
        isLoading = false;
        hasMoreItems = false;
//        mSpanItem = new int[SPAN_SIZE];
        //init something
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
        //2015.11.17 finish method
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

                if (getRecyclerView() == null || mHeader == null) {
                    return;
                }
                if (mRootHeader == null) {
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
        // now is empty, you can do in extra adapter
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
        if(getLayoutManager() == null){
            return;
        }
        if(!hasMoreItems){

            //if it's last line, minus the extra height of loadmore
            mCurScroll = mCurScroll - 100;

        }

        // if items is too short, don't show loadingview
        if (getLayoutManager().getItemCount() < mLoadMoreCount) {

            hasMoreItems = false;

        }

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
        //just like extra setEnable(boolean).but it's more easy to use, like super.setEnable
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

    @Override
    public void release() {

    }

    public void setLoadmoreString(String str){
        if(mLoadMoreFooter != null){
            mLoadMoreFooter.setLoadmoreString(str);
        }
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
            //do super before callback
            if(mOnScrollLinstener != null){
                mOnScrollLinstener.onScrollStateChanged(recyclerView,newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //do super before callback
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
            //sometimes ,the last item is too big so as that the screen cannot show the item fully
            lastVisibleItem = findLastVisibleItemPosition();
//            lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if(mIsSwipeEnable) {
                if (findFirstCompletelyVisibleItemPosition() != 0) {
                    //here has a bug, if the item is too big , use findFirstCompletelyVisibleItemPosition会产生如果第一条太大，没办法完全显示则无法下啦刷新,但如果不用又会导致没拉到顶就可以下拉
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
            //adapter has change
            if(mRecyclerView == null){
                //here must be wrong ,recyclerView is null????
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
