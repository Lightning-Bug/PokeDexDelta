package comnitt.boston.pokedexdelta;

//import comnitt.boston.pokedexdelta.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {


    //ActivityMainBinding binding;
    String string;
    JSONObject Object;
    public static int num = 0;
    JSONArray Array;
    Boolean present = false;
    public static ArrayList<Bitmap> bmp = new ArrayList<>();
    public static ArrayList<String> list = new ArrayList<>();

    RelativeLayout screen;
    TextView Pokemon,Text2;
    ImageView PokemonImage;
    EditText Text;
    TextView hText,wText,tText;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //binding = DataBindingUtil.setContentView(this , R.layout.activity_main);

        Text = (EditText) findViewById(R.id.Text);
        screen = (RelativeLayout) findViewById(R.id.screen);
        hText = (TextView) findViewById(R.id.hText);
        Pokemon = (TextView) findViewById(R.id.Pokemon);
        wText = (TextView) findViewById(R.id.wText);
        tText = (TextView) findViewById(R.id.tText);
        Text2 = (TextView) findViewById(R.id.Text2);
        PokemonImage = (ImageView) findViewById(R.id.PokemonImage);
    }

    public void search (View v)
    {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int index = 0;
        present = false;
        String string = Text.getText().toString();
        string = string.trim().toUpperCase();

        for (int i=0; i<num; i++) {
            String t = list.get(i);
            if (string.equalsIgnoreCase(t)) {
                present = true;
                index = i;
                break;
            }
        }
        if (present) {
            String stringTemp = list.get(index);
            list.remove(index); list.add(0,stringTemp);
            Bitmap bitmapTemp = bmp.get(index);
            bmp.remove(index); bmp.add(0,bitmapTemp);
        }
        new BackgroundTask().execute();
    }

    public void history (View v)
    {
        Intent i =new Intent(this,NextActivity.class);
        startActivity(i);
    }

    public Bitmap getBitmap(String src)
    {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap mybmp = BitmapFactory.decodeStream(input);
            return mybmp;
        } catch (IOException e) {
            return null;}
    }

    class BackgroundTask extends AsyncTask<Void,Void,String>
    {
        String j_url;
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(j_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((string=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(string+"\n");
                }
                inputStream.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                String str = stringBuilder.toString().trim();
                Object = new JSONObject(str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1));
                Object = (JSONObject) Object.get("sprites");
                String picurl = Object.get("front_default").toString();
                bmp.add(0,getBitmap(picurl));
                return str;
        } catch (MalformedURLException e)
        {
        e.printStackTrace();
        Log.i("me","first");
        } catch (IOException e) {
        e.printStackTrace();
        Log.i("me","second");
       } catch (JSONException e) {
        e.printStackTrace();
        }
        return null;
        }
        @Override
        protected void onPreExecute() {
            j_url = "https://pokeapi.co/api/v2/pokemon/"+ Text.getText().toString().toLowerCase().trim()+"/";
        }

        @Override
        protected void onPostExecute(String s)
        {
            Text.setText("");
            Text.clearFocus();
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (s!=null)
            {
                screen.setVisibility(View.VISIBLE);
                try {
                    Object = new JSONObject(s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1));
                    /*binding.Text2.setText(Object.get("text").toString());
                    binding.Pokemon.setText(Object.get("name").toString().toUpperCase());
                    binding.wText.setText(Object.get("weight").toString());
                    binding.hText.setText(Object.get("height").toString());
                    if (!present) list.add(0,Object.get("name").toString().toUpperCase());
                    binding.PokemonImage.setImageBitmap(bmp.get(0));
                    */
                    wText.setText(Object.get("weight").toString());
                    hText.setText(Object.get("height").toString());
                    Text2.setText(Object.get("id").toString());
                   Pokemon.setText(Object.get("name").toString().toUpperCase());
                    if (!present)
                        list.add(0,Object.get("name").toString().toUpperCase());
                    PokemonImage.setImageBitmap(bmp.get(0));

                    if (present) bmp.remove(0);
                    num++;
                    Array = Object.getJSONArray("types");
                    String str = "";
                    int counts = 0;
                    while (counts < Array.length())
                    {
                        JSONObject J1 = Array.getJSONObject(counts);
                        J1 = (JSONObject) J1.get("type");
                        if (counts == 0) str = str + J1.get("name").toString();
                        else str = str + " | " + J1.get("name").toString();
                        counts++;
                    }
                    tText.setText(str);
                    Toast.makeText(getApplicationContext(),"Loaded Pokemon Details",Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    Log.i("Handler ","JSONException: Handled");
                    e.printStackTrace();

                }
            }
            else Toast.makeText(getApplicationContext(),"Invalid Pokemon Name",Toast.LENGTH_LONG).show();
        }
    }
}
