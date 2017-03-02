package com.moveitemslist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IssueDurationItemView extends RelativeLayout {

    private ViewGroup mRoot;
    private TextView mTextView;

    private OnIssueDurationLongClickListener mOnIssueDurationLongClickListener;

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnIssueDurationLongClickListener != null) mOnIssueDurationLongClickListener.onItemLongClick(mTextView.getText().toString());
            return true;
        }
    };

    public IssueDurationItemView(Context context) {
        super(context);
        inflate(context, R.layout.item, this);

        initView();
    }

    private void initView() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mTextView = (TextView) findViewById(R.id.text);
        setOnLongClickListener(mOnLongClickListener);
    }

    public void setContent(String text) {
        mTextView.setText(text);
        mRoot.setBackgroundResource(R.color.white);
    }

    public void setSelected(Integer position) {
        mTextView.setText("SELECTED " + position);
        mRoot.setBackgroundResource(R.color.selectedColor);
    }

    public void setBeforeSelected() {
        mRoot.setBackgroundResource(R.color.colorAccentLight);
    }

    public void setOnIssueDurationLongClickListener(OnIssueDurationLongClickListener onIssueDurationLongClickListener) {
        mOnIssueDurationLongClickListener = onIssueDurationLongClickListener;
    }

    interface OnIssueDurationLongClickListener {
        void onItemLongClick(String text);
    }
}
