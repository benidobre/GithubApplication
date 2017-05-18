package com.example.beni.myapplication.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class GithubContentProvider extends ContentProvider {
    private MySqlHelper mySqlHelper;

    interface Entities {
        int REPOSITORIES = 0;
        int REPOSITORY = 1;
        int PROFILES = 2;
        int PROFILE = 3;
    }

    private static final String AUTHORITY = "com.example.beni.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri REPOSITORY_URI = CONTENT_URI.buildUpon()
            .appendPath("repositories")
            .build();

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(AUTHORITY, "repositories", Entities.REPOSITORIES);

        sUriMatcher.addURI(AUTHORITY, "repositories/#", Entities.REPOSITORY);
    }

    public GithubContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {


        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES:
                return "vnd.android.cursor.dir/vnd.example.beni.repository";
            case Entities.REPOSITORY:
                return "vnd.android.cursor.item/vnd.example.beni.repository";
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        long id;
        Uri modifiedUri = GithubContentProvider.CONTENT_URI;
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES:
            case Entities.REPOSITORY:
                id = database.insertOrThrow(DbContract.Repository.TABLE, null, values);
                modifiedUri = modifiedUri.buildUpon()
                        .appendPath(DbContract.Repository.TABLE)
                        .build();
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        if (id >= 0) {
            modifiedUri = ContentUris.withAppendedId(modifiedUri, id);
            getContext().getContentResolver().notifyChange(modifiedUri, null);
            return modifiedUri;
        }
        return null;

    }

    @Override
    public boolean onCreate() {

        mySqlHelper = new MySqlHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (TextUtils.isEmpty(selection))
            selection = "1";
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORIES:
                return mySqlHelper.getReadableDatabase().query(DbContract.Repository.TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
            case Entities.REPOSITORY:
                String sqlSelection = "AND" + DbContract.Repository.ID + "=" + uri.getLastPathSegment();
                return mySqlHelper.getReadableDatabase().query(DbContract.Repository.TABLE,
                        projection, selection + sqlSelection, selectionArgs, null, null, sortOrder);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount;
        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
        if (TextUtils.isEmpty(selection))
            selection = "1";
        switch (sUriMatcher.match(uri)) {
            case Entities.REPOSITORY:
                selection += " AND " + DbContract.Repository.ID + "=" + uri.getLastPathSegment();

            case Entities.REPOSITORIES:
                updateCount = database.update(DbContract.Repository.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
