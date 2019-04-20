package techworld.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.BufferedReader;
import java.io.IOException;
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

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chooseCeleb=0;
    int locationOfCorrectAnswer=0;
    ImageView imageView;
    String[] answer= new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls){

            try {
                URL url= new URL(urls[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is= connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(is);
                return myBitmap;

            }

            catch (MalformedURLException e) {

                e.printStackTrace();
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            return null;

        }
    }


    public class DownloadTask extends AsyncTask<String , Void , String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection connection=null;

            try{
                url=new URL(urls[0]);
                connection= (HttpURLConnection) url.openConnection();
                InputStream is=connection.getInputStream();
                InputStreamReader reader= new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(reader);
                    String inputLine;
                    StringBuilder builder = new StringBuilder();

                    while ((inputLine = br.readLine()) != null) {
                        builder.append(inputLine);
                    }
                    result = builder.toString();
                } catch (Exception e) {

                e.printStackTrace();
            }
            return result;
            }


        }


    public void chooseCelebrity(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            FancyToast.makeText(this,"Correct Answer!!",FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show();
        }
        else {

            FancyToast.makeText(this,"Wrong Answer!!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();

        }

        newQuestion();
    }

    public void newQuestion(){

        Random random= new Random();
        chooseCeleb = random.nextInt(celebUrls.size());
        DownloadImage imageTask= new DownloadImage();
        Bitmap celebImage;
    try {
        celebImage = imageTask.execute(celebUrls.get(chooseCeleb)).get();

        imageView.setImageBitmap(celebImage);

        int incorrectAnswerLocation;
        locationOfCorrectAnswer = random.nextInt(4);
        for (int i = 0; i < 4; i++) {

            if (i == locationOfCorrectAnswer) {

                answer[i] = celebNames.get(chooseCeleb);

            } else {
                incorrectAnswerLocation = random.nextInt(celebUrls.size());
                while (incorrectAnswerLocation == chooseCeleb) {

                    incorrectAnswerLocation = random.nextInt(celebUrls.size());
                }
                answer[i] = celebNames.get(incorrectAnswerLocation);
            }

        }

        button1.setText(answer[0]);
        button2.setText(answer[1]);
        button3.setText(answer[2]);
        button4.setText(answer[3]);
    }
    catch(Exception e){
        e.printStackTrace();
    }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.icon);

        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        imageView=findViewById(R.id.imageView);
        DownloadTask task= new DownloadTask();
        String result=null;
        try{
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult=result.split("<div class=\"articleImage\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find()){
                celebUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"/>");
            m = p.matcher(splitResult[0]);
            while(m.find()){
                celebNames.add(m.group(1));
            }



        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        newQuestion();
    }

}
