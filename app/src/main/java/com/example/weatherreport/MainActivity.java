package com.example.weatherreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView info;
    EditText editText;

    public void getInfo(View view){
        String res=null;
        String location=editText.getText().toString().toLowerCase();
        Downloader task=new Downloader();
        try{
            res=task.execute("https://openweathermap.org/data/2.5/weather?q="+location+"&appid=439d4b804bc8187953eb36d2a8c26a02").get();
            Log.i("Info",res);
        }catch (Exception e){
            e.printStackTrace();
        }
        InputMethodManager mgr= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    public class Downloader extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                String weatherinfo=jsonObject.getString("weather");
                String tempinfo=jsonObject.getString("main");
                JSONArray arr=new JSONArray(weatherinfo);
                JSONObject temp=new JSONObject(tempinfo);
                String message="";
                String tempmessage="";
                String humdmessage="";
                String chosencity="";
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description").toUpperCase();
                    if(!main.equals("")&&!description.equals("")){
                        chosencity="City/Country : "+editText.getText().toString().toUpperCase()+"\n";
                        message="Overall Weather now "+": "+description+"\r\n";
                        tempmessage="Average Temperature(in degree celsius) :"+" "+temp.getString("temp")+"\n";
                        humdmessage="Average Humidity : "+" "+temp.getString("humidity")+"%";
                    }
                }

                if(!message.equals("")){
                    info.setText(chosencity+"\n"+message+"\n"+tempmessage+"\n"+humdmessage);
                }else{
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                info.setText("");
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info=findViewById(R.id.info);
        editText=findViewById(R.id.editText);
    }
}