package com.example.kiit.echo.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.example.kiit.echo.Songs
import android.widget.TextView
import android.widget.Toast
import com.example.kiit.echo.CurrentSongHelper
import com.example.kiit.echo.R

import com.example.kiit.echo.fragments.SongPlayingFragment


/*This adapter class also serves the same function to act as a bridge between the single row
view and its data. The implementation is quite similar to the one we did
* for the navigation drawer adapter*/
class MainScreenAdapter(_songDetails: ArrayList<Songs>, _context: Context) :
        RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {
    /*Local variables used for storing the data sent from the fragment to be used in the
    adapter
    * These variables are initially null*/
    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null
    /*In the init block we assign the data received from the params to our local
    variables*/
    var mediaPlayer: MediaPlayer? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val songObject = songDetails?.get(position)




        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist
            holder.contentHolder?.setOnClickListener({


                val songPlayingFragment = SongPlayingFragment()
                var args = Bundle() //val
                args.putString("songArtist", songObject?.artist)
                args.putString("path", songObject?.songData)
                args.putString("songTitle", songObject?.songTitle)
                args.putInt("SongId", songObject?.songID?.toInt() as Int)
                args.putInt("songPosition", position)
                args.putParcelableArrayList("songData", songDetails)
                songPlayingFragment.arguments = args

                (mContext as FragmentActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, songPlayingFragment)
                        .addToBackStack("SongPlayingFragment")
                        .commit()

            })
    }
    /*This has the same implementation which we did for the navigation drawer adapter*/
    override  fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {

        if(songDetails == null) {

            return 0
        }

        else {

            return (songDetails as ArrayList<Songs>).size
        }
    }
    /*Every view holder class we create will serve the same purpose as it did when we
    created it for the navigation drawer*/
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*Declaring the widgets and the layout used*/
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null
        /*Constructor initialisation for the variables*/
        init {
            trackTitle = view.findViewById(R.id.trackTitle) as TextView
            trackArtist = view.findViewById(R.id.trackArtist) as TextView
            contentHolder = view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }
}