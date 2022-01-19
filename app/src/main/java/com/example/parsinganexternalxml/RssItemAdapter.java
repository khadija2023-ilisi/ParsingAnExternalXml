package com.example.parsinganexternalxml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RssItemAdapter  extends ArrayAdapter<Item> {

    private Context context;

    public RssItemAdapter(Context context, int textViewResourceId,
                          List<Item> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.itel_layout, null);
        }

        Item item = getItem(position);
        if (item != null) {
            // our layout has two TextView elements
            TextView titleView = (TextView) view.findViewById(R.id.titleText);
            TextView descView = (TextView) view
                    .findViewById(R.id.descriptionText);
            ImageView imgv =view.findViewById(R.id.imageViewFlag);

            titleView.setText(item.getTitle());
            descView.setText(item.getDescription()+"\n"+item.getPubDate()+"\n");

            downloadImg dwn =new downloadImg();
            String url =item.getImage().toString();
            try {
                //Bitmap bit=dwn.execute("https://images.livemint.com/img/2022/01/08/1600x900/9c8444da-6887-11ec-9c15-fc0164268d4c_1641624651281_1641624660328.jpg").get();
                Bitmap bit=dwn.execute(url).get();
                imgv.setImageBitmap(bit);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public class downloadImg extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=null;
            URL url;
            HttpURLConnection httpURLConnection;
            InputStream inputStream;
            try {
                url=new URL(strings[0]);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                inputStream=httpURLConnection.getInputStream();
                bitmap= BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
