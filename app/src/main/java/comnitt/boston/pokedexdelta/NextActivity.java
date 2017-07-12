package comnitt.boston.pokedexdelta;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HP on 08-Jul-17.
 */
public class NextActivity extends AppCompatActivity {
    EditText Text;
    ListView list;
    CustomAdapter Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity);

        Text = (EditText) findViewById(R.id.Text);
        list = (ListView) findViewById(R.id.list);
        Adapter = new CustomAdapter(this);
        list.setAdapter(Adapter);
    }

    class MyViewHolder {
        TextView text;
        ImageView img;

        public MyViewHolder(View view) {
            img = (ImageView) view.findViewById(R.id.icon);
            text = (TextView) view.findViewById(R.id.text1);
        }
    }

    class CustomAdapter extends ArrayAdapter<String> {

        Context context;

        public CustomAdapter(Context context) {
            super(context, R.layout.list, R.id.text1, MainActivity.list);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            MyViewHolder viewholder = null;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list, parent, false);
                viewholder = new MyViewHolder(row);
                row.setTag(viewholder);
            } else {
                viewholder = (MyViewHolder) row.getTag();
            }
            viewholder.img.setImageBitmap(MainActivity.bmp.get(position));
            viewholder.text.setText(MainActivity.list.get(position));
            return row;
        }
    }

    public void clear(View v) {
        if (MainActivity.num == 0)
            Toast.makeText(getApplicationContext(), "List empty", Toast.LENGTH_LONG).show();
        MainActivity.num = 0;
        MainActivity.list.clear();
        MainActivity.bmp.clear();
        list.setAdapter(Adapter);
    }

    public void deletepoke(View v) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int mark = -1;
        String string = Text.getText().toString();
        string = string.trim().toUpperCase();
        Text.setText("");
        Text.clearFocus();

        for (int x = 0; x < MainActivity.num; x++) {
            String t = MainActivity.list.get(x);
            if (string.equalsIgnoreCase(t)) {
                MainActivity.list.remove(x);
                MainActivity.bmp.remove(x);
                MainActivity.num--;
                mark = 1;
                list.setAdapter(Adapter);
                break;
            }
        }
        if (mark != 1)
            Toast.makeText(getApplicationContext(), "No Such Item Present", Toast.LENGTH_LONG).show();
    }
}
