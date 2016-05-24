package newapp.test.test.popularmovieapp1.db;

import android.provider.BaseColumns;

/**
 * Created by badarinadh on 5/12/2016.
 */
public final class DatabaseContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
// give it an empty constructor.
    private DatabaseContract() {
    }

    public static abstract class Table1 implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_NAME = "name";
        public static final String POSTER_IMAGE_URL = "poster_image_url";
        public static final String BACKDROP_IMAGE_URL = "backdrop_image_url";
        public static final String RATTING = "ratting";
        public static final String RELEASE_DATE = "release_date";
        public static final String VOTES = "votes";
        public static final String TRAILER_URL = "trailer_url";
        public static final String DESCRIPTION = "description";
        public static final String REVIEW = "review";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                MOVIE_NAME + TEXT_TYPE + COMMA_SEP +
                POSTER_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                BACKDROP_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                RATTING + TEXT_TYPE + COMMA_SEP +
                RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                VOTES + TEXT_TYPE + COMMA_SEP +
                TRAILER_URL + TEXT_TYPE + COMMA_SEP +
                DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                REVIEW + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}