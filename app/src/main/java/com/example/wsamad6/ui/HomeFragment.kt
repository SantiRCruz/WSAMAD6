package com.example.wsamad6.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.navigation.fragment.findNavController
import com.example.wsamad6.R
import com.example.wsamad6.core.Constants
import com.example.wsamad6.data.get
import com.example.wsamad6.data.models.History
import com.example.wsamad6.databinding.ActivityHomeBinding
import com.example.wsamad6.databinding.FragmentHomeBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var  binding : FragmentHomeBinding
    private val historyList = mutableListOf<History>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        obtainActualDate()
        obtainCases()
        obtainHistory()
        clicks()


    }

    private fun clicks() {
        binding.imgQr.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_qrFragment) }
        binding.imgMap.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_mapFragment) }
        binding.btnCheck.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_checkListFragment) }
    }

    private fun obtainHistory() {
        val sharedPreferences = requireContext().getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id","")
        val name = sharedPreferences.getString("name","")
        setNames(name!!)
        Constants.OKHTTP.newCall(get("symptoms_history?user_id=$id")).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")){
                    val data  = json.getJSONArray("data")
                    for(i in 0 until data.length()){
                        val item  = data.getJSONObject(i)
                        historyList.add(History(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getString("date")),item.getInt("probability_infection")))
                    }
                    val finalData = historyList[data.length()-1]
                    requireActivity().runOnUiThread {
                        binding.txtMonthDay.text  = SimpleDateFormat("MM/dd").format(finalData.date)
                        binding.txtYearHour.text  = SimpleDateFormat("/yyyy KK:mma").format(finalData.date)
                        if (finalData.probability_infection>50){
                            binding.txtTitleWithData.text  = "CALL TO DOCTOR"
                            binding.llBgWithData.backgroundTintList = getColorStateList(requireContext(),R.color.dark_blue)
                            binding.txtMessageWithData.text = "You may be infected with a virus"
                        }else{
                            binding.txtTitleWithData.text  = "CLEAR"
                            binding.llBgWithData.backgroundTintList = getColorStateList(requireContext(),R.color.blue_200)
                            binding.txtMessageWithData.text = "* Wear mask. Keep 2m distance. Wash hands."
                        }
                        binding.llWithData.visibility = View.VISIBLE
                        binding.llWithData1.visibility = View.VISIBLE
                    }
                }else{
                    requireActivity().runOnUiThread {
                        binding.llNoData.visibility = View.VISIBLE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setNames(s:String) {
        binding.txtName.text = s
    }

    private fun obtainCases() {
        Constants.OKHTTP.newCall(get("cases")).enqueue(object  : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data =  (0..20).random()
                if (data>0){
                    requireActivity().runOnUiThread {
                        binding.txtCases.text = "$data Cases"
                        binding.txtCases1.text = "$data Cases"
                        binding.llCases.backgroundTintList = getColorStateList(requireContext(),R.color.dark_blue)
                        binding.llCases1.backgroundTintList = getColorStateList(requireContext(),R.color.dark_blue)
                    }
                }else{
                    requireActivity().runOnUiThread {
                        binding.txtCases.text = "No Cases"
                        binding.txtCases1.text = "No Cases"
                        binding.llCases.backgroundTintList = getColorStateList(requireContext(),R.color.light_dark_blue_200)
                        binding.llCases1.backgroundTintList = getColorStateList(requireContext(),R.color.light_dark_blue_200)
                    }
                }
            }
        })
    }

    private fun obtainActualDate() {
        binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
    }

}