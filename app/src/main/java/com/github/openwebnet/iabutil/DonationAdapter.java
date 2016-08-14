package com.github.openwebnet.iabutil;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.openwebnet.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DonationAdapter extends BaseAdapter {

    private final DialogFragment dialog;
    private final List<DonationEntry> donationEntries;

    public DonationAdapter(DialogFragment dialog, List<DonationEntry> donationEntries) {
        this.dialog = dialog;
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
            view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.dialog_donation_item, null);
            holder = new DonationViewHolder(view);
            view.setTag(holder);
        }

        DonationEntry donationEntry = (DonationEntry) getItem(position);
        holder.name.setText(donationEntry.getName());
        holder.description.setText(donationEntry.getDescription());
        holder.price.setText(donationEntry.getPrice().concat(" ").concat(donationEntry.getCurrencyCode()));
        holder.item.setOnClickListener(v -> {
            IabUtil.getInstance().purchase(donationEntry.getSku());
            dialog.dismiss();
        });
        return view;
    }

    /**
     *
     */
    static class DonationViewHolder {

        @BindView(R.id.linearLayoutDialogDonationItem)
        LinearLayout item;

        @BindView(R.id.textViewDonationName)
        TextView name;

        @BindView(R.id.textViewDonationDescription)
        TextView description;

        @BindView(R.id.textViewDonationPrice)
        TextView price;

        public DonationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
