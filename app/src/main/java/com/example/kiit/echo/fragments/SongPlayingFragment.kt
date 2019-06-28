package com.example.kiit.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.kiit.echo.CurrentSongHelper
import com.example.kiit.echo.R
import com.example.kiit.echo.Songs
import com.example.kiit.echo.databases.EchoDatabase

import java.util.*
import java.util.concurrent.TimeUnit


class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null


        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null

        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        var fab: ImageButton? = null

        var favoriteContent: EchoDatabase? = null

        //After launch
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"




        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaPlayer?.getCurrentPosition()
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as
                                        Long))))

                seekBar?.setProgress(getCurrent?.toInt() as Int)

                Handler().postDelayed(this, 1000)
            }

        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"


        fun clickHandler() {

            Statified.fab?.setOnClickListener({
                if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int)
                                as Boolean) {
                    Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
                    Statified.favoriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as
                            Int)
/*Toast is prompt message at the bottom of screen indicating that an
action has been performed*/
                    Toast.makeText(Statified.myActivity, "Removed from Favorites",
                            Toast.LENGTH_SHORT).show()
                } else {
/*If the song was not a favorite, we then add it to the favorites using
the method we made in our database*/
                    Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
                    Statified.favoriteContent?.storeasFavourite(Statified.currentSongHelper?.songId?.toInt(),
                            Statified.currentSongHelper?.songArtist, Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath)
                    Toast.makeText(Statified.myActivity, "Added to Favorites",
                            Toast.LENGTH_SHORT).show()
                }

            })

            Statified.shuffleImageButton?.setOnClickListener({
                var editorShuffle =
                        Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop =
                        Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                                Context.MODE_PRIVATE)?.edit()

                if (Statified.currentSongHelper?.isShuffle as Boolean) {
                    Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                    Statified.currentSongHelper?.isShuffle = false

                    editorShuffle?.putBoolean("feature", false)
                    editorShuffle?.apply()
                } else {
                    Statified.currentSongHelper?.isShuffle = true
                    Statified.currentSongHelper?.isLoop = false
                    Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                    Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    editorShuffle?.putBoolean("feature", true)
                    editorShuffle?.apply()

                    editorLoop?.putBoolean("feature", false)
                    editorLoop?.apply()
                }
            })
            Statified.nextImageButton?.setOnClickListener({
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                if (Statified.currentSongHelper?.isShuffle as Boolean) {
                    playNext("PlayNextLikeNormalShuffle")

                } else {
                    playNext("PlayNextNormal"
                    )
                }

            })
            Statified.previousImageButton?.setOnClickListener({
                Statified.currentSongHelper?.isPlaying = true

                if (Statified.currentSongHelper?.isLoop as Boolean) {

                    Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                }

                playPrevious()
            })
            Statified.loopImageButton?.setOnClickListener({

                var editorShuffle =
                        Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                        Context.MODE_PRIVATE)?.edit()
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isLoop = false
                    Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                    editorLoop?.putBoolean("feature", false)
                    editorLoop?.apply()
                } else {
                    Statified.currentSongHelper?.isLoop = true
                    Statified.currentSongHelper?.isShuffle = false
                    Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                    Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                    editorShuffle?.putBoolean("feature", false)
                    editorShuffle?.apply()
                    editorLoop?.putBoolean("feature", true)
                    editorLoop?.apply()
                }
            })
            Statified.playPauseImageButton?.setOnClickListener {
                Toast.makeText(Statified.myActivity, "play it", Toast.LENGTH_SHORT).show()
                if (Statified.mediaPlayer?.isPlaying as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true

                    //Statified.currentSongHelper?.isPlaying = false
                    Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                    Statified.mediaPlayer?.pause()
                } else {
                    Statified.currentSongHelper?.isPlaying = false
                    Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                    Statified.mediaPlayer?.seekTo(Statified.seekBar?.progress as Int)
                    Statified.mediaPlayer?.start()


                }
            }


        }

        fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {

                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {

                var randomObject = Random()

                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)

                Statified.currentPosition = randomPosition


            }

            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isLoop = false
            var nextSongs = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSongs?.songData
            Statified.currentSongHelper?.songTitle = nextSongs?.songTitle
            Statified.currentSongHelper?.songId = nextSongs?.songID as Long

            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as
                            Boolean) {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            }

        }

        fun playPrevious() {
/*Decreasing the current position by 1 to get the position of the previous song*/
            Statified.currentPosition = Statified.currentPosition - 1
/*If the current position becomes less than 1, we make it 0 as there is no index as
-1*/
            if (Statified.currentPosition == -1) {
                Statified.currentPosition = 0
            }
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isLoop = false

            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as
                            Boolean) {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

        /*Function to handle the event where the song completes playing*/
        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(Statified.myActivity,
                                Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as
                            Boolean) {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {

            var songTitleUpdated = songTitle
            var songArtistUpdated=songArtist
            if (songTitle.equals("<unknown>",true)){
                songTitleUpdated= "unknown"
            }
            if (songArtist.equals("<unknown>",true)){
                songArtistUpdated= "unknown"
            }
            Statified.songTitleView?.setText(songTitleUpdated)
            Statified.songArtistView?.setText(songArtistUpdated)
        }

        /*function used to update the time*/
        fun processInformation(mediaPlayer: MediaPlayer) {
/*Obtaining the final time*/
            val finalTime = mediaPlayer.duration
/*Obtaining the current position*/
            val startTime = mediaPlayer.currentPosition
/*Here we format the time and set it to the start time text*/
            Statified.seekBar?.max = finalTime
            Statified.startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )
/*Similar to above is done for the end time text*/
            Statified.endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )
/*Seekbar has been assigned this time so that it moves according to the time of
song*/
            Statified.seekBar?.setProgress(startTime)
/*Now this task is synced with the update song time*/
            Handler().postDelayed(Statified.updateSongTime, 2000)


        }


    }


    var mAccelaration: Float = 0f
    var mAccelarationCurrent: Float = 0f
    var mAccelarationLast: Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Now Playing"

        //Change 1


        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false

        //yaha tak
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favoriteIcon)

        Statified.fab?.alpha = 0.8f

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        Statified.favoriteContent = EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()



        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")!!.toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments!!.getParcelableArrayList("songData")
            if (_songArtist.equals("<unknown>", true)) {
                _songArtist = "unknown"
            }

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId

            Statified.currentSongHelper?.currentPosition = Statified.currentPosition


            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //change 4
       // val fromBottomBar = arguments?.get("BottomBar") as? String



        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
        } else {


            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
        }
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.mediaPlayer?.pause()
            Statified.currentSongHelper?.isPlaying = false

            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

        //change 5
        Statified.audioVisualization?.linkTo(DbmHandler.Factory.newVisualizerHandler(activity as Context, Statified.mediaPlayer?.audioSessionId as Int))
        if (Statified.mediaPlayer?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        //yahan tak
        Staticated.clickHandler()

        //var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        //Statified.audioVisualization?.linkTo(visualizationHandler)


        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                Context.MODE_PRIVATE)

        var isShuffleAllowed = prefsForShuffle?.getBoolean("feaure", false)
        if (isShuffleAllowed as Boolean) {


            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {

            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
/*Similar to the shuffle we check the value for loop activation*/
        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE)
/*Here we extract the value of preferences and check if loop was ON or not*/
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
/*If loop was activated we change the icon color and shuffle is turned OFF */
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
/*Else defaults are used*/
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            Statified.currentSongHelper?.isLoop = false
        }

        /*if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
        //above var unit: Unit? =   xtra hai
        {
            Statified.fab?.setBackgroundResource(R.drawable.favorite_on)
        } else {
            Statified.fab?.setBackgroundResource(R.drawable.favorite_off)
        }*/

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()

        //change 2
        if (Statified.mediaPlayer?.isPlaying() as Boolean) {
            Statified.currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.currentSongHelper?.isPlaying = false
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        //yaha tak
    Statified.mSensorManager?.registerListener(Statified.mSensorListener, Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
}

    override fun onPause() {
        super.onPause()
        Statified.audioVisualization?.onPause()

        //after Launch
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Statified.audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelaration=0.0f
        mAccelarationCurrent=SensorManager.GRAVITY_EARTH
        mAccelarationLast=SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()

        inflater?.inflate(R.menu.song_playing_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {

        super.onPrepareOptionsMenu(menu)

        val item:MenuItem? =menu?.findItem(R.id.action_redirect)
        item?.isVisible =true

        //change 3
        /*val item2:MenuItem? =menu?.findItem(R.id.action_sort)
        item2?.isVisible =false*/
        //yaha tak
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){

            R.id.action_redirect ->{
                Statified.myActivity?.onBackPressed()
                return false
            }

        }
        return false
    }



    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }


            override fun onSensorChanged(event: SensorEvent?) {


                val x = event!!.values[0]
                val y = event!!.values[1]
                val z = event!!.values[2]


                mAccelarationLast = mAccelarationCurrent
                mAccelarationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = mAccelarationCurrent - mAccelarationLast
                mAccelaration = mAccelaration * 0.9f + delta

                if (mAccelaration > 12) {
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }

                }

            }
        }
    }
}


