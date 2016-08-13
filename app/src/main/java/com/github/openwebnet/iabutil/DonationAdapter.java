package com.github.openwebnet.iabutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.openwebnet.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationAdapter extends BaseAdapter {

    private final Context context;
    private final List<DonationEntry> donationEntries;

    public DonationAdapter(Context context, List<DonationEntry> donationEntries) {
        this.context = context;
        this.donationEntries = donationEntries;
    }

    @Override
    public int getCount() {
        return donationEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return donationEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        DonationViewHolder holder;
        if (view != null) {
            holder = (DonationViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_donation_item, null);
            //view = LayoutInflater.from(context).inflate(R.layout.dialog_donation_item, parent, false);
            holder = new DonationViewHolder(view);
            view.setTag(holder);
        }

        DonationEntry donationEntry = (DonationEntry) getItem(position);
        holder.name.setText(donationEntry.getName());
        return view;
    }

    /**
     * TODO IabUtil.getInstance().purchase(SKU);
     */
    static class DonationViewHolder {

        @BindView(R.id.textViewDonationName)
        TextView name;

        public DonationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
