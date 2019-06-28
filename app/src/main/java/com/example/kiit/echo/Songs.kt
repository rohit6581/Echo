package com.example.kiit.echo

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable




class Songs(var songID: Long, var songTitle: String, var artist: String, var songData:
String, var dateAdded: Long) : Parcelable{


    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }


    object Statified {
        /*Here we sort the songs according to their names*/
        var nameComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)
        }
        /*Here we sort them according to the date*/
        var dateComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.dateAdded.toDouble()
            val songTwo = song2.dateAdded.toDouble()
            songTwo.compareTo(songOne)
        }
    }

    }

