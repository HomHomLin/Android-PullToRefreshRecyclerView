package com.lhh.ptrrv.library.footer.loadmore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;

import com.lhh.ptrrv.library.util.PullToRefreshRecyclerViewUtil;

/**
 * Created by Linhh on 15/11/15.
 */
public class BaseLoadMoreView extends RecyclerView.ItemDecoration {

    protected RecyclerView mRecyclerView;
    protected String mLoadMoreString;
    protected static final int MSG_INVILIDATE = 1;
    protected long mUpdateTime = 150;
    protected PullToRefreshRecyclerViewUtil mPtrrvUtil;
    protected int mLoadMorePadding = 100;
    protected OnDrawListener mOnDrawListener;

    public BaseLoadMoreView(Context context, RecyclerView recyclerView){
        mRecyclerView = recyclerView;
        mPtrrvUtil = new PullToRefreshRecyclerViewUtil();
    }

    public interface OnDrawListener{
        public boolean onDrawLoadMore(Canvas c, RecyclerView parent);
    }

    public void setLoadmoreString(String str) {
        mLoadMoreString = str;
    }

    public String getLoadmoreString(){
        return mLoadMoreString;
    }

    public int getLoadMorePadding(){
        return mLoadMorePadding;
    }

    public void setLoadMorePadding(int padding){
        mLoadMorePadding = padding;
    }

    protected Handler mInvalidateHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
                return;
            }
            int lastItemPosition = mRecyclerView.getAdapter().getItemCount() - 1;
            if (mPtrrvUtil.findLastVisibleItemPosition(mRecyclerView.getLayoutManager()) == lastItemPosition) {

                //when the item is visiable do this method
//                    View view = mRecyclerView.getLayoutManager().findViewByPosition(lastItemPosition);
//                    mInvilidateRect.set(0, 0, view.getRight() - view.getLeft(), view.getBottom() - view.getTop());
                mRecyclerView.invalidate();

            }
        }
    };


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        mInvalidateHanlder.removeMessages(MSG_INVILIDATE);
        onDrawLoadMore(c, parent);
        mInvalidateHanlder.sendEmptyMessageDelayed(MSG_INVILIDATE, mUpdateTime);
    }

    /**
     * @param outRect
     * @param itemPosition
     * @param parent
     */
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if(itemPosition == parent.getAdapter().getItemCount() - 1) {
            outRect.set(0, 0, 0, getLoadMorePadding());
        }
    }

    protected void onDrawLoadMore(Canvas c, RecyclerView parent){
        if(mOnDrawListener != null){
            if(mOnDrawListener.onDrawLoadMore(c,parent)){
                return;
            }
        }
    }

    public void setOnDrawListener(OnDrawListener listener){
        mOnDrawListener = listener;
    }

    public void release(){
        mRecyclerView = null;
    }
}
