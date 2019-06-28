package com.example.kiit.echo.databases

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kiit.echo.Songs

class EchoDatabase:SQLiteOpenHelper{

    var _songsList=ArrayList<Songs>()



    object Statified{
        val DB_VERSION=13
        val DB_NAME="FavouriteDatabase"
        val TABLE_NAME="FavouriteTable"
        val COLUMN_ID="SongID"
        val COLUMN_SONG_TITLE="SongTitle"
        val COLUMN_SONG_ARTIST="SongArtist"
        val COLUMN_SONG_PATH="SongPath"

    }

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory, version: Int) : super(context, name, factory, version) {}

    constructor(context: Context?) : super(context, EchoDatabase.Statified.DB_NAME, null, EchoDatabase.Statified.DB_VERSION) {}

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + EchoDatabase.Statified.TABLE_NAME + "( " + EchoDatabase.Statified.COLUMN_ID +
                " INTEGER," + EchoDatabase.Statified.COLUMN_SONG_ARTIST + " STRING," + EchoDatabase.Statified.COLUMN_SONG_TITLE + " STRING,"
                + EchoDatabase.Statified.COLUMN_SONG_PATH + " STRING);")
//check such lines carefully , exact space should be there
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    fun storeasFavourite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(EchoDatabase.Statified.COLUMN_ID, id)
        contentValues.put(EchoDatabase.Statified.COLUMN_SONG_ARTIST, artist)
        contentValues.put(EchoDatabase.Statified.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(EchoDatabase.Statified.COLUMN_SONG_PATH, path)
        db.insert(EchoDatabase.Statified.TABLE_NAME, null, contentValues)
        db.close()
    }

    //love

    fun queryDBforList(): ArrayList<Songs>? {
        try {

            val db = this.readableDatabase
            val query_params = "SELECT " + "*" + " FROM " + EchoDatabase.Statified.TABLE_NAME
            val cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(EchoDatabase.Statified.COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Statified.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Statified.COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Statified.COLUMN_SONG_PATH))
                    _songsList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                } while (cSor.moveToNext())
            } else {
                return null
            }

        } catch (e: Exception) {
            e.printStackTrace()

        }

        return _songsList

    }


    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        val query_params = "SELECT " + "*" + " FROM " + EchoDatabase.Statified.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                counter++
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }

    fun checkifIdExists(_id: Int): Boolean {
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + EchoDatabase.Statified.TABLE_NAME + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(EchoDatabase.Statified.COLUMN_ID))


            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
    }

    fun deleteFavourite(_id: Int) {
        val db = this.writableDatabase
        db.delete(EchoDatabase.Statified.TABLE_NAME, EchoDatabase.Statified.COLUMN_ID + "=" + _id, null)
        db.close()
    }


}


