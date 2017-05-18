package com.example.beni.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beni.myapplication.model.GitHub;
import com.example.beni.myapplication.model.LoginData;

import okhttp3.Credentials;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.layout_login).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                closeKeyboard(v);
//                return false;
//            }
//        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("auth",null)!= null){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        Button b = (Button)findViewById(R.id.button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText t = (EditText)findViewById(R.id.pass);
                final String pass =t.getText().toString();
                t = (EditText)findViewById(R.id.user);
                final String user = t.getText().toString();
                final String authHash = Credentials.basic(user,pass);
                Call<LoginData> callable = GitHub.Service.get().checkAuth(authHash);

                callable.enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                        if(response.isSuccessful()){
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        preferences.edit().putString("auth",authHash)
                                .putString("username",user)
                                .apply();
                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);

                        startActivity(intent);}
                        else{
                            Context context = getApplicationContext();
                            CharSequence text = "Wrong password or username!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {
                        Context context = getApplicationContext();
                        CharSequence text = "Wrong password or username!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });



            }
        });

    }

    private void closeKeyboard (View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


}
