package com.lhh.ptrrv.library.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by linhonghong on 2015/11/19.
 */
public class PullToRefreshRecyclerViewUtil {

    public int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager){
        if(layoutManager != null) {

            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

        }
        return RecyclerView.NO_POSITION;
    }

    public int findFirstCompletelyVisibleItemPosition(RecyclerView.LayoutManager layoutManager){
        if(layoutManager != null) {

            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }

            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }

        }
        return RecyclerView.NO_POSITION;
    }

    public int findFirstVisibleItemPosition(RecyclerView.LayoutManager layoutManager){
        if(layoutManager != null) {

            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }

            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }

        }
        return RecyclerView.NO_POSITION;
    }
}
