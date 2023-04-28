package a0547110.tees.ac.uk.eatwell;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="FavouriteShop";
    private static final  int DB_VERSION = 1;
    private Context context;

    public SQLiteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db,oldVersion,newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 1){
            db.execSQL("CREATE TABLE favourite(Placeid TEXT PRIMARY KEY,Userid TEXT);");
        }


    }
    public static void insertitem(SQLiteDatabase db,String Placeid,String Userid){
        ContentValues itemValues = new ContentValues();
        itemValues.put("Placeid",Placeid);
        itemValues.put("Userid",Userid);
        db.insert("favourite",null,itemValues);
    }


}
