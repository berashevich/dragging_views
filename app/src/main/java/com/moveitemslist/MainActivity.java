package com.moveitemslist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    View mFloatingView;
    ViewGroup mFloatingViewContainer;
    RecyclerView mRecyclerView;
    View mArea;
    RecyclerView.LayoutManager mLayoutManager;
    MyAdapter mAdapter;
    private static int X_DELTA;
    private static int Y_DELTA;

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        X_DELTA = dpToPx(getApplicationContext(), 100);
        Y_DELTA = dpToPx(getApplicationContext(), 65);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());
                if (!isFloatingViewVisible()) {
                    if (isListZone(point)) {
                        mAdapter.setSelected(getListItemPosition(mLayoutManager, getViewCenterPoint(mFloatingView)));
                    } else if (isBufferZone(point)) {
                        mArea.setBackgroundResource(R.color.selectedColor);
                    }
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());
                createFloatingView(point.x, point.y);
                if (!isFloatingViewVisible()) mFloatingViewContainer.addView(mFloatingView);
                if (isListZone(point)) {
                    mRecyclerView.dispatchTouchEvent(e);

                } else if (isBufferZone(point)) {
                    mArea.dispatchTouchEvent(e);
                }
                super.onLongPress(e);
            }
        });

        mFloatingViewContainer = (ViewGroup) findViewById(R.id.floating_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mArea = findViewById(R.id.area);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();

        List<Long> items = new ArrayList<>();
        for (long i = 1; i <= 30; i++) {
            items.add(i);
        }
        mAdapter.setItems(items);

        mRecyclerView.setAdapter(mAdapter);

        mArea.setOnLongClickListener(mOnAreaLongClickListener);
    }

    private View.OnLongClickListener mOnAreaLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d("TESTING", "areaLongPress");
            mArea.setBackgroundResource(R.color.defaultColor);
            return true;
        }
    };

    private RecyclerView.OnLongClickListener mOnListItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d("TESTING", "listItemLongPress");
            return true;
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        Point point = new Point(x, y);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                createFloatingView(x, y);
                if (isListZone(point)) {
                    return mRecyclerView.dispatchTouchEvent(event);
                } else if (isBufferZone(point)) {
                    return mArea.dispatchTouchEvent(event);
                }

            case MotionEvent.ACTION_UP:
                if (isFloatingViewVisible()) {
                    mFloatingViewContainer.removeView(mFloatingView);
                    if (isListZone(point)) {
                        mHandler.removeCallbacks(mAutoSelectListItemRunnable);
                        mAdapter.setSelected(getListItemPosition(mLayoutManager, getViewCenterPoint(mFloatingView)));
                    } else if (isBufferZone(point)) {
                        mArea.setBackgroundResource(R.color.selectedColor);
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if(isFloatingViewVisible()) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mFloatingView.getLayoutParams();
                    layoutParams.topMargin = (int) event.getRawY() - Y_DELTA;
                    layoutParams.leftMargin = (int) event.getRawX() - X_DELTA;
                    mFloatingView.setLayoutParams(layoutParams);

                    if (isListZone(point)) {
                        int position = getListItemPosition(mLayoutManager, getViewCenterPoint(mFloatingView));

                        mArea.setBackgroundResource(R.color.defaultColor);
                        if (position != mTargetListItemPosition) {
                            Log.d("TESTING", "position " + position);
                            mTargetListItemPosition = position;
                            mAdapter.setBeforeSelected(mTargetListItemPosition);
                            mHandler.removeCallbacks(mAutoSelectListItemRunnable);
                            mHandler.postDelayed(mAutoSelectListItemRunnable, AUTO_SELECTING_TIME);
                        }
                    } else if (isBufferZone(point)) {
                        mTargetListItemPosition = -1;
                        mAdapter.setBeforeSelected(-1);
                        mHandler.removeCallbacks(mAutoSelectListItemRunnable);
                        mArea.setBackgroundResource(R.color.colorAccentLight);
                    }
                    return false;

                } else {
                    if (isBufferZone(point)) {
                        mArea.dispatchTouchEvent(event);
                    } else if (isListZone(point)) {
                        mRecyclerView.dispatchTouchEvent(event);
                    }
                }

            default:
                return false;
        }
    }

    private int mTargetListItemPosition = -1;
    private static final int AUTO_SELECTING_TIME = 1500; //1.5 sec
    private Handler mHandler = new Handler();
    private Runnable mAutoSelectListItemRunnable = new Runnable() {
        @Override
        public void run() {
            mFloatingViewContainer.removeView(mFloatingView);
            mAdapter.setSelected(getListItemPosition(mLayoutManager, getViewCenterPoint(mFloatingView)));
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemHolder> {
        List<Long> items;
        private int selectedPosition = -1;
        private int beforeSelectedPosition = -1;

        public class ItemHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView mTextView;

            public ItemHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mTextView = (TextView) itemView.findViewById(R.id.text);
            }

            public void setSelected() {
                setText("SELECTED!");
                setColor(R.color.selectedColor);
            }

            public void setText(String text) {
                mTextView.setText(text);
            }

            public void setColor(int color) {
                mView.setBackgroundResource(color);
            }
        }

        @Override
        public MyAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            v.setOnLongClickListener(mOnListItemLongClickListener);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ItemHolder holder, final int position) {
            if (position == beforeSelectedPosition) {
                holder.setText(String.valueOf(items.get(position)));
                holder.setColor(R.color.colorAccentLight);

            } else if (position == selectedPosition) {
                holder.setSelected();

            } else {
                holder.setText(String.valueOf(items.get(position)));
                holder.setColor(R.color.white);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(List<Long> items) {
            this.items = items;
        }

        public void setSelected(int position) {
            beforeSelectedPosition = -1;
            int oldSelectedPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldSelectedPosition);
            notifyItemChanged(position);
        }

        public void setBeforeSelected(int position) {
            Log.d("TESTING", "select " + position);
            int oldBeforeSelectedPosition = beforeSelectedPosition;
            beforeSelectedPosition = position;
            notifyItemChanged(oldBeforeSelectedPosition);
            notifyItemChanged(position);
        }
    }

    public int getListItemPosition(RecyclerView.LayoutManager layoutManager, Point point) {
        View itemView = mRecyclerView.findChildViewUnder(point.x, point.y);
        return itemView == null? -1 : mLayoutManager.getPosition(itemView);
    }

    public Point getViewCenterPoint(View view) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return new Point(
                layoutParams.leftMargin + layoutParams.width  / 2,
                layoutParams.topMargin + layoutParams.height / 2
        );
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public boolean isFloatingViewVisible() {
        return mFloatingView != null && mFloatingViewContainer.findViewById(mFloatingView.getId()) != null;
    }

    public void createFloatingView(int x, int y) {
        mFloatingView = LayoutInflater.from(mFloatingViewContainer.getContext()).inflate(R.layout.item_small, mFloatingViewContainer, false);
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) mFloatingView.getLayoutParams();
        lParams.leftMargin = x - X_DELTA;
        lParams.topMargin = y - Y_DELTA;
        mFloatingView.setLayoutParams(lParams);
        mFloatingView.setId(123);
    }

    private boolean isBufferZone(Point point) {
        return point.y > mArea.getY();
    }

    private boolean isListZone(Point point) {
        return point.y < mArea.getY();
    }
}