package com.example.beni.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.style.UnderlineSpan;


import com.example.beni.myapplication.model.GitHub;
import com.example.beni.myapplication.model.GithubProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mProfilePicture;
    private TextView mName;
    private TextView mOrganization;
    private TextView mBio;
    private TextView mLocation;
    private TextView mEmail;
    private TextView mCreated;
    private TextView mUpdated;
    private TextView mPublicRepos;
    private TextView mPrivateRepos;
    private GithubProfile mDisplayedProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfilePicture = (ImageView) findViewById(R.id.me_image);
        mName = (TextView) findViewById(R.id.me_name);
        mOrganization = (TextView) findViewById(R.id.work);
        mBio = (TextView) findViewById(R.id.bio);
        mLocation = (TextView) findViewById(R.id.location);
        mEmail = (TextView) findViewById(R.id.email);
        mCreated = (TextView) findViewById(R.id.created);
        mUpdated = (TextView) findViewById((R.id.updated));
        mPublicRepos = (TextView) findViewById(R.id.publi);
        mPrivateRepos = (TextView) findViewById(R.id.privat);
        findViewById(R.id.view_blog).setOnClickListener( this);

        fetchProfile();

    }
    private void fetchProfile(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<GithubProfile> profileCall = GitHub.Service.get().getUserProfile(preferences.getString("auth",null));
        profileCall.enqueue(new Callback<GithubProfile>() {
            @Override
            public void onResponse(Call<GithubProfile> call, Response<GithubProfile> response) {
                if(response.isSuccessful()){
                    GithubProfile profile = response.body();
                    updateUI(profile);
                }else{
                    Toast.makeText(ProfileActivity.this,"esti praf",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GithubProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,"esti si mai praf",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(GithubProfile profile) {
        mDisplayedProfile = profile;
        DateFormat df = new DateFormat();
        mProfilePicture.setImageResource(R.drawable.b);
        mName.setText(profile.getName());
        mOrganization.setText(profile.getCompany());
        mBio.setText(profile.getBio());
        setTextUnderlined(mLocation, profile.getLocation());
        setTextUnderlined(mEmail, profile.getEmail());
        setTextUnderlined(mCreated, profile.getCreatedAt());
        setTextUnderlined(mUpdated, profile.getUpdatedAt());
        setTextUnderlined(mPublicRepos, profile.getPublicGists().toString());
        setTextUnderlined(mPrivateRepos, profile.getPrivateGists().toString());
    }

    private void setTextUnderlined(TextView textView, String text) {
        if(!TextUtils.isEmpty(text)) {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu,menu);
        return true;
    }
    // de adaugat onclick pt view blog
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                preferences.edit().remove("auth").apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_blog:
                Intent intent = new Intent(v.getContext(), RepositoriesActivity.class);
                startActivity(intent);

                break;
        }
    }
}
