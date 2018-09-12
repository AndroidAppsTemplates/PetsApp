package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String TAG = "PetProvider";

    /**
     * URI match constants
     */
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Database connection
     */
    private PetDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        String petName = values.getAsString(PetEntry.COLUMN_PET_NAME);
        Integer petGender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        Integer petWeight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        if(petName == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        if(petGender == null || !PetEntry.isValidGender(petGender)) {
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        if(petWeight != null && petWeight < 0) {
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(PetEntry.TABLE_NAME, null, values);

        if(id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // If updating the whole table
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);

            // If updating specific row
            case PET_ID:
                selection = PetEntry._ID + "=?";
                // Extract the id value from the Uri
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Return early if contentValues is empty
        if(contentValues.size() == 0) {
            return 0;
        }

        if(contentValues.containsKey(PetEntry.COLUMN_PET_NAME)) {
            if(contentValues.getAsString(PetEntry.COLUMN_PET_NAME) == null) {
                throw new IllegalArgumentException("Pet requires a valid name");
            }
        }

        if(contentValues.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer petGender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if(petGender == null || !PetEntry.isValidGender(petGender)) {
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }

        if(contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer petWeight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if(petWeight != null && petWeight < 0) {
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}