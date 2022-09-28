package com.example.demoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallBack {
    private lateinit var binding: ActivityMainBinding
    private var selectedImage: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var listData: ArrayList<DataImages>
    lateinit var imageId: Array<Int>
    lateinit var caption: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNavigation.background = null

        imageId = arrayOf(
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash,
            R.drawable.andreas_kruck_l8ypmiu1hio_unsplash
        )


        caption = arrayOf(
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images",
            "These are times square Images"

        )

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        listData = arrayListOf<DataImages>()
        getUserData()
        binding.fab.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityResultLauncher.launch(intent)
        }
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    var bitmapIm = result!!.data!!.extras!!.get("data") as Bitmap
                    var res = WeakReference<Bitmap>(
                        Bitmap.createScaledBitmap(bitmapIm, bitmapIm.height, bitmapIm.width, false)
                            .copy(
                                Bitmap.Config.RGB_565, true
                            )
                    )

                    var bitImage: Bitmap? = res.get()

                    selectedImage = saveImg(bitImage, this)
                    val intent_Two = Intent(this, ImagePickerActivity::class.java)
                    intent_Two.putExtra("Uri_Image", selectedImage)
                    startActivity(intent_Two)
                    binding.imageView.setImageURI(selectedImage)

                    binding.textView.text = "Selected Image"


                } else {
                    Toast.makeText(applicationContext, "Image Not Clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun getUserData() {
        for (i in imageId.indices) {
            val dataImages = DataImages(imageId[i], caption[i])
            listData.add(dataImages)
        }
        recyclerView.adapter = MyAdapter(listData)
    }

    private fun saveImg(bitImage: Bitmap?, context: Context): Uri? {
        val imgFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imgFolder.mkdirs()
            val fi = File(imgFolder, "captured_image.jpg")
            val opStream = FileOutputStream(fi)
            bitImage?.compress(Bitmap.CompressFormat.JPEG, 100, opStream)
            opStream.flush()
            opStream.close()
            uri = FileProvider.getUriForFile(
                context.applicationContext,
                "com.example.demoapp" + ".provider",
                fi
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return uri

    }

    override fun onProgressUpdate(percentage: Int) {
        binding.progressBar.progress = percentage
    }

}