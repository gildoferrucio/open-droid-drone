package com.example.gildo.quadcoptercontroller.models.gui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gildo.quadcoptercontroller.R;

import java.util.List;

/**
 * Created by gildo on 14/03/16.
 */
public class PeripheralAdapter extends BaseAdapter {
    Context context;

    List<Peripheral> peripheralList;

    public PeripheralAdapter(Context context, List<Peripheral> peripheralList) {
        this.context = context;
        this.peripheralList = peripheralList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return this.peripheralList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return this.peripheralList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Peripheral peripheral = this.peripheralList.get(position);

        View row = LayoutInflater.from(this.context).inflate(R.layout.item_peripheral, null);

        ImageView periphericIcon = (ImageView) row.findViewById(R.id.imageView_peripheric_icon);
        TextView periphericName = (TextView) row.findViewById(R.id.textView_peripheric_name);
        TextView periphericDescription = (TextView) row.findViewById(R.id.textView_peripheric_description);

        periphericIcon.setImageDrawable(this.context.getResources().getDrawable(peripheral
                .getIconResource()));
        periphericName.setText(this.context.getResources().getString(peripheral
                .getName()));
        periphericDescription.setText(this.context.getResources().getString(peripheral
                .getDescription()));

        return row;
    }
}
