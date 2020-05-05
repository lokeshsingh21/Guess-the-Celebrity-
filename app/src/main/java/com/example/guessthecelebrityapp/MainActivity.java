package com.example.guessthecelebrityapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    int choosenCeleb=0,correctAnswer;
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    ImageView imageView;
    Button option1,option2,option3,option4;
    Random rand=new Random();

    public void guessButton(View view){
        if(correctAnswer+1==Integer.parseInt(view.getTag().toString())) Toast.makeText(this,"Guessed correct:)",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Wrong guess! It was "+celebNames.get(choosenCeleb)+'.',Toast.LENGTH_LONG).show();
        newQuestion();
    }

    public void newQuestion(){
        choosenCeleb=rand.nextInt(celebURLs.size());
        ImageDownloader imagetask=new ImageDownloader();
        Bitmap celebImage= null;
        try {
            celebImage = imagetask.execute(celebURLs.get(choosenCeleb)).get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(celebImage);
        setoptions();
    }

    public void setoptions(){
        correctAnswer=rand.nextInt(4);
        ArrayList<String> answers=new ArrayList<String>();
        for(int i=0;i<4;i++){
            if(i==correctAnswer) answers.add(celebNames.get(choosenCeleb));
            else{
                String wrong=celebNames.get(rand.nextInt(celebNames.size()));
                while(wrong==celebNames.get(choosenCeleb))
                    wrong=celebNames.get(rand.nextInt(celebNames.size()));
                answers.add(wrong);
            }
        }
        option1.setText(answers.get(0));
        option2.setText(answers.get(1));
        option3.setText(answers.get(2));
        option4.setText(answers.get(3));
    }

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url=new URL(urls[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            try{
                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                String result=null;
                while(data!=-1){
                    char current=(char)data;
                    data=reader.read();
                    result+=current;
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

        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        option3=findViewById(R.id.option3);
        option4=findViewById(R.id.option4);
        imageView=findViewById(R.id.celebimage);
        DownloadTask task=new DownloadTask();
        String result="";
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String[] splitResult=result.split("<div class=\"listedArticles\">");
        Pattern p=Pattern.compile("img src=\"(.*?)\"");
        Matcher m=p.matcher(splitResult[0]);
        while(m.find()){
            celebURLs.add(m.group(1));
        }
        p=Pattern.compile("alt=\"(.*?)\"");
        m=p.matcher(splitResult[0]);
        while(m.find()){
            celebNames.add(m.group(1));
        }
        newQuestion();
    }
}
