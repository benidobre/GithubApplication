package com.example.beni.myapplication.db;

import android.provider.BaseColumns;

/**
 * Created by beni on 5/2/2017.
 */

public interface DbContract {
    interface Profile extends BaseColumns{
        String TABLE = "profile";

    }
    interface Repository extends  BaseColumns{
        String TABLE = "repositories";
        String ID = "id";
        String NAME = "name";

        String URL = "url";
        String HTML_URL = "html_url";
        String IS_PUBLIC = "is_public";
//        String OWNER_ID = "owner_id";


    }
}
