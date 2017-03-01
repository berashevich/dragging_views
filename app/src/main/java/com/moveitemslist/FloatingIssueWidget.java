package com.moveitemslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FloatingIssueWidget {

    private View mFloatingView;
    private TextView mTextView;

    public FloatingIssueWidget(ViewGroup root) {
        mFloatingView = LayoutInflater.from(root.getContext()).inflate(R.layout.floating_issue_item, root, false);

        initView();
    }

    public View getView() {
        return mFloatingView;
    }

    private void initView() {
        mTextView = (TextView) mFloatingView.findViewById(R.id.floating_text_view);
    }

    public void setContent(String text) {
        mTextView.setText(text);
    }
}
