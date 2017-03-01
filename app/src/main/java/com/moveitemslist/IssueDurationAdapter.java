package com.moveitemslist;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class IssueDurationAdapter extends RecyclerView.Adapter<IssueDurationViewHolder> {
    private List<Long> items;
    private int selectedPosition = -1;
    private int beforeSelectedPosition = -1;

    private View.OnClickListener mOnClickListener;
    private IssueDurationItemView.OnIssueDurationLongClickListener mOnIssueDurationLongClickListener;

    public IssueDurationAdapter(View.OnClickListener onClickListener, IssueDurationItemView.OnIssueDurationLongClickListener onIssueDurationLongClickListener) {
        mOnClickListener = onClickListener;
        mOnIssueDurationLongClickListener = onIssueDurationLongClickListener;
    }

    @Override
    public IssueDurationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IssueDurationItemView v = new IssueDurationItemView(parent.getContext());

        return new IssueDurationViewHolder(v, mOnClickListener, mOnIssueDurationLongClickListener);
    }

    @Override
    public void onBindViewHolder(IssueDurationViewHolder holder, final int position) {
        IssueDurationItemView issueDurationItemView = (IssueDurationItemView) holder.itemView;

        issueDurationItemView.setContent(String.valueOf(items.get(position)));

        if (position == beforeSelectedPosition) {
            issueDurationItemView.setBeforeSelected();

        } else if (position == selectedPosition) {
            issueDurationItemView.setSelected();
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
        notifyItemChanged(position);
        notifyItemChanged(oldSelectedPosition);
    }

    public void setBeforeSelected(int position) {
        int oldBeforeSelectedPosition = beforeSelectedPosition;
        beforeSelectedPosition = position;
        notifyItemChanged(position);
        notifyItemChanged(oldBeforeSelectedPosition);
    }
}
