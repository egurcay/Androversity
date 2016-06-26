package com.example.egurcay.androversity;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity {

    EditText name1,surname2, email3, password4, repassword5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        name1 = (EditText) findViewById(R.id.editText);
        surname2 = (EditText) findViewById(R.id.editText2);
        email3 = (EditText) findViewById(R.id.editText3);
        password4 = (EditText) findViewById(R.id.editText4);
        repassword5 = (EditText) findViewById(R.id.editText5);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               send( name1.getText().toString() , surname2.getText().toString() , email3.getText().toString() , password4.getText().toString(), repassword5.getText().toString());
            }

        });
    }

    public boolean checkEmail(String email3)
    {
        boolean a = true;
        if(email3.indexOf(".com") >= 0)
            a = true;
        else
            a = false;
        return a;
    }

    public boolean checkPwMatch(String password4, String repassword5)
    {
        return (password4.equals(repassword5));
    }



    public void send(final String name, final String surname, final String email, final String password, final String repassword){
        Thread t = new Thread(){
            public void run() {
                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),5000);
                HttpResponse response;



                try {
                    if (checkEmail(email) && checkPwMatch(password,repassword)) {
                        HttpPost post = new HttpPost("http://egurcay.com/androversity/kaydet.php");
                        List<NameValuePair> thingsToAdd = new ArrayList<NameValuePair>(2);
                        thingsToAdd.add(new BasicNameValuePair("name", name));
                        thingsToAdd.add(new BasicNameValuePair("surname", surname));
                        thingsToAdd.add(new BasicNameValuePair("email", email));
                        thingsToAdd.add(new BasicNameValuePair("password", password));
                        post.setEntity(new UrlEncodedFormEntity(thingsToAdd, "UTF-8"));
                        String html = "";
                        response = client.execute(post);

                        if (response != null) {
                            InputStream in = response.getEntity().getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            StringBuilder sb = new StringBuilder();
                            String lines = null;
                            try {
                                while ((lines = reader.readLine()) != null) {
                                    sb.append(lines + "\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                            html = sb.toString();
                            JSONArray parcala = new JSONArray(html);

                            if (parcala.length() > 0) {
                                JSONObject obj = parcala.getJSONObject(0);

                                if (obj.getBoolean("done") != true) {
                                    Toast.makeText(LogIn.this, "A problem occured please try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LogIn.this, "You have logged in susccessfully.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    else
                        Toast.makeText(LogIn.this, "Passwords do not match or your email adress is invalid", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        Log.e("Try-catch Hatası", "Hata: " + e);
                    }
                    Looper.loop();

            }
        };
        t.start();
    }
}
