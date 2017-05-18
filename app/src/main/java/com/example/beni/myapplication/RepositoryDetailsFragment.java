package com.example.beni.myapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beni.myapplication.model.Repository;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepositoryDetailsFragment extends Fragment {
    private TextView mDescription,mPublic, mUrl,mHtmlUrl;
    private Repository mRepository;

    public static Fragment New(Repository repository){
        Fragment f = new RepositoryDetailsFragment();
        Bundle args = new Bundle();
        //args.putString("description",repository.getDescription());
        args.putBoolean("is_public",!repository.getPrivate());
        args.putString("url",repository.getUrl());
        args.putString("html_url",repository.getHtmlUrl());
        f.setArguments(args);
        return f;
    }

    public RepositoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if(args != null && !args.isEmpty()){
            mRepository = new Repository();
            mRepository.setPrivate(!args.getBoolean("is_public"));
            mRepository.setUrl(args.getString("url"));
            mRepository.setHtmlUrl( args.getString("html_url"));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDescription = (TextView) view.findViewById(R.id.description);
        mPublic = (TextView) view.findViewById(R.id.is_public);
        mUrl = (TextView) view.findViewById(R.id.url);
        mHtmlUrl = (TextView) view.findViewById(R.id.html_url);

        if(mRepository != null) {
            mPublic.setText(getArguments().getBoolean("is_public") ? "Public" : "Private");
            mUrl.setText(getArguments().getString("url"));
            mHtmlUrl.setText(getArguments().getString("html_url"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repository_details, container, false);
    }

}
