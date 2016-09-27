package AdapterList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liangchenzhou.weatherlife.R;

import java.util.ArrayList;

import entity.WeatherFeather;

/**
 * list suburb adapter for displaying user preference
 */
public class ListSuburbAdatper extends BaseAdapter {
    private Context context;
    private ArrayList<WeatherFeather> feathers;



    public ListSuburbAdatper(Context context, ArrayList<WeatherFeather> arrayList) {
        this.context = context;
        this.feathers = arrayList;
    }


    @Override
    public int getCount() {
        return feathers.size();
    }

    @Override
    public Object getItem(int position) {
        return feathers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolders{
        TextView subName;
    }

    public class ViewHoldersAll{
        TextView subNameA, condi, temp;
        ImageView image;
    }

    //display the view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolders vh;
        ViewHoldersAll allVh;
        if (feathers.get(position).getTemps() == null){
            vh = new ViewHolders();
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.preference_item, null);
            vh.subName = (TextView) convertView.findViewById(R.id.itemSuburb);
            vh.subName.setText(feathers.get(position).getSuburbName());
            return convertView;
        } else {
            allVh = new ViewHoldersAll();
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.preference_item, null);
            allVh.subNameA = (TextView) convertView.findViewById(R.id.itemSuburb);
            allVh.condi = (TextView) convertView.findViewById(R.id.itemWeather);
            allVh.temp = (TextView) convertView.findViewById(R.id.itemTemp);
            allVh.image = (ImageView) convertView.findViewById(R.id.itemImage);
            allVh.subNameA.setText(feathers.get(position).getSuburbName());
            allVh.condi.setText(feathers.get(position).getWeatherCondition());
            allVh.temp.setText(feathers.get(position).getTemps());
            allVh.image.setImageBitmap(feathers.get(position).getWeaImage());
            return convertView;
        }
    }
}
