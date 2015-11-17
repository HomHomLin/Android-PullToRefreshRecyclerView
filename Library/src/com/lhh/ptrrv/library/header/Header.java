package com.lhh.ptrrv.library.header;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

/**
 * Created by linhonghong on 2015/11/17.
 */
public class Header extends BaseHeader{

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if(itemPosition == 0) {
            outRect.set(0, mHeaderHeight, 0, 0);
        }
    }
}
