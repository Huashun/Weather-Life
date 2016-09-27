package AdapterList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liangchenzhou.weatherlife.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import entity.MsgChat;

/**
 * List adapter for displaying the chat messages item
 */
public class ChatSenderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MsgChat> arrayMsgs;

    public ChatSenderAdapter() {
    }

    public ChatSenderAdapter(Context context, ArrayList<MsgChat> arrayMsgs) {
        this.context = context;
        this.arrayMsgs = arrayMsgs;
    }

    @Override
    public int getCount() {
        return arrayMsgs.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayMsgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView senderText, senderName, senderTime;
    }

    //
    private class ViewHolderReceiver {
        TextView senderTextR, senderNameR, senderTimeR;
    }

    public class ViewHolderImage {
        ImageView imageVS;
        TextView imageName, senderNames, senderTimes;
    }

    public class ViewHolderImageReceiver {
        ImageView imageVR;
        TextView imageNamer, senderNamer, senderTimer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shareUser", Context.MODE_PRIVATE);
        ViewHolder vh;
        ViewHolderReceiver vh2;
        ViewHolderImage vhI;
        ViewHolderImageReceiver vhIR;
//.equals(sharedPreferences.getString("userName", ""))

        if (!(arrayMsgs.get(position).getSenderName()).equals(sharedPreferences.getString("userName", "")) &&
                !arrayMsgs.get(position).getImageName().equals("")) {
            vhIR = new ViewHolderImageReceiver();
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.chat_image_item_receiver, null);
            vhIR.imageNamer = (TextView) convertView.findViewById(R.id.imageNameR);
            vhIR.senderNamer = (TextView) convertView.findViewById(R.id.imageReceiverName);
            vhIR.senderTimer = (TextView) convertView.findViewById(R.id.imageReceiverTime);
            vhIR.imageVR = (ImageView) convertView.findViewById(R.id.imageReceiver);

            vhIR.imageNamer.setText(arrayMsgs.get(position).getImageName());
            vhIR.senderNamer.setText(arrayMsgs.get(position).getSenderName());
            vhIR.imageVR.setImageBitmap(displayImage(arrayMsgs.get(position).getContent()));
            vhIR.senderTimer.setText(arrayMsgs.get(position).getMsgDate());
        } else if (!(arrayMsgs.get(position).getSenderName()).equals(sharedPreferences.getString("userName", ""))) {

            vh2 = new ViewHolderReceiver();
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.chat_item_receiver, null);
            vh2.senderNameR = (TextView) convertView.findViewById(R.id.receiverName);
            vh2.senderTextR = (TextView) convertView.findViewById(R.id.receiverContent);
            vh2.senderTimeR = (TextView) convertView.findViewById(R.id.smallTimeReceiver);

            vh2.senderNameR.setText(arrayMsgs.get(position).getSenderName());
            vh2.senderTextR.setText(arrayMsgs.get(position).getContent());
            vh2.senderTimeR.setText(arrayMsgs.get(position).getMsgDate());

        } else if (!arrayMsgs.get(position).getImageName().equals("")) {
            vhI = new ViewHolderImage();
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.chat_image_item, null);
            vhI.imageVS = (ImageView) convertView.findViewById(R.id.imageSender);
            vhI.senderNames = (TextView) convertView.findViewById(R.id.imageSenderName);
            vhI.senderTimes = (TextView) convertView.findViewById(R.id.imageSenderTime);
            vhI.imageName = (TextView) convertView.findViewById(R.id.imageNameS);

            vhI.imageName.setText(arrayMsgs.get(position).getImageName());
            vhI.senderNames.setText(arrayMsgs.get(position).getSenderName());
            vhI.imageVS.setImageBitmap(displayImage(arrayMsgs.get(position).getContent()));
            vhI.senderTimes.setText(arrayMsgs.get(position).getMsgDate());
        } else {
            vh = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.chat_item, null);
            vh.senderName = (TextView) convertView.findViewById(R.id.senderName);
            vh.senderText = (TextView) convertView.findViewById(R.id.sContent);
            vh.senderTime = (TextView) convertView.findViewById(R.id.smallTime);

            vh.senderName.setText(arrayMsgs.get(position).getSenderName());
            vh.senderText.setText(arrayMsgs.get(position).getContent());
            vh.senderTime.setText(arrayMsgs.get(position).getMsgDate());

        }
        return convertView;
    }

    //get image from url
    public Bitmap displayImage(String url) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            URL urls = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap mmbit = BitmapFactory.decodeStream(inputStream);
            return mmbit;
        } catch (Exception e) {
            System.out.println(e + "=====================");
        }
        return null;
    }

//    //get image from url
//    class AsyncLoadImage extends AsyncTask<String, Void, Bitmap>{
//        private Context context;
//        AsyncLoadImage(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... url) {
//            try {
//                URL urls = new URL(url[0]);
//                HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream inputStream = connection.getInputStream();
//                Bitmap mmbit = BitmapFactory.decodeStream(inputStream);
//                return mmbit;
//            } catch (Exception e) {
//                System.out.println(e + "=====================");
//            }
//            return null;
//        }
//    }
}
