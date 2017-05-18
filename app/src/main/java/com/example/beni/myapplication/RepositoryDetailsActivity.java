package com.example.beni.myapplication;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.beni.myapplication.model.Repository;

public class RepositoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Repository repository = new Repository();

        repository.setPrivate(!getIntent().getBooleanExtra("is_public",true));
        repository.setUrl(getIntent().getStringExtra("url"));
        repository.setHtmlUrl(getIntent().getStringExtra("html_url"));

        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentById(android.R.id.content) == null){
            fm.beginTransaction()
                    .replace(android.R.id.content, RepositoryDetailsFragment.New(repository))
                    .commit();
        }
    }
}
