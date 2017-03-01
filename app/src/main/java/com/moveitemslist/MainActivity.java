package com.moveitemslist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private FloatingIssueWidget mFloatingIssueWidget;
    private RecyclerView mItemsRecyclerView;
    private View mBufferZone;
    private IssueDurationAdapter mIssueDurationAdapter;

    private int mTargetListItemPosition = -1;

    private View.OnLongClickListener mOnBufferLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showFloatingView();
            mFloatingIssueWidget.setContent("Item content: buffer");
            return true;
        }
    };

    private IssueDurationItemView.OnIssueDurationLongClickListener mOnIssueDurationLongClickListener = new IssueDurationItemView.OnIssueDurationLongClickListener() {
        @Override
        public void onItemLongClick(String text) {
            showFloatingView();
            mFloatingIssueWidget.setContent("Item content: " + text);
        }
    };

    private View.OnClickListener mOnIssueDurationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mItemsRecyclerView.getChildLayoutPosition(v);
            mIssueDurationAdapter.setSelected(position);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Point point = new Point((int) event.getRawX(), (int) event.getRawY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                moveViewTo(mFloatingIssueWidget.getView(), point);
                return super.dispatchTouchEvent(event);

            case MotionEvent.ACTION_UP:
                if (isFloatingViewVisible()) {

                    hideFloatingView();
                    Point floatingViewCenter = getViewCenterPoint(mFloatingIssueWidget.getView());

                    if (isListZone(floatingViewCenter)) onIssueDurationRecyclerViewActionUp(floatingViewCenter);
                    else if (isBufferZone(floatingViewCenter)) onBufferZoneActionUp(floatingViewCenter);

                    return true;

                } else return super.dispatchTouchEvent(event);

            case MotionEvent.ACTION_MOVE:
                if (isFloatingViewVisible()) {

                    moveViewTo(mFloatingIssueWidget.getView(), point);
                    Point floatingViewCenter = getViewCenterPoint(mFloatingIssueWidget.getView());

                    if (isListZone(floatingViewCenter)) onIssueDurationRecyclerViewActionMove(floatingViewCenter);
                    else if (isBufferZone(floatingViewCenter)) onBufferZoneActionMove(floatingViewCenter);
                    return true;

                } else return super.dispatchTouchEvent(event);

            default:
                return super.dispatchTouchEvent(event);
        }
    }

    private void onIssueDurationRecyclerViewActionUp(Point point) {
        mIssueDurationAdapter.setSelected(getRecyclerViewItemPosition(mItemsRecyclerView, point));
    }

    private void onBufferZoneActionUp(Point point) {
        mBufferZone.setBackgroundResource(R.color.defaultColor);
    }

    private void onIssueDurationRecyclerViewActionMove(Point point) {
        int position = getRecyclerViewItemPosition(mItemsRecyclerView, point);
        if (position != mTargetListItemPosition) {
            mTargetListItemPosition = position;
            mIssueDurationAdapter.setBeforeSelected(mTargetListItemPosition);
        }
    }

    private void onBufferZoneActionMove(Point point) {
        mBufferZone.setBackgroundResource(R.color.colorAccentLight);
        mTargetListItemPosition = -1;
        mIssueDurationAdapter.setBeforeSelected(-1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initFloatingView();
        initIssueDurationRecyclerView();
        initBufferZone();
    }

    private void initFloatingView() {
        ViewGroup rootContainer = (ViewGroup) findViewById(R.id.root);
        mFloatingIssueWidget = new FloatingIssueWidget(rootContainer);
        rootContainer.addView(mFloatingIssueWidget.getView());
        hideFloatingView();
    }

    private void initIssueDurationRecyclerView() {
        mItemsRecyclerView = (RecyclerView) findViewById(R.id.issue_duration_recucler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mItemsRecyclerView.setLayoutManager(layoutManager);
        mIssueDurationAdapter = new IssueDurationAdapter(mOnIssueDurationClickListener, mOnIssueDurationLongClickListener);
        mIssueDurationAdapter.setItems(getFakeItems());
        mItemsRecyclerView.setAdapter(mIssueDurationAdapter);
    }

    private void initBufferZone() {
        mBufferZone = findViewById(R.id.buffer_zone);
        mBufferZone.setOnLongClickListener(mOnBufferLongClickListener);
    }

    private void showFloatingView() {
        mFloatingIssueWidget.getView().setVisibility(View.VISIBLE);
    }

    private void hideFloatingView() {
        mFloatingIssueWidget.getView().setVisibility(View.GONE);
    }

    private boolean isFloatingViewVisible() {
        return mFloatingIssueWidget.getView().getVisibility() == View.VISIBLE;
    }

    private List<Long> getFakeItems() {
        List<Long> items = new ArrayList<>();
        for (long i = 1; i <= 30; i++) {
            items.add(i);
        }
        return items;
    }

    private boolean isBufferZone(Point point) {
        return point.y > mBufferZone.getY();
    }

    private boolean isListZone(Point point) {
        return point.y < mBufferZone.getY();
    }

    // --------------------------------- TODO Utils methods ---------------------------------------

    private void moveViewTo(View view, Point point) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = point.y - layoutParams.height;
        layoutParams.leftMargin = point.x - layoutParams.width;
        view.setLayoutParams(layoutParams);
    }

    private int getRecyclerViewItemPosition(RecyclerView recyclerView, Point point) {
        View itemView = recyclerView.findChildViewUnder(point.x, point.y);
        return recyclerView.getChildAdapterPosition(itemView);
    }

    private Point getViewCenterPoint(View view) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return new Point(
                layoutParams.leftMargin + layoutParams.width  / 2,
                layoutParams.topMargin + layoutParams.height / 2
        );
    }
}