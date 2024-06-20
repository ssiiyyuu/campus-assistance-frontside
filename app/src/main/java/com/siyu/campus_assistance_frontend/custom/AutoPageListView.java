package com.siyu.campus_assistance_frontend.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.siyu.campus_assistance_frontend.R;
import com.siyu.campus_assistance_frontend.entity.PaginationQuery;
import com.siyu.campus_assistance_frontend.entity.PaginationResult;

import lombok.Getter;

/**
 * 自动分页list view
 */
public class AutoPageListView<T> extends ListView implements AbsListView.OnScrollListener {

    public final static int SMALL_PAGE_SIZE = 5;
    public final static int MEDIUM_PAGE_SIZE = 10;
    public final static int BIG_PAGE_SIZE = 15;

    @Getter
    private PaginationQuery<T> query;

    private View loadingLayoutView;
    private View loadingView;

    private int lastItemIndex;
    private int totalItemCount;

    private InnerCallback innerCallback;

    public AutoPageListView(Context context) {
        super(context);
        init(context);
    }

    public AutoPageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoPageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AutoPageListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        loadingLayoutView = View.inflate(context, R.layout.loading, null);
        loadingView = loadingLayoutView.findViewById(R.id.loading);
        loadingView.setVisibility(View.GONE);
        this.addFooterView(loadingLayoutView);
        this.setOnScrollListener(this);
        initQuery();
    }

    public void changePageSize(int size) {
        query.setPageSize(size);
    }

    private void initQuery() {
        query = new PaginationQuery<>();
        query.setPageSize(SMALL_PAGE_SIZE);
        query.setPageNum(0);
    }


    public void updateQuery(PaginationResult<?> result) {
        if(result.getPageNum() <= result.getTotalPages()) {
            query.setPageNum(Math.toIntExact(result.getPageNum()));
        }
    }

    public PaginationQuery<T> getNextPageQuery() {
        PaginationQuery<T> result = new PaginationQuery<>();
        result.setCondition(query.getCondition());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNum() + 1);
        return result;
    }

    public void clearQueryPageNum() {
        query.setPageNum(0);
    }

    public void setCondition(T condition) {
        query.setCondition(condition);
    }

    public interface InnerCallback {
        void load();
    }

    public void setCallback(InnerCallback callback) {
        innerCallback = callback;
    }

    public void loadFinished() {
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(lastItemIndex == totalItemCount && scrollState == SCROLL_STATE_IDLE && totalItemCount > 1) {
            loadingView.setVisibility(View.VISIBLE);
            innerCallback.load();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastItemIndex = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
}
