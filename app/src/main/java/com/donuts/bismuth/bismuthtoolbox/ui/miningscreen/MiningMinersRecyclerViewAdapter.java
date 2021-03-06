package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolMinersData;
import com.donuts.bismuth.bismuthtoolbox.R;

import java.util.List;

public class MiningMinersRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EggpoolMinersData> minersStatsList;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    MiningMinersRecyclerViewAdapter(List<EggpoolMinersData> eggpoolMinersData) {
        if (eggpoolMinersData == null) {
            throw new IllegalArgumentException("minersData must not be null");
        }
        minersStatsList = eggpoolMinersData;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup minersViewGroup, int viewType) {
        viewType = TYPE_ITEM; // remove this line if you want to have a header
        if (viewType == TYPE_ITEM) {
            // create a new view
            View minersItemView = LayoutInflater.from(minersViewGroup.getContext()).
                    inflate(R.layout.rv_container_miners_item, minersViewGroup, false);
            // create ViewHolder
            return new MinersItemViewHolder(minersItemView);
        }else if(viewType == TYPE_HEADER) {
            // create a new view
            View minersItemView= LayoutInflater.from(minersViewGroup.getContext()).
                    inflate(R.layout.rv_container_miners_header, minersViewGroup, false);
            // create ViewHolder
            return new MinersHeaderViewHolder(minersItemView);
        }
        throw new RuntimeException("wrong type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder minersViewHolder, int position) {
        if (minersViewHolder instanceof MinersItemViewHolder) {
            EggpoolMinersData miners = minersStatsList.get(position);// change to <minersStatsList.get(position-1)> if you want to have a header
            ((MinersItemViewHolder) minersViewHolder).workerNameTextViewItem.setText(miners.getMinerName());
            ((MinersItemViewHolder) minersViewHolder).hashRateTextViewItem.setText(String.valueOf(miners.getHashrateCurrent()));
            ((MinersItemViewHolder) minersViewHolder).lastSeenTextViewItem.setText(String.valueOf((System.currentTimeMillis() - miners.getLastSeen() * 1000) / 60000) + " min ago");
        }else if (minersViewHolder instanceof MinersHeaderViewHolder) {
            EggpoolMinersData miners = minersStatsList.get(position);
            ((MinersHeaderViewHolder) minersViewHolder).workerNameTextViewHeader.setText(miners.getMinerName());
            ((MinersHeaderViewHolder) minersViewHolder).hashRateTextViewHeader.setText(String.valueOf(miners.getHashrateCurrent()));
            ((MinersHeaderViewHolder) minersViewHolder).lastSeenTextViewHeader.setText(String.valueOf((System.currentTimeMillis() - miners.getLastSeen() * 1000) / 60000) + " min ago");
        }
    }

    @Override
    public int getItemCount() {
        return minersStatsList.size(); // change to <return minersStatsList.size()+1;> if you want to have a header
    }

    public final static class MinersItemViewHolder extends RecyclerView.ViewHolder {
        TextView workerNameTextViewItem;
        TextView hashRateTextViewItem;
        TextView lastSeenTextViewItem;

        MinersItemViewHolder(View itemView) {
            super(itemView);
            workerNameTextViewItem = itemView.findViewById(R.id.textView_workerName_item);
            hashRateTextViewItem = itemView.findViewById(R.id.textView_hashRate_item);
            lastSeenTextViewItem = itemView.findViewById(R.id.textView_lastSeen_item);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    public final static class MinersHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView workerNameTextViewHeader;
        TextView hashRateTextViewHeader;
        TextView lastSeenTextViewHeader;

        MinersHeaderViewHolder(View itemView) {
            super(itemView);
            workerNameTextViewHeader = itemView.findViewById(R.id.textView_workerName_header);
            hashRateTextViewHeader = itemView.findViewById(R.id.textView_hashRate_header);
            lastSeenTextViewHeader = itemView.findViewById(R.id.textView_lastSeen_header);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

