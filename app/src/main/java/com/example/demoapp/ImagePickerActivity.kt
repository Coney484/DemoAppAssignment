package com.example.demoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.demoapp.databinding.ActivityImagePickerBinding
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

class ImagePickerActivity : AppCompatActivity(), UploadRequestBody.UploadCallBack {
    private lateinit var binding: ActivityImagePickerBinding
    private var selectedImageFromCamera: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var isClickedFromImagePickerScreen: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker)
        binding.bottomNavigation.background = null
        binding.fab.setOnClickListener {
            isClickedFromImagePickerScreen = true
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityResultLauncher.launch(intent)
        }
        val bundle = intent.extras
        if (bundle != null && !isClickedFromImagePickerScreen) {
            selectedImageFromCamera = bundle.getParcelable("Uri_Image")
            binding.imageView.setImageURI(selectedImageFromCamera)
        }
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    var bitmapIm = result!!.data!!.extras!!.get("data") as Bitmap
                    var res = WeakReference<Bitmap>(
                        Bitmap.createScaledBitmap(
                            bitmapIm,
                            bitmapIm.height,
                            bitmapIm.width,
                            false
                        )
                            .copy(
                                Bitmap.Config.RGB_565, true
                            )
                    )

                    var bitImage: Bitmap? = res.get()

                    selectedImageFromCamera = saveImg(bitImage, this)
                    val intent_Two = Intent(this, ImagePickerActivity::class.java)
                    intent_Two.putExtra("Uri_Image", selectedImageFromCamera)
                    startActivity(intent_Two)
                    binding.imageView.setImageURI(selectedImageFromCamera)

                    binding.textView.text = "Selected Image"


                } else {
                    Toast.makeText(
                        applicationContext,
                        "Image Not Clicked",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        binding.buttonUpload.setOnClickListener {
            uploadImage()
        }


    }

    private fun uploadImage() {
        if (selectedImageFromCamera == null) {
            binding.linearLayout.snackBar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageFromCamera!!, "r", null) ?: return
        val file = File(cacheDir, contentResolver.getFileName(selectedImageFromCamera!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyApi().uploadImage(
            MultipartBody.Part.createFormData("image", file.name, body),
            RequestBody.create(MediaType.parse("multipart/form-data"), "media")
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                binding.progressBar.progress = 100
                binding.linearLayout.snackBar(response.body()?.message.toString())
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.linearLayout.snackBar(t.message!!)
            }

        })
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