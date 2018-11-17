package com.sanxynet.bakingapp.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class ContentProvider extends android.content.ContentProvider {

    private static final int RECIPES = 100;
    private static final int RECIPE_WITH_ID = 101;

    private static final int INGREDIENTS = 200;
    private static final int INGREDIENT_WITH_ID = 201;

    private static final int STEPS = 300;
    private static final int STEP_WITH_ID = 301;


    private static final UriMatcher sUriMatMATCHER = buildURIMatcher();

    private static UriMatcher buildURIMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_RECIPES + "/#", RECIPE_WITH_ID);

        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_INGREDIENTS, INGREDIENTS);
        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_INGREDIENTS + "/#", INGREDIENT_WITH_ID);

        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_STEPS, STEPS);
        uriMatcher.addURI(BakingAppContract.CONTENT_AUTHORITY, BakingAppContract.PATH_STEPS + "/#", STEP_WITH_ID);


        return uriMatcher;
    }


    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new DbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String mSelection;
        String[] mSelectionArgs;

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatMATCHER.match(uri);

        Cursor returnCursor;

        switch (match) {

            case RECIPES:

                returnCursor = db.query(BakingAppContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case RECIPE_WITH_ID:

                String id = uri.getPathSegments().get(1);

                mSelection = " = ? ";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(BakingAppContract.RecipeEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INGREDIENTS:

                returnCursor = db.query(BakingAppContract.IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case INGREDIENT_WITH_ID:

                id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(BakingAppContract.IngredientEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case STEPS:
                returnCursor = db.query(BakingAppContract.StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case STEP_WITH_ID:

                id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(BakingAppContract.StepEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if (getContext() != null) {

            returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatMATCHER.match(uri);

        switch (match) {

            case RECIPES:
                return BakingAppContract.RecipeEntry.CONTENT_TYPE;

            case RECIPE_WITH_ID:
                return BakingAppContract.RecipeEntry.CONTENT_ITEM_TYPE;

            case INGREDIENTS:
                return BakingAppContract.IngredientEntry.CONTENT_TYPE;

            case INGREDIENT_WITH_ID:
                return BakingAppContract.IngredientEntry.CONTENT_ITEM_TYPE;

            case STEPS:
                return BakingAppContract.StepEntry.CONTENT_TYPE;

            case STEP_WITH_ID:
                return BakingAppContract.StepEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long id;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);

        Uri returnUri;

        switch (match) {

            case RECIPES:

                // insert values into recipes table
                id = db.insert(BakingAppContract.RecipeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(BakingAppContract.RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }
                break;

            case INGREDIENTS:
                // insert values into table
                id = db.insert(BakingAppContract.IngredientEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(BakingAppContract.IngredientEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }
                break;

            case STEPS:

                // insert values into table
                id = db.insert(BakingAppContract.StepEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(BakingAppContract.StepEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
        if (getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int id;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);

        int recordDelete;

        switch (match) {

            case RECIPES:
                recordDelete = db.delete(BakingAppContract.RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case RECIPE_WITH_ID:
                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(BakingAppContract.RecipeEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;


            case INGREDIENTS:
                recordDelete = db.delete(BakingAppContract.IngredientEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case INGREDIENT_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(BakingAppContract.IngredientEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case STEPS:
                recordDelete = db.delete(BakingAppContract.StepEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case STEP_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(BakingAppContract.StepEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if ((getContext() != null) && (recordDelete != 0)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return recordDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int id;
        int rowsUpdate;

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);


        switch (match) {

            case RECIPES:
                rowsUpdate = db.update(BakingAppContract.RecipeEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case RECIPE_WITH_ID:
                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(BakingAppContract.RecipeEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case INGREDIENTS:
                rowsUpdate = db.update(BakingAppContract.IngredientEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case INGREDIENT_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(BakingAppContract.IngredientEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case STEPS:
                rowsUpdate = db.update(BakingAppContract.StepEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case STEP_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(BakingAppContract.StepEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if (rowsUpdate != 0) {
            if (getContext() != null) {

                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdate;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);


        int rowsInserted;
        switch (match) {

            case RECIPES:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // insert all data
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }

                        long _id = db.insertOrThrow(BakingAppContract.RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert %s", e.getMessage());
                } finally {
                    // execute after ..... when is complete
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case INGREDIENTS:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // insert all data
                    for (ContentValues value : values) {

                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }


                        long _id = db.insertOrThrow(BakingAppContract.IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert %s", e.getMessage());
                } finally {
                    // execute after ..... when is complete
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case STEPS:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    for (ContentValues value : values) {

                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }

                        long _id = db.insertOrThrow(BakingAppContract.StepEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert %s", e.getMessage());
                } finally {
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }

    }
}
