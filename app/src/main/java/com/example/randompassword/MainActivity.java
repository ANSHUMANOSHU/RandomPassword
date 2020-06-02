package com.example.randompassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CheckBox capitals,small,numbers,specials;
    public EditText lengthofpass;
    public static EditText required;
    public ListView listView;
    public ArrayList<String> passwords;
    public ArrayAdapter<String> adapter;

    private static Random random = new Random();
    private static String result ="";
    static int i =0;
    private static final String capitalletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String smallletters = "abcdefghijklmnopqrstuvwxyz";
    private static final String number = "0123456789";
    private static final String special = "@!#$%^&*+-/_|";
    public  static String value = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capitals = findViewById(R.id.capitals);
        small = findViewById(R.id.smalls);
        numbers = findViewById(R.id.numbers);
        specials = findViewById(R.id.specials);
        lengthofpass = findViewById(R.id.length);
        required = findViewById(R.id.required);
        listView = findViewById(R.id.list);

        passwords = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,passwords);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("copy",passwords.get(position));
                Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void generateClicked(View view) {

        boolean selected = false;
        boolean written = false;

        passwords.clear();

        if(capitals.isChecked()){
            value = value + capitalletters;
            selected = true;
        }
        if (small.isChecked()){
            value = value + smallletters;
            selected = true;
        }
        if (numbers.isChecked()){
            value = value + number;
            selected = true;
        }
        if (specials.isChecked()){
            value = value + special;
            selected = true;
        }

        if(!lengthofpass.getText().toString().isEmpty() && !required.getText().toString().isEmpty()){
           written = true;
        }

        if(!(written && selected)){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            return;
        }

        Progression progression = new Progression();
        progression.execute();

    }

    private static String passwordGenerator(int len, String values) {
        result = "";
        for(i=0;i<len;i++){
            result +=values.charAt(random.nextInt(values.length()));
        }
        return result;
    }

    class Progression extends AsyncTask<Void,Integer,Void>{
        int requiredpass = Integer.parseInt(required.getText().toString());
        int length = Integer.parseInt(lengthofpass.getText().toString());
        int j =0;

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for(;j<requiredpass;j++) {
                publishProgress(j);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passwords.add(passwordGenerator(length, value));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            dialog.setMessage("Generated : "+values[0]+" of " + requiredpass);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }

}

