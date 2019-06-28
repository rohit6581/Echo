package com.example.kiit.echo.fragments


import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.example.kiit.echo.R
import kotlinx.android.synthetic.main.fragment_about_us.*
import android.content.Intent
import android.net.Uri
import android.widget.*
import android.content.pm.PackageManager




/**
 * A simple [Fragment] subclass.
 *
 */
class AboutUsFragment : Fragment() {
    var myActivity: Activity?=null

    var fb:Button?=null
    var linkedin:Button?=null
    var git:Button?=null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fb?.setOnClickListener({

            val facebookIntent = Intent(Intent.ACTION_VIEW)
            val facebookUrl = getFacebookPageURL(requireContext())
            facebookIntent.data = Uri.parse(facebookUrl)
            startActivity(facebookIntent)

        })

        linkedin?.setOnClickListener({
            val linkedinIntent = Intent(Intent.ACTION_VIEW)
            val linkedinUrl = getLinkedinURL(requireContext())
            linkedinIntent.data = Uri.parse(linkedinUrl)
            startActivity(linkedinIntent)

        })

        git?.setOnClickListener({
            val gitIntent = Intent(Intent.ACTION_VIEW)
            val gitUrl = getgitURL(requireContext())
            gitIntent.data = Uri.parse(gitUrl)
            startActivity(gitIntent)

        })


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_about_us, container, false)

        activity?.title = "About Me"
        fb=view?.findViewById(R.id.facebook)
        linkedin=view?.findViewById(R.id.linkedin)
        git=view?.findViewById(R.id.git)//=view?.findViewById(R.id.switchShake)


        return view
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

     override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item =menu?.findItem(R.id.action_sort)
        item?.isVisible=false

    }

    var FACEBOOK_URL = "https://www.facebook.com/rohit022?ref=bookmarks"
    var FACEBOOK_PAGE_ID = "rohit022"
    var LINK_URL = "https://www.linkedin.com/in/rohit-raj-4bb0a2161/"
    var GIT_URL="https://github.com/rohit6581"

    //method to get the right URL to use in the intent

    fun getLinkedinURL(context: Context):String{
        return LINK_URL
    }

    fun getgitURL(context: Context):String{
        return GIT_URL
    }


    fun getFacebookPageURL(context: Context): String {
        val packageManager = context.packageManager
        try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            return if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=$FACEBOOK_URL"
            } else { //older versions of fb app
                "fb://page/$FACEBOOK_PAGE_ID"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return FACEBOOK_URL //normal web url
        }

    }




}
