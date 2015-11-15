package com.lhh.ptrrv.library.impl;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;

/**
 * Created by Linhh on 15/11/15.
 */
public interface PrvInterface{
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
