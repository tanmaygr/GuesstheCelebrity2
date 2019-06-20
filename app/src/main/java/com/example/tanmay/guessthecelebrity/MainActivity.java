package com.example.tanmay.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebLink= new ArrayList<>();
    ArrayList<String> celebName= new ArrayList<>();
    int choseCeleb=0;
    int loc=0;
//    TextView b5;
    String[] arr= new String[4];

    Button b1,b2,b3,b4;
    ImageView imageView;
    int cor=0;
    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(loc))){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
//            cor++;
//            b5.setText(cor);
            createQues();
        }
        else{
            Toast.makeText(this, "Wrong....It was - "+celebName.get(choseCeleb), Toast.LENGTH_SHORT).show();
//            b5.setText(cor);
            createQues();
        }

    }
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class Download extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(strings[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data = reader.read();
                while (data!=-1){
                    char current= (char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Download d = new Download();
        String result = "";
        imageView=findViewById(R.id.imageView);
        b1=findViewById(R.id.button);
        b2=findViewById(R.id.button2);
        b3=findViewById(R.id.button3);
        b4=findViewById(R.id.button4);
//        b5=findViewById(R.id.button6);

        try {
            Log.d("INSIDE: ", "TRY");
            result = d.execute("http://www.posh24.se/kandisar").get();
            Log.d("HERE2: ", result);
            String [] splitResult= result.split("\t<a href=\"/lauren_conrad\">");
            Log.d("HERE: ", String.valueOf(splitResult));
            Pattern p =Pattern.compile("<img src=\"(.*?)\"");
            Matcher m= p.matcher(splitResult[0]);

            while (m.find()){
               celebLink.add(m.group(1));
            }

            p=Pattern.compile("alt=\"(.*?)\"/>");
            m=p.matcher(splitResult[0]);

            while (m.find()){
               celebName.add(m.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        createQues();
    }
    public void createQues(){
        Random random =new Random();
        choseCeleb=random.nextInt(celebLink.size());
        ImageDownloader task= new ImageDownloader();
        Bitmap celebImage;
        try {
            celebImage=task.execute(celebLink.get(choseCeleb)).get();
            imageView.setImageBitmap(celebImage);
            loc=random.nextInt(4);
            int incorrect;
            for(int i=0;i <4;i++){
                if(i==loc){
                    arr[i]=celebName.get(choseCeleb);
                }
                else {
                    incorrect=random.nextInt(celebLink.size());
                    while (incorrect==choseCeleb) {
                        incorrect=random.nextInt(celebName.size());
                    }
                    arr[i]=celebName.get(incorrect);
                }
            }
            b1.setText(arr[0]);
            b2.setText(arr[1]);
            b3.setText(arr[2]);
            b4.setText(arr[3]);
        }catch (Exception e) {
            e.printStackTrace();
        }



    }

}