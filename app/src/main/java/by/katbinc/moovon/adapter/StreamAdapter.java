package by.katbinc.moovon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.katbinc.moovon.R;
import by.katbinc.moovon.model.PlayerStreamModel;

public class StreamAdapter extends AbstractGenericAdapter<PlayerStreamModel> {
    private Context mContext;

    public StreamAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(mContext).inflate(R.layout.list_item_stream, null, true);
            holder = new ViewHolder();
            holder.title = (TextView) rowView.findViewById(R.id.title);
            holder.description = (TextView) rowView.findViewById(R.id.description);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.title.setText(getObject(position).getTitle());
        holder.description.setText(getObject(position).getDescription());
        return rowView;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
    }
}
