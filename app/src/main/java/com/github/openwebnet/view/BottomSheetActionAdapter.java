package com.github.openwebnet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.openwebnet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BottomSheetActionAdapter extends BaseAdapter {

    //http://stackoverflow.com/questions/18850704/dynamically-change-column-number-in-android-gridview

    private Integer[] images = new Integer[]{
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };

    private final Context mContext;

    public BottomSheetActionAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemHolder holder;
        if (view != null) {
            holder = (ItemHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sheet_grid_item, parent, false);
            holder = new ItemHolder(view);
            view.setTag(holder);
        }

        // TODO
        holder.icon.setImageResource(R.mipmap.ic_launcher);
        holder.label.setText("label");

        return view;
    }

    /**
     *
     */
    public static class ItemHolder {

        @Bind(R.id.bs_item_icon)
        ImageView icon;

        @Bind(R.id.bs_item_label)
        TextView label;

        public ItemHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
