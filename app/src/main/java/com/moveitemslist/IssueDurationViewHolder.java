package com.moveitemslist;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class IssueDurationViewHolder extends RecyclerView.ViewHolder {

    public IssueDurationViewHolder(IssueDurationItemView issueDurationItemView,
                                   View.OnClickListener onClickListener,
                                   IssueDurationItemView.OnIssueDurationLongClickListener onIssueDurationLongClickListener) {
        super(issueDurationItemView);
        issueDurationItemView.setOnClickListener(onClickListener);
        issueDurationItemView.setOnIssueDurationLongClickListener(onIssueDurationLongClickListener);
    }
}
