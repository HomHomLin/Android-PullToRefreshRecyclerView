## Update Log
**1.3.0 What's New**

Fix bug

* setEmpty is work now.
* add scroll ect method.

**1.2.0 What's New**

Fix bug

**1.1.0 What's New**

**Two new methods:**

* void removeHeader();

To remove the header of PTRRV.

* void setLoadMoreFooter(BaseLoadMoreView loadMoreFooter);

To add a custom LoadMoreView.

Now you can define a LoadMoreView by extends BaseLoadMoreView and implements it's onDrawLoadMore(Canvas c, RecyclerView parent), and replace the default LoadMoreView by using setLoadMoreFooter(BaseLoadMoreView loadMoreFooter).