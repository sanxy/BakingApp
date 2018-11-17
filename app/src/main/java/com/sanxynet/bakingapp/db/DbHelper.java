package com.sanxynet.bakingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import timber.log.Timber;

class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "baking.db";

    private final Context mContext;

    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_RECIPE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + BakingAppContract.RecipeEntry.TABLE_NAME + " (" +
                        BakingAppContract.RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                        BakingAppContract.RecipeEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                        BakingAppContract.RecipeEntry.COLUMN_NAME_SERVINGS + " REAL NOT NULL, " +
                        BakingAppContract.RecipeEntry.COLUMN_NAME_IMAGE + " TEXT " +
                        ");";

        final String SQL_CREATE_INGREDIENT_TABLE =
                "CREATE TABLE IF NOT EXISTS " + BakingAppContract.IngredientEntry.TABLE_NAME + " (" +

                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID + " INTEGER NOT NULL, " +

                        BakingAppContract.IngredientEntry.COLUMN_NAME_QUANTITY + " REAL NOT NULL, " +
                        BakingAppContract.IngredientEntry.COLUMN_NAME_MEASURE + " TEXT NOT NULL, " +
                        BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT + " TEXT NOT NULL, " +

                        " FOREIGN KEY (" + BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID + ") REFERENCES " +
                        BakingAppContract.RecipeEntry.TABLE_NAME + "(" + BakingAppContract.RecipeEntry._ID + ")," +

                        " UNIQUE (" + BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID + "," + BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT + ")" + " ON CONFLICT REPLACE " +
                        ");";

        final String SQL_CREATE_STEP_TABLE =
                "CREATE TABLE IF NOT EXISTS " + BakingAppContract.StepEntry.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        BakingAppContract.StepEntry.COLUMN_NAME_ID + " INTEGER NOT NULL, " +
                        BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + " INTEGER NOT NULL, " +
                        BakingAppContract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                        BakingAppContract.StepEntry.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                        BakingAppContract.StepEntry.COLUMN_NAME_VIDEO_URL + " TEXT NOT NULL, " +
                        BakingAppContract.StepEntry.COLUMN_NAME_THUMBNAIL_URL + " TEXT NOT NULL, " +

                        " FOREIGN KEY (" + BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + ") REFERENCES " +
                        BakingAppContract.RecipeEntry.TABLE_NAME + "(" + BakingAppContract.RecipeEntry._ID + "), " +

                        " UNIQUE (" + BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + "," + BakingAppContract.StepEntry.COLUMN_NAME_DESCRIPTION + ")" + " ON CONFLICT REPLACE " +
                        ");";


        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        db.execSQL(SQL_CREATE_STEP_TABLE);

        Timber.d("SQL STATEMENT:  " + SQL_CREATE_RECIPE_TABLE + " " + SQL_CREATE_INGREDIENT_TABLE + " " + SQL_CREATE_STEP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.IngredientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.StepEntry.TABLE_NAME);
        onCreate(db);
        new DataUtils(mContext).clearPreferenceDb();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.IngredientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BakingAppContract.StepEntry.TABLE_NAME);
        onCreate(db);
        new DataUtils(mContext).clearPreferenceDb();
    }
}
