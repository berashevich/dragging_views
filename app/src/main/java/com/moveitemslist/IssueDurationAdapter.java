package com.moveitemslist;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;

class IssueDurationAdapter extends RecyclerView.Adapter<IssueDurationViewHolder> {
    private List<Long> items;
    private List<Integer> mSelectedPositions = new ArrayList<>();
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

        } else if (mSelectedPositions.contains(position)) {
            issueDurationItemView.setSelected(mSelectedPositions.indexOf(position) + 1);
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

        if (mSelectedPositions.contains(position)) {
            mSelectedPositions.remove(Integer.valueOf(position));

            Stream.of(mSelectedPositions).forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer position) {
                    notifyItemChanged(position);
                }
            });

        } else mSelectedPositions.add(position);

        notifyItemChanged(position);
    }

    public void setBeforeSelected(int position) {
        if (position != beforeSelectedPosition) {
            int oldBeforeSelectedPosition = beforeSelectedPosition;
            beforeSelectedPosition = position;
            notifyItemChanged(position);
            notifyItemChanged(oldBeforeSelectedPosition);
        }
    }
}
