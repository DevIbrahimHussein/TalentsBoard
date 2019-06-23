package com.example.talentsboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private var url: String= "http://10.0.2.2/TalentsboardAndroidsqlConnect/User.php"
    private var first_name : TextView?= null
    private var second_name : TextView?= null
    private var followers : TextView?= null
    private var following : TextView?= null
    private var country : TextView?= null
    private var my_videos : TextView?= null
    private var saved_videos : TextView?= null
    private var history : TextView?= null
    private var user_id : Int?=null
    private var liked_videos : TextView?= null
    private lateinit var listView: ListView
    private var edit_profile : Button?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        user_id = 6
        first_name=view?.findViewById<TextView>(R.id.profile_firstname)
        second_name=view?.findViewById<TextView>(R.id.profile_secondname)
        country=view?.findViewById<TextView>(R.id.profile_country)
        followers=view?.findViewById<TextView>(R.id.followers)
        following = view?.findViewById<TextView>(R.id.followings)
        edit_profile= view?.findViewById<Button>(R.id.edit_profile)
        my_videos = view?.findViewById<TextView>(R.id.myvideotab)
        saved_videos = view?.findViewById<TextView>(R.id.saved_videos)
        history = view?.findViewById<TextView>(R.id.history)
        liked_videos=view?.findViewById<TextView>(R.id.liked_videos)

        Fuel.get(url!!, listOf("user" to user_id)).responseJson{ request, response, result ->
            try{
                first_name!!.text=result.get().array().getJSONObject(0).getString("first_name").toString()
                second_name!!.text=result.get().array().getJSONObject(0).getString("last_name").toString()
                country!!.text=result.get().array().getJSONObject(0).getString("country").toString()
            } catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }

        }

        Fuel.get(url!!, listOf("following" to user_id)).responseJson{ request, response, result ->
            try{
                following!!.text=result.get().content
            } catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }

        }

        Fuel.get(url!!, listOf("followers" to user_id)).responseJson{ request, response, result ->
            try{
                followers!!.text=result.get().content
            } catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }
        }

        Fuel.get(url!!, listOf("QuestAndAns" to user_id)).responseJson{ request, response, result ->

            try{
                listView = view?.findViewById<ListView>(R.id.profie_listview)!!
                val listItems = arrayOfNulls<String>(result.get().array().getJSONObject(0).names().length())

                for (i in 0 until result.get().array().getJSONObject(0).names().length()) {

                    listItems[i] = result.get().array().getJSONObject(0).names()[i].toString()+" : "+ result.get().array().getJSONObject(0).getString(result.get().array().getJSONObject(0).names()[i].toString())
                }

                val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter
            } catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            }

        }


        edit_profile?.setOnClickListener {

            val fragment = Edit_profile()
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        profile_save.setOnClickListener {

            val fragment = Complete_profile()
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }
        my_videos!!.setOnClickListener{

            val fragment = Videos_library_Fragment("my_videos")
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        history!!.setOnClickListener{

            val fragment = Videos_library_Fragment("history")
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        saved_videos!!.setOnClickListener{

            val fragment = Videos_library_Fragment("saved_videos")
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        liked_videos!!.setOnClickListener{

            val fragment = Videos_library_Fragment("liked_videos")
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.linear_main, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

    }


}
