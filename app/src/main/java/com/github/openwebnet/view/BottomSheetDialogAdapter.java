package com.github.openwebnet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.openwebnet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BottomSheetDialogAdapter extends BaseAdapter {

    private final Context mContext;
    private Menu mMenu;

    public BottomSheetDialogAdapter(Context context) {
        mContext = context;
        setupMenu();
    }

    private void setupMenu() {
        // https://github.com/Flipboard/bottomsheet/blob/master/bottomsheet-commons/src/main/java/com/flipboard/bottomsheet/commons/MenuSheetView.java
        // dirty hack to get a menu instance since MenuBuilder isn't public
        this.mMenu = new PopupMenu(mContext, null).getMenu();

        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.menu_bottom_sheet, mMenu);
    }

    @Override
    public int getCount() {
        return mMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenu.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mMenu.getItem(position).getItemId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemHolder holder;
        if (view != null) {
            holder = (ItemHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bottom_sheet_dialog_item, parent, false);
            holder = new ItemHolder(view);
            view.setTag(holder);
        }

        MenuItem menuItem = (MenuItem) getItem(position);
        holder.icon.setImageDrawable(menuItem.getIcon());
        holder.label.setText(menuItem.getTitle());

        return view;
    }

    /**
     *
     */
    public static class ItemHolder {

        @Bind(R.id.imageViewBottomSheetItemIcon)
        ImageView icon;

        @Bind(R.id.textViewBottomSheetItemLabel)
        TextView label;

        public ItemHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
