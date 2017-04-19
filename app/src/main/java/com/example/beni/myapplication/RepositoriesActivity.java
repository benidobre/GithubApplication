package com.example.beni.myapplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import com.example.beni.myapplication.model.GitHub;
import com.example.beni.myapplication.model.GithubProfile;
import com.example.beni.myapplication.model.Repositori;
import com.example.beni.myapplication.model.Repository;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoriesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Call<List<Repository>> repoCall = GitHub.Service.get().getRepos(preferences.getString("auth",null));
        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if(response.isSuccessful()){

                    List<Repository> repos = response.body();
                    Adapter adapter = new Adapter();
                    adapter.setmData(repos);
                    mRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {

            }
        });



    }

    static class Adapter extends RecyclerView.Adapter{
        List<Repository> mData;

        public void setmData(List<Repository> mData) {
            this.mData = mData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_repository,parent,false);
            return new ViewHolder(view);
            }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mNameAndOwner;
            private final TextView mCountWatchers;
            private final TextView mDescription;
            private final CheckBox mIsPublic;
            private final LinearLayout mTopics;

            public ViewHolder(View itemView) {
                super(itemView);
                mCountWatchers = (TextView)itemView.findViewById(R.id.watchers);
                mNameAndOwner = (TextView)itemView.findViewById(R.id.project_name);
                mDescription = (TextView)itemView.findViewById(R.id.project_description);
                mIsPublic = (CheckBox)itemView.findViewById(R.id.is_public);
                mTopics = (LinearLayout)itemView.findViewById(R.id.expand_layout);

            }
            public void bind(Repository repository){
                if(repository.getWatchersCount()>0)
                    mCountWatchers.setText(String.valueOf(repository.getWatchersCount()));
                else{
                    mCountWatchers.setText("0");
                }
                mNameAndOwner.setText(itemView.getContext().getString(R.string.repo_name_owner,repository.getName(),repository.getOwner()));
                if(repository.getDescription() != null)
                    mDescription.setText(repository.getDescription().toString());
                mIsPublic.setChecked(!repository.getPrivate());

                mTopics.removeAllViews();
//                for(String topic: repository.getTopics()){
//                    TextView topicTextView = new TextView(itemView.getContext());
//                    topicTextView.setText(topic);
//                    mTopics.addView(topicTextView);
//                }
            }
        }
    }




    }

