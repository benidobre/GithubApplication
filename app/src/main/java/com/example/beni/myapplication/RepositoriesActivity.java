package com.example.beni.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beni.myapplication.db.DbContract;
import com.example.beni.myapplication.db.GithubContentProvider;
import com.example.beni.myapplication.db.MySqlHelper;
import com.example.beni.myapplication.model.GitHub;
import com.example.beni.myapplication.model.GithubProfile;
import com.example.beni.myapplication.model.Repositori;
import com.example.beni.myapplication.model.Repository;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoriesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private boolean mCanShowDetails = false;
    private Adapter mAdapter;
    private SQLiteDatabase mDbConnection;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);
        mCanShowDetails = (findViewById(R.id.container) != null);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(new Adapter.CallBack() {
            @Override
            public void show(Repository repository) {
                if(mCanShowDetails){
                    Fragment details = RepositoryDetailsFragment.New(repository);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container,details)
                            .commit();
                }else{
                    Intent intent = new Intent(RepositoriesActivity.this, RepositoryDetailsActivity.class);
                    intent.putExtra("is_public",!repository.getPrivate());
                    intent.putExtra("url",repository.getUrl());
                    intent.putExtra("html_url",repository.getHtmlUrl());
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        MySqlHelper mMySqlHelper = new MySqlHelper(this);
        mDbConnection = mMySqlHelper.getWritableDatabase();

        updateUIFromDb();
        fetchRepositories();






    }

    private void updateUIFromDb() {
        Cursor cursor = getContentResolver().query(GithubContentProvider.REPOSITORY_URI,null,null,null,null);
//                mDbConnection.query(DbContract.Repository.TABLE,null,null,null,null,
//                null,null,null);
        if(cursor != null){

            if(cursor.moveToFirst() && cursor.getCount()> 0){

                List<Repository> mRepos = new ArrayList<>();
                int idIndex = cursor.getColumnIndex(DbContract.Repository.ID);
                int nameIndex = cursor.getColumnIndex(DbContract.Repository.NAME);
                int isPublicIndex = cursor.getColumnIndex(DbContract.Repository.IS_PUBLIC);
                int htmlUrlIndex = cursor.getColumnIndex(DbContract.Repository.HTML_URL);
                int urlIndex = cursor.getColumnIndex(DbContract.Repository.URL);
                do{
                    Repository repository = new Repository();
                    repository.setId(cursor.getInt(idIndex));
                    repository.setName(cursor.getString(nameIndex));
                    repository.setPrivate(cursor.getInt(isPublicIndex) == 0);
                    repository.setUrl(cursor.getString(urlIndex));

                    repository.setHtmlUrl(cursor.getString(htmlUrlIndex));
                    mRepos.add(repository);
                }while(cursor.moveToNext());
                mAdapter.setmData(mRepos);
                mAdapter.notifyDataSetChanged();

                cursor.close();
            }
        }
    }

    private void fetchRepositories(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Call<List<Repository>> repoCall = GitHub.Service.get().getRepos(preferences.getString("auth",null));
        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if(response.isSuccessful()){

                    List<Repository> repos = response.body();
                    handleNetworkResponse(repos);


                }else {
                    Toast.makeText(RepositoriesActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RepositoriesActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNetworkResponse(List<Repository> repos){
        for(Repository repo : repos){
            ContentValues values = new ContentValues();
            values.put(DbContract.Repository.ID, repo.getId());
            values.put(DbContract.Repository.NAME, repo.getName());
            values.put(DbContract.Repository.URL, repo.getUrl());
            values.put(DbContract.Repository.HTML_URL, repo.getHtmlUrl());
            values.put(DbContract.Repository.IS_PUBLIC, !repo.getPrivate());

            try {
                getContentResolver().insert(GithubContentProvider.REPOSITORY_URI, values);
            }catch(SQLException e){

                String selection = DbContract.Repository.ID + "=" + repo.getId();
                getContentResolver().update(GithubContentProvider.REPOSITORY_URI, values,selection,null);
            }
        }
        updateUIFromDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to close the link to the database
        if (mDbConnection != null) {
            mDbConnection.close();
        }
    }

    static class Adapter extends RecyclerView.Adapter{
        List<Repository> mData;
        boolean mCanShowDetails;
        CallBack mCallBack;

        Adapter(CallBack c){
            mCallBack = c;
        }

        public interface CallBack{
            void show(Repository repository);
        }
        public void setmData(List<Repository> mData) {
            this.mData = mData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_repository,parent,false);
            return new ViewHolder(view);
            }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ViewHolder)holder).bind(mData.get(position), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mCallBack.show(mData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mName;

            public ViewHolder(View itemView) {
                super(itemView);

                mName = (TextView)itemView.findViewById(R.id.name);

            }
            public void bind(Repository repository, View.OnClickListener onClickListener){

                mName.setText(repository.getName());
                itemView.setOnClickListener(onClickListener);
//                for(String topic: repository.getTopics()){
//                    TextView topicTextView = new TextView(itemView.getContext());
//                    topicTextView.setText(topic);
//                    mTopics.addView(topicTextView);
//                }
            }
        }
    }




    }

