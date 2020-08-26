package com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen;

import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.donuts.bismuth.bismuthtoolbox.Data.AllHypernodesData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolPayoutsData;
import com.donuts.bismuth.bismuthtoolbox.R;

import java.text.DecimalFormat;
import java.util.List;

public class MyHypernodesRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AllHypernodesData> allHypernodesDataList;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    MyHypernodesRecyclerViewAdapter(List<AllHypernodesData> allHypernodesData) {
        if (allHypernodesData == null) {
            throw new IllegalArgumentException("AllHypernodesData must not be null");
        }
        allHypernodesDataList = allHypernodesData;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup myHypernodeDataViewGroup, int viewType) {
        viewType = TYPE_ITEM; // remove this line if you want to have a header
        if (viewType == TYPE_ITEM) {
            // create a new view
            View myHypernodesItemView = LayoutInflater.from(myHypernodeDataViewGroup.getContext()).
                    inflate(R.layout.rv_container_my_hypernodes_item, myHypernodeDataViewGroup, false);
            // create ViewHolder
            return new MyHypernodesItemViewHolder(myHypernodesItemView);
        }else if(viewType == TYPE_HEADER) {
            // create a new view
            View myHypernodesItemView = LayoutInflater.from(myHypernodeDataViewGroup.getContext()).
                    inflate(R.layout.rv_container_my_hypernodes_header, myHypernodeDataViewGroup, false);
            // create ViewHolder
            return new MyHypernodesHeaderViewHolder(myHypernodesItemView);
        }
        throw new RuntimeException("wrong type " + viewType);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myHypernodesViewHolder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        if (myHypernodesViewHolder instanceof MyHypernodesItemViewHolder) {
            AllHypernodesData data = allHypernodesDataList.get(position);// change to <allHypernodesDataList.get(position-1)> if you want to have a header
            ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeIpAddressItem.setText(data.getHypernodeIP());
            ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeStatusItem.setText(data.getHypernodeStatus());
            ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeBlockHeightItem.setText(String.valueOf(data.getHypernodeBlockHeight()));

            ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeRewardAddressItem.setText(data.getHypernodeRewardAddress());

            //HTML.fromHtml method is deprecated in api lvl 24
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeRewardAddressItem.setText(HtmlCompat.fromHtml(data.getHypernodeRewardAddress(), HtmlCompat.FROM_HTML_MODE_LEGACY));
//            } else {
//                ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeRewardAddressItem.setText(Html.fromHtml(data.getHypernodeRewardAddress()));
//            }
//            ((MyHypernodesItemViewHolder) myHypernodesViewHolder).myHypernodeRewardAddressItem.setMovementMethod(LinkMovementMethod.getInstance());
        }else if (myHypernodesViewHolder instanceof MyHypernodesHeaderViewHolder) {
            AllHypernodesData data = allHypernodesDataList.get(position);
            ((MyHypernodesHeaderViewHolder) myHypernodesViewHolder).myHypernodeIpAddressHeader.setText(data.getHypernodeIP());
            ((MyHypernodesHeaderViewHolder) myHypernodesViewHolder).myHypernodeStatusHeader.setText(data.getHypernodeStatus());
            ((MyHypernodesHeaderViewHolder) myHypernodesViewHolder).myHypernodeBlockHeightHeader.setText(String.valueOf(data.getHypernodeBlockHeight()));
            ((MyHypernodesHeaderViewHolder) myHypernodesViewHolder).myHypernodeRewardAddressHeader.setText(data.getHypernodeRewardAddress());
        }
    }

    @Override
    public int getItemCount() {
        return allHypernodesDataList.size(); // change to <return minersStatsList.size()+1;> if you want to have a header
    }

    public final static class MyHypernodesItemViewHolder extends RecyclerView.ViewHolder {
        TextView myHypernodeIpAddressItem;
        TextView myHypernodeRewardAddressItem;
        TextView myHypernodeStatusItem;
        TextView myHypernodeBlockHeightItem;

        MyHypernodesItemViewHolder(View itemView) {
            super(itemView);
            myHypernodeIpAddressItem = (TextView) itemView.findViewById(R.id.textView_my_hypernode_ip_item);
            myHypernodeRewardAddressItem = (TextView) itemView.findViewById(R.id.textView_my_hypernode_reward_address_item);
            myHypernodeStatusItem = (TextView) itemView.findViewById(R.id.textView_my_hypernode_status_item);
            myHypernodeBlockHeightItem = (TextView) itemView.findViewById(R.id.textView_my_hypernode_block_height_item);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    public final static class MyHypernodesHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView myHypernodeIpAddressHeader;
        TextView myHypernodeRewardAddressHeader;
        TextView myHypernodeStatusHeader;
        TextView myHypernodeBlockHeightHeader;

        MyHypernodesHeaderViewHolder(View itemView) {
            super(itemView);
            myHypernodeIpAddressHeader = (TextView) itemView.findViewById(R.id.textView_my_hypernode_ip_header);
            myHypernodeRewardAddressHeader = (TextView) itemView.findViewById(R.id.textView_my_hypernode_reward_address_header);
            myHypernodeStatusHeader = (TextView) itemView.findViewById(R.id.textView_my_hypernode_status_header);
            myHypernodeBlockHeightHeader = (TextView) itemView.findViewById(R.id.textView_my_hypernode_block_height_header);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

