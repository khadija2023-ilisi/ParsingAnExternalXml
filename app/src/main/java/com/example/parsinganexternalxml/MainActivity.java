package com.example.parsinganexternalxml;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public ListView listview;
    public ArrayAdapter<Item> adapter;
    public List<Item> listItem = new ArrayList<Item>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.newsListView);
        adapter = new RssItemAdapter(this, android.R.layout.simple_list_item_1, listItem);
        listview.setAdapter(adapter);

        String siteURL = "https://www.livemint.com/rss/science";
        new RetrieveFeedTask().execute(siteURL);
    }

    public List<Item> parsingMethod(URL feedURL)
            throws XmlPullParserException, IOException {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(feedURL.openStream(), null);

        int eventType = parser.getEventType();

        boolean done = false;

        Item currentItem = new Item();

        while (eventType != XmlPullParser.END_DOCUMENT && !done) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")) {
                        // a new item element
                        currentItem = new Item();
                    } else if (currentItem != null) {
                        if (name.equalsIgnoreCase("link")) {
                            currentItem.setLink(parser.nextText());
                        } else if (name.equalsIgnoreCase("description")) {
                            currentItem.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("pubDate")) {
                            currentItem.setPubDate(parser.nextText());
                        } else if (name.equalsIgnoreCase("title")) {
                            currentItem.setTitle(parser.nextText());
                        }else if(name.equalsIgnoreCase("content")){
                            currentItem.setImage(parser.getAttributeValue(2));
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentItem != null) {
                        listItem.add(currentItem);
                    } else if (name.equalsIgnoreCase("channel")) {
                        done = true;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return listItem;
    }

    //------- AsyncTask ------------//
    class RetrieveFeedTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... urls) {
            try {
                URL feedURL = new URL(urls[0]);
                listItem = parsingMethod(feedURL);

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (XmlPullParserException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
        }
    }

}