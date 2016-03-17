package com.sourcenews.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sourcenews.android.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;

public class ProfilesActivity extends AppCompatActivity {
    //String[] colors = { "Красны", "Оранжевый", "Желтый", "Зелёный", "Голубой", "Синий", "Фиолетовый"};
    ListView lvSimple;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Адаптированные сайты");

        tags_base mt = new tags_base(this);
        mt.execute();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private class tags_base extends AsyncTask<Void, Void, Void> {
        String page_html;
        ProfilesActivity p_activity;

        tags_base(ProfilesActivity p_activity){
            this.p_activity = p_activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(ProfilesActivity.this);
            mProgressDialog.setTitle("Получение профилей");
            mProgressDialog.setMessage("Загрузка...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect("http://newsly.esy.es/tags_base.txt").get();
                //title = document.title();
                page_html = document.html();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //TextView txttitle = (TextView) findViewById(R.id.titletxt);
            //txttitle.setText(page_html);
            //String lololo = page_html;
            String page_text = Jsoup.parse(page_html).text();

            int delim = 0;
            int start_i = 0;

            for (int i = 0; i < page_text.length()-1; i++) {
                char text_Char = page_text.charAt(i);
                char next_text_Char = page_text.charAt(i+1);

                String text_Char_str = Character.toString(text_Char);
                String next_text_Char_str = Character.toString(next_text_Char);

                if (text_Char_str.contains("|") && next_text_Char_str.contains("|")){
                    String tag_delim = page_text.substring(start_i, i);
                    start_i = i + 3;
                    delim++;
                }
            }


            String[] tags_mas = new String[delim];
            start_i = 0;
            delim = 0;

            for (int i = 0; i < page_text.length()-1; i++) {
                char text_Char = page_text.charAt(i);
                char next_text_Char = page_text.charAt(i+1);

                String text_Char_str = Character.toString(text_Char);
                String next_text_Char_str = Character.toString(next_text_Char);

                if (text_Char_str.contains("|") && next_text_Char_str.contains("|")){
                    String tag_delim = page_text.substring(start_i, i);
                    start_i = i + 3;
                    tags_mas[delim] = tag_delim;
                    delim++;
                }
            }



            for (int i = 0; i < delim; i++) {
                tags_mas[i] = tags_mas[i] + "|";

            }

            String[] all_tags_mas = new String[delim*3];
            int delim_col = delim * 3;

            int main_i = 0;
            start_i = 0;
            for (int i = 0; i < delim; i++) {
                start_i = 0;
                for (int j = 0; j < tags_mas[i].length(); j++) {
                    char text_Char = tags_mas[i].charAt(j);

                    String text_Char_str = Character.toString(text_Char);
                    if (text_Char_str.contains("|")) {
                        all_tags_mas[main_i] = tags_mas[i].substring(start_i, j);
                        start_i = j + 1;
                        main_i++;
                    }
                }
            }

            for (int i = 0; i < delim * 3; i++) {
                System.out.print(all_tags_mas[i]);
                System.out.println();
            }

            //ListView list = (ListView) findViewById(R.id.listView);
            // String[] colors = {all_tags_mas[0], all_tags_mas[3]};

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>
            //        (p_activity, android.R.layout.simple_list_item_1, colors);


            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.main_liner);
            String [] texts = new String[delim_col/3];

            for (int t = 0; t < texts.length; t++){
                texts[t] = all_tags_mas[t*3];
            }

            boolean[] checked = new boolean[delim_col/3];
            Arrays.fill(checked, false);

            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                    texts.length);
            Map<String, Object> m;
            for (int i = 0; i < texts.length; i++) {
                m = new HashMap<String, Object>();
                m.put("ATTRIBUTE_NAME_TEXT", texts[i]);
                m.put("ATTRIBUTE_NAME_CHECKED", checked[i]);
                data.add(m);
            }

            String[] from = { "ATTRIBUTE_NAME_TEXT", "ATTRIBUTE_NAME_CHECKED"};

            int[] to = { R.id.tvText, R.id.cbChecked};

            SimpleAdapter sAdapter = new SimpleAdapter(p_activity, data, R.layout.adapter_item, from, to);

            lvSimple = (ListView) findViewById(R.id.listView);
            lvSimple.setAdapter(sAdapter);


//            CheckBox checkBox = new CheckBox(p_activity);
//            checkBox.setText("lalala");
//            linearLayout.addView(checkBox);
//


//            for (int i = 0; i < delim_col/3; i++) {
//                CheckBox checkBox = new CheckBox(p_activity);
//
//                //checkBox.isChecked();
//                checkBox.setTextSize(16);
//                //checkBox.setText(all_tags_mas[i * 3]);
//                checkBox.setId(i);
//                int ch_num = (checkBox.getId());
//                int y = 1;
//                String str_y = ch_num + "";
//                checkBox.setText(str_y);
//                linearLayout.addView(checkBox);
//            }

            //list.setAdapter(adapter);
            //String lalala = page_html;
            mProgressDialog.dismiss();
        }

    }

}



