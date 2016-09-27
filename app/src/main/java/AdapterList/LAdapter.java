package AdapterList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liangchenzhou.weatherlife.R;

import java.util.ArrayList;

import entity.Cloth;

/**
 * Adapter for displaying list of cloth items
 */
public class LAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Cloth> clothes;



    public LAdapter(Context context, ArrayList<Cloth> arrayList) {
        this.context = context;
        this.clothes = arrayList;
    }

    @Override
    public int getCount() {
        return clothes.size();
    }

    @Override
    public Object getItem(int position) {
        return clothes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder{
        TextView name, descCloth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cloth_item_layout, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.sContent);
            vh.descCloth = (TextView) convertView.findViewById(R.id.descCloth);
            convertView.setTag(vh);
        } else  {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(clothes.get(position).getClothName());
        vh.descCloth.setText(clothes.get(position).getClothDesc());
        return convertView;
    }
}
