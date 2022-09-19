package com.example.wsamad6.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wsamad6.R
import com.example.wsamad6.core.Constants
import com.example.wsamad6.data.*
import com.example.wsamad6.data.models.Symptom
import com.example.wsamad6.databinding.DialogLoadingBinding
import com.example.wsamad6.databinding.DialogPhotoBinding
import com.example.wsamad6.databinding.FragmentCheckListBinding
import com.example.wsamad6.databinding.FragmentMapBinding
import com.example.wsamad6.ui.adapter.CheckListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class CheckListFragment : Fragment(R.layout.fragment_check_list) {
    private lateinit var binding: FragmentCheckListBinding
    private val args by navArgs<CheckListFragmentArgs>()
    private val symptomList = mutableListOf<Symptom>()
    private lateinit var alertDialog: AlertDialog
    private var uriResult: Uri? = null
    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.extras?.get("data") as Bitmap

            // Get the context wrapper
            val wrapper = ContextWrapper(requireContext())

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "${UUID.randomUUID()}.jpg")

            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            data.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            // Return the saved bitmap uri
            uriResult = Uri.parse(file.absolutePath)
            binding.imgClose.visibility = View.VISIBLE
            binding.imgAdd.setImageURI(uriResult)

        }
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data
            data?.let {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor =
                    requireContext().contentResolver.query(data, projection, null, null, null)
                val column = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
                if (cursor.moveToNext()) uriResult = Uri.parse(cursor.getString(column))

                binding.imgClose.visibility = View.VISIBLE
                binding.imgAdd.setImageURI(uriResult)
            }
        }
    private val registerPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                permissions()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckListBinding.bind(view)

        setDate()
        obtainList()
        clicks()

    }

    private fun clicks() {
        binding.imgAdd.setOnClickListener { permissions() }
        binding.imgClose.setOnClickListener {
            uriResult = null
            binding.imgClose.visibility = View.GONE
            binding.imgAdd.setImageResource(R.drawable.add)
        }
        binding.btnConfirm.setOnClickListener { sendDialog() }
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

    }

    private fun sendDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("You Really want to send that information?")
            .setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
            .setPositiveButton("Yes") { d, _ ->
                validate()
                d.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun validate() {
        if ((binding.rvCheckList.adapter as CheckListAdapter).buttonsChecked().isNullOrEmpty()) {
            Snackbar.make(
                binding.root,
                "You must checked at list one option",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        if (uriResult == null) {
            Snackbar.make(binding.root, "You must select a photo", Snackbar.LENGTH_SHORT).show()
            return
        }

        dialogLoading()
        sendList()
        sendPhoto()
    }

    private fun dialogLoading() {
        val dialog = DialogLoadingBinding.inflate(LayoutInflater.from(requireContext()))
        alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun sendPhoto() {
        Constants.OKHTTP.newCall(
            post2(
                "https://cloudlabs-image-object-detection.p.rapidapi.com/objectDetection/byImageFile",
                photo(uriResult!!)
            )
        ).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                Log.e("onResponse: ", json.toString())
                if (json.getString("status") == "success") {
                    Snackbar.make(
                        binding.root,
                        "The Photo was successfully sent",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requireActivity().runOnUiThread {
                        alertDialog.dismiss()
                        findNavController().popBackStack()
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "The Photo was unsuccessfully sent",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    alertDialog.dismiss()
                }
            }
        })
    }

    private fun sendList() {
        Constants.OKHTTP.newCall(
            post(
                "day_symptoms",
                symptomList((binding.rvCheckList.adapter as CheckListAdapter).buttonsChecked())
            )
        ).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")) {
                    Snackbar.make(
                        binding.root,
                        "The Symptom list was successfully sent",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "The Symptom list was unsuccessfully sent",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun permissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                bottomDialog()
            }
            else -> {
                registerPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun bottomDialog() {
        val dialog = DialogPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).apply {
            setContentView(dialog.root)
        }
        dialog.llCamera.setOnClickListener {
            pickFromCamera()
            alert.dismiss()
        }
        dialog.llGallery.setOnClickListener {
            pickFromGallery()
            alert.dismiss()
        }
        alert.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alert.show()
    }

    private fun pickFromCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(i)
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        galleryResult.launch(i)

    }

    private fun obtainList() {
        Constants.OKHTTP.newCall(get("symptom_list")).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    symptomList.add(
                        Symptom(
                            item.getInt("id"),
                            item.getString("title"),
                            item.getInt("priority")
                        )
                    )
                    requireActivity().runOnUiThread {
                        binding.rvCheckList.adapter = CheckListAdapter(symptomList)
                        binding.rvCheckList.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            }
        })
    }

    private fun setDate() {
        binding.txtActualDate.text = args.date
    }

}