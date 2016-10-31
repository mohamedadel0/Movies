package com.example.muhammad_adel.final_project_phase1_version2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


 public class FilmData {

        DataBase dataBase;
        FilmData(Context c) {
            dataBase = new DataBase(c);
        }
     public long insertData(String filmsPoster, String title, String releaseData, String voteAverage, String overview, String trailers[], String[] authorsForReviews, String[] reviews) {
         SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put(dataBase.posters, filmsPoster);
         contentValues.put(dataBase.title, title);
         contentValues.put(dataBase.releaseData, releaseData);
         contentValues.put(dataBase.voteAverage, voteAverage);
         contentValues.put(dataBase.overview, overview);
         contentValues.put(dataBase.trailers, ConvertTypes.convertArrayToString(trailers));
         contentValues.put(dataBase.authorsForReviews, ConvertTypes.convertArrayToString(authorsForReviews));
         contentValues.put(dataBase.reviews, ConvertTypes.convertArrayToString(reviews));
         long row = sqLiteDatabase.insert(dataBase.tableName, null, contentValues);
         return row;
     }
     public Cursor getAllData() {
         String columns[];
         SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
                  columns = new String[] {dataBase.posters, dataBase.title, dataBase.releaseData, dataBase.voteAverage, dataBase.overview, dataBase.trailers, dataBase.authorsForReviews, dataBase.reviews};
        Cursor cursor = sqLiteDatabase.query(dataBase.tableName, columns, null, null, null, null, null);
        return cursor;
     }
     public int deleteFilmFormFavourite(String filmTitle) {
         SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
         String args[] = new String[] {filmTitle};
        int count = sqLiteDatabase.delete(dataBase.tableName, dataBase.title + " =? ", args);
        return count;
     }
        private class DataBase extends SQLiteOpenHelper {
            private static final String dataBaseName = "FavouriteFilms";
            private static final String tableName = "Films";
            private static final String FID = "_id";
            private static final String posters = "posters";
            private static final String title = "title";
            private static final String releaseData = "releaseData";
            private static final String voteAverage = "voteAverage";
            private static final String overview = "overview";
            private static final String trailers = "trailers";
            private static final String authorsForReviews = "authorsForReviews";
            private static final String reviews = "reviews";
            private static final String CREATE_TABLE =
                    "CREATE TABLE " + tableName +
                            " (" + FID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            posters + " TEXT, " +
                            title + " TEXT, " +
                            releaseData + " TEXT, " +
                            voteAverage + " TEXT, " +
                            overview + " TEXT, " +
                            trailers + " TEXT, " +
                            authorsForReviews + " TEXT, " +
                            reviews + " TEXT);";
            private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
            private Context context;
            private static final int version = 22;

            public DataBase(Context context) {
                super(context, dataBaseName, null, version);
                this.context = context;
            }

            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                try {
                    sqLiteDatabase.execSQL(CREATE_TABLE);
                } catch (SQLException e) {
                    MessageForDB.message(context, e.toString());
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

                sqLiteDatabase.execSQL(DROP_TABLE);
                try {
                    onCreate(sqLiteDatabase);
                } catch (SQLException e) {
                    MessageForDB.message(context, e.toString());
                }
            }
        }
}