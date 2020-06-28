package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolMinersData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolPayoutsData;
import com.donuts.bismuth.bismuthtoolbox.R;

import java.text.DecimalFormat;
import java.util.List;

public class MiningPayoutsRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EggpoolPayoutsData> payoutsStatsList;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    MiningPayoutsRecyclerViewAdapter(List<EggpoolPayoutsData> eggpoolPayoutsData) {
        if (eggpoolPayoutsData == null) {
            throw new IllegalArgumentException("payoutsData must not be null");
        }
        payoutsStatsList = eggpoolPayoutsData;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup payoutsViewGroup, int viewType) {
        viewType = TYPE_ITEM; // remove this line if you want to have a header
        if (viewType == TYPE_ITEM) {
            // create a new view
            View payoutsItemView = LayoutInflater.from(payoutsViewGroup.getContext()).
                    inflate(R.layout.rv_container_payouts_item, payoutsViewGroup, false);
            // create ViewHolder
            return new PayoutsItemViewHolder(payoutsItemView);
        }else if(viewType == TYPE_HEADER) {
            // create a new view
            View payoutsItemView= LayoutInflater.from(payoutsViewGroup.getContext()).
                    inflate(R.layout.rv_container_payouts_header, payoutsViewGroup, false);
            // create ViewHolder
            return new PayoutsHeaderViewHolder(payoutsItemView);
        }
        throw new RuntimeException("wrong type " + viewType);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder payoutsViewHolder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        if (payoutsViewHolder instanceof PayoutsItemViewHolder) {
            EggpoolPayoutsData payouts = payoutsStatsList.get(position);// change to <payoutsStatsList.get(position-1)> if you want to have a header
            ((PayoutsItemViewHolder) payoutsViewHolder).datePayoutItem.setText(payouts.getPayoutTime());
            ((PayoutsItemViewHolder) payoutsViewHolder).amountPayoutItem.setText(decimalFormat.format(payouts.getPayoutAmount()));

            //HTML.fromHtml method is depricated in api lvl 24
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((PayoutsItemViewHolder) payoutsViewHolder).txIDpayoutItem.setText(HtmlCompat.fromHtml(payouts.getPayoutTx(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                ((PayoutsItemViewHolder) payoutsViewHolder).txIDpayoutItem.setText(Html.fromHtml(payouts.getPayoutTx()));
            }
            ((PayoutsItemViewHolder) payoutsViewHolder).txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }else if (payoutsViewHolder instanceof PayoutsHeaderViewHolder) {
            EggpoolPayoutsData payouts = payoutsStatsList.get(position);
            ((PayoutsHeaderViewHolder) payoutsViewHolder).datePayoutHeader.setText(payouts.getPayoutTime());
            ((PayoutsHeaderViewHolder) payoutsViewHolder).amountPayoutHeader.setText(decimalFormat.format(payouts.getPayoutAmount()));
            ((PayoutsHeaderViewHolder) payoutsViewHolder).txIDpayoutHeader.setText(String.valueOf(payouts.getPayoutTx()));
        }
    }

    @Override
    public int getItemCount() {
        return payoutsStatsList.size(); // change to <return minersStatsList.size()+1;> if you want to have a header
    }

    public final static class PayoutsItemViewHolder extends RecyclerView.ViewHolder {
        TextView datePayoutItem;
        TextView amountPayoutItem;
        TextView txIDpayoutItem;

        PayoutsItemViewHolder(View itemView) {
            super(itemView);
            datePayoutItem = (TextView) itemView.findViewById(R.id.textView_payoutDate_item);
            amountPayoutItem = (TextView) itemView.findViewById(R.id.textView_payoutAmount_item);
            txIDpayoutItem= (TextView) itemView.findViewById(R.id.textView_payoutTxID_item);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    public final static class PayoutsHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView datePayoutHeader;
        TextView amountPayoutHeader;
        TextView txIDpayoutHeader;

        PayoutsHeaderViewHolder(View itemView) {
            super(itemView);
            datePayoutHeader = (TextView) itemView.findViewById(R.id.textView_payoutDate_header);
            amountPayoutHeader = (TextView) itemView.findViewById(R.id.textView_payoutAmount_header);
            txIDpayoutHeader= (TextView) itemView.findViewById(R.id.textView_payoutTxID_header);
            //txIDpayoutItem.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

