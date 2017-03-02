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

public class IssueDurationsActivity extends Activity {

    private FloatingIssueWidget mFloatingIssueWidget;
    private RecyclerView mItemsRecyclerView;
    private View mBufferZone;
    private IssueDurationAdapter mIssueDurationAdapter;

    private ViewGroup mBottomActionZone;
    private View mBottomMoveZone;
    private View mBottomCopyZone;

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
            showBottomActionZone();
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

                    Point floatingViewCenter = getViewCenterPoint(mFloatingIssueWidget.getView());

                    setAllZonesDefaultState();

                    if (isMoveBottomZone(floatingViewCenter)) onMoveBottomZoneActionUp();
                    else if (isCopyBottomZone(floatingViewCenter)) onCopyBottomZoneActionUp();
                    else if (isListZone(floatingViewCenter)) onIssueDurationRecyclerViewActionUp(floatingViewCenter);
                    else if (isBufferZone(floatingViewCenter)) onBufferZoneActionUp();

                    hideFloatingView();
                    hideBottomActionZone();

                    return true;

                } else return super.dispatchTouchEvent(event);

            case MotionEvent.ACTION_MOVE:
                if (isFloatingViewVisible()) {

                    moveViewTo(mFloatingIssueWidget.getView(), point);
                    Point floatingViewCenter = getViewCenterPoint(mFloatingIssueWidget.getView());

                    setAllZonesDefaultState();

                    if (isMoveBottomZone(floatingViewCenter)) onMoveBottomZoneActionMove();
                    else if (isCopyBottomZone(floatingViewCenter)) onCopyBottomZoneActionMove();
                    else if (isListZone(floatingViewCenter)) onIssueDurationRecyclerViewActionMove(floatingViewCenter);
                    else if (isBufferZone(floatingViewCenter)) onBufferZoneActionMove( );

                    return true;

                } else return super.dispatchTouchEvent(event);

            default:
                return super.dispatchTouchEvent(event);
        }
    }

    private void setAllZonesDefaultState() {
        mBottomMoveZone.setBackgroundResource(R.color.transparent);
        mBottomCopyZone.setBackgroundResource(R.color.transparent);
        mIssueDurationAdapter.setBeforeSelected(-1);
        mBufferZone.setBackgroundResource(R.color.defaultColor);
    }

    private void onIssueDurationRecyclerViewActionUp(Point point) {
        int position = getRecyclerViewItemPosition(mItemsRecyclerView, point);
        mIssueDurationAdapter.setSelected(position);
    }

    private void onIssueDurationRecyclerViewActionMove(Point point) {
        int position = getRecyclerViewItemPosition(mItemsRecyclerView, point);
        mIssueDurationAdapter.setBeforeSelected(position);
    }

    private void onBufferZoneActionUp() {
    }

    private void onBufferZoneActionMove() {
        mBufferZone.setBackgroundResource(R.color.colorAccentLight);
    }

    private void onMoveBottomZoneActionMove() {
        mBottomMoveZone.setBackgroundResource(R.color.colorPrimaryAlpha);
    }

    private void onCopyBottomZoneActionMove() {
        mBottomCopyZone.setBackgroundResource(R.color.colorPrimaryAlpha);
    }

    private void onMoveBottomZoneActionUp() {
    }

    private void onCopyBottomZoneActionUp() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initIssueDurationRecyclerView();
        initFloatingView();
        initBufferZone();
        initBottomActionZone();
    }

    private void initFloatingView() {
        ViewGroup rootContainer = (ViewGroup) findViewById(R.id.root);
        mFloatingIssueWidget = new FloatingIssueWidget(rootContainer);
        rootContainer.addView(mFloatingIssueWidget.getView());
        hideFloatingView();
    }

    private void initIssueDurationRecyclerView() {
        mItemsRecyclerView = (RecyclerView) findViewById(R.id.issue_duration_recycler_view);
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

    private void initBottomActionZone() {
        mBottomActionZone = (ViewGroup) findViewById(R.id.action_bottom_zone);
        mBottomMoveZone = findViewById(R.id.move_action_bottom_zone);
        mBottomCopyZone = findViewById(R.id.copy_action_bottom_zone);
    }

    private void showFloatingView() {
        mFloatingIssueWidget.getView().setVisibility(View.VISIBLE);
    }

    private void hideFloatingView() {
        mFloatingIssueWidget.getView().setVisibility(View.GONE);
    }

    private void showBottomActionZone() {
        mBottomActionZone.setVisibility(View.VISIBLE);
    }

    private void hideBottomActionZone() {
        mBottomActionZone.setVisibility(View.GONE);
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

    private boolean isMoveBottomZone(Point point) {
        return mBottomActionZone.getVisibility() == View.VISIBLE
                && point.y > mBottomActionZone.getY()
                && point.x < mBottomActionZone.getWidth() / 2;
    }

    private boolean isCopyBottomZone(Point point) {
        return mBottomActionZone.getVisibility() == View.VISIBLE
                && point.y > mBottomActionZone.getY()
                && point.x > mBottomActionZone.getWidth() / 2;
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