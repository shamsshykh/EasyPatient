package com.app.easy_patient.activity.change_profile

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.R
import com.app.easy_patient.activity.BaseActivity
import com.app.easy_patient.databinding.ActivityChangeProfileBinding
import com.app.easy_patient.ktx.setLocalImage
import com.app.easy_patient.wrappers.Resource
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.util.FileUtil
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChangeProfileActivity : BaseActivity() {

    private lateinit var bi: ActivityChangeProfileBinding
    private lateinit var viewModel: ChangeProfileViewModel

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider).get(ChangeProfileViewModel::class.java)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_change_profile)
        bi.executePendingBindings()

        setupViews()
        initObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews() {
        setSupportActionBar(bi.toolbar)
        title = getString(R.string.photo_str)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        updateProfilePic()
    }

    private fun initObservers() {
        viewModel.uploadProfileResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Error -> {
                    closeProgressDialog()
                    showToast(this, resource.exception.localizedMessage)
                }
                is Resource.Loading -> showProgressDialog()

                is Resource.Success -> {
                    closeProgressDialog()
                    val uploadData = resource.data
                    if (uploadData.upload) viewModel.updateImagePath(uploadData.location)
                    finish()
                }

                is Resource.Empty -> closeProgressDialog()
            }
        }

        viewModel.deleteProfileResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Error -> {
                    closeProgressDialog()
                    showToast(this, resource.exception.localizedMessage)
                }
                is Resource.Loading -> showProgressDialog()

                is Resource.Success -> {
                    closeProgressDialog()
                    val status = resource.data
                    if (status.status) {
                        mUri = null
                        viewModel.updateImagePath(null)
                        updateProfilePic()
                    }
                }

                is Resource.Empty -> closeProgressDialog()
            }
        }
    }

    fun onLibraryClick(view: View) {
        galleryLauncher.launch(
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .cropFreeStyle()
                .galleryMimeTypes( // no gif images at all
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent()
        )
    }

    fun onCameraClick(view: View) {
        cameraLauncher.launch(
            ImagePicker.with(this)
                .crop(1f, 1f) 			//Crop image(Optional), Check Customization for more option
                .cropOval()	    		//Allow dimmed layer to have a circle inside
                .cropFreeStyle()	    //Let the user to resize crop bounds
                .cameraOnly()
                .maxResultSize(1080, 1920, true)
                .createIntent()
        )
    }

    fun onDeletePicClick(view: View) {
        viewModel.deleteProfilePhoto()
    }

    fun onConfirmBtnClick(view: View) {
        when (mUri) {
            null ->  finish()
            else -> {
                val file = FileUtil.getTempFile(this, mUri!!)
                file?.let {
                    viewModel.uploadProfilePhoto(it)
                } ?: showToast(this, getString(R.string.picture_error_str))
            }
        }
    }

    private fun updateProfilePic() {
        if (viewModel.getGender() == "m") {
            Glide.with(this)
                .load(viewModel.userImage())
                .apply(imagePlaceHolder(R.drawable.ic_profile_pic_male))
                .into(bi.profileImage)
        } else {
            Glide.with(this)
                .load(viewModel.userImage())
                .apply(imagePlaceHolder(R.drawable.profile_pic_female))
                .into(bi.profileImage)
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                mUri = uri
                bi.profileImage.setLocalImage(uri, false)
            } else parseError(it)
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                mUri = uri
                bi.profileImage.setLocalImage(uri, false)
            } else parseError(it)
        }

    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            showToast(this, ImagePicker.getError(activityResult.data))
        } else {
            showToast(this, getString(R.string.task_cancelled_str))
        }
    }
}