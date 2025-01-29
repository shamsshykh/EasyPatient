package com.app.easy_patient.activity

import android.Manifest
import com.app.easy_patient.util.LangContextWrapper.Companion.wrap
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.os.Bundle
import com.app.easy_patient.R
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.annotation.ColorRes
import com.app.easy_patient.util.LangContextWrapper
import android.widget.EditText
import android.graphics.drawable.Drawable
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Environment
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.Paragraph
import com.itextpdf.text.DocumentException
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import android.view.View.OnFocusChangeListener
import android.content.Intent
import com.app.easy_patient.util.AppConstants
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import com.app.easy_patient.BuildConfig
import com.downloader.PRDownloader
import com.downloader.OnDownloadListener
import com.bumptech.glide.request.RequestOptions
import com.downloader.Error
import com.itextpdf.text.Document
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

open class BaseActivity : AppCompatActivity() {
    @JvmField
    var progressDialog: ProgressDialog? = null
    var errorColor = 0
    var spannableStringBuilder: SpannableStringBuilder? = null
    var MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 300
    @JvmField
    protected var mImageCaptureUri: Uri? = null
    var foregroundColorSpan: ForegroundColorSpan? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        val version = Build.VERSION.SDK_INT
        //Get the defined errorColor from color resource.
        errorColor = if (version >= 23) {
            ContextCompat.getColor(applicationContext, R.color.errorColor)
        } else {
            resources.getColor(R.color.errorColor)
        }
        val errorString = "This field cannot be empty"
        foregroundColorSpan = ForegroundColorSpan(errorColor)
    }

    fun statusBar(@ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = getColor(color)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(wrap(newBase, "pt-rBR"))
    }

    fun mapDayToIntegerString(day: String?): String {
        var returnInt = "0"
        when (day) {
            "Monday" -> returnInt = "1"
            "Tuesday" -> returnInt = "2"
            "Wednesday" -> returnInt = "3"
            "Thursday" -> returnInt = "4"
            "Friday" -> returnInt = "5"
            "Saturday" -> returnInt = "6"
            "Sunday" -> returnInt = "7"
        }
        return returnInt
    }

    fun getSelectedDayString(dayOfWeek: String): StringBuilder {
        val sb = StringBuilder()
        val daysArray = dayOfWeek.split(",").toTypedArray()
        Arrays.sort(daysArray)
        for (i in daysArray.indices) {
            if (i == daysArray.size - 1) sb.append(localiseDay(mapToDayString(daysArray[i]))) else sb.append(
                localiseDay(
                    mapToDayString(
                        daysArray[i]
                    )
                ) + ", "
            )
        }
        return sb
    }

    fun mapToDayString(day: String): String? {
        var returnDay: String? = null
        when (day.trim { it <= ' ' }) {
            "1" -> returnDay = "Monday"
            "2" -> returnDay = "Tuesday"
            "3" -> returnDay = "Wednesday"
            "4" -> returnDay = "Thursday"
            "5" -> returnDay = "Friday"
            "6" -> returnDay = "Saturday"
            "7" -> returnDay = "Sunday"
        }
        return returnDay
    }

    private fun localiseDay(day: String?): String? {
        var returnDay: String? = null
        when (day!!.trim { it <= ' ' }) {
            "Monday" -> returnDay = getString(R.string.monday_str)
            "Tuesday" -> returnDay = getString(R.string.tuesday_str)
            "Wednesday" -> returnDay = getString(R.string.wednesday_str)
            "Thursday" -> returnDay = getString(R.string.thursday_str)
            "Friday" -> returnDay = getString(R.string.friday_str)
            "Saturday" -> returnDay = getString(R.string.saturday_str)
            "Sunday" -> returnDay = getString(R.string.sunday_str)
        }
        return returnDay
    }

    fun closeProgressDialog() {
        if (progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    fun showProgressDialog() {
        progressDialog!!.show()
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    protected fun showError(et: EditText, errorString: String) {
        spannableStringBuilder = SpannableStringBuilder(errorString)
        spannableStringBuilder!!.setSpan(foregroundColorSpan, 0, errorString.length, 0)
        val drawable = resources.getDrawable(R.drawable.img_error)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        et.setError(spannableStringBuilder, drawable)
        et.requestFocus()
    }

    fun createAndSavePdf(mContext: Context?, text: String?, name: String) {
        val fileName = name + "-" + System.currentTimeMillis()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                )
            }
        }
        val doc = Document()
        try {
            var directory: File? = null
            var file: File? = null
            var dir: String? = ""
            val folderName = "EasyPatient"
            val sdcard = Environment.getExternalStorageState()
            dir = if (sdcard == Environment.MEDIA_MOUNTED) {
                Environment.getExternalStorageDirectory().absolutePath
            } else {
                Environment.getRootDirectory().absolutePath
            }
            directory = File(dir, folderName)
            if (!directory.exists()) {
                directory.mkdir()
            }
            file = File(directory, "$fileName.pdf")
            val fOut = FileOutputStream(file)
            PdfWriter.getInstance(doc, fOut)

            //open the document
            doc.open()
            val p1 = Paragraph(text)
            p1.alignment = Paragraph.ALIGN_CENTER

            //add paragraph to document
            doc.add(p1)
        } catch (de: DocumentException) {
            Log.e("PDFCreator", "DocumentException:$de")
        } catch (e: IOException) {
            Log.e("PDFCreator", "ioException:$e")
        } finally {
            doc.close()
        }

//        showAlert(mContext,"A pdf file is saved in as ..../EasyPatient/"+fileName+".pdf");
        showToast(mContext, getString(R.string.pdf_save_str, fileName))
    }

    fun showToast(mContext: Context?, message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    fun setEditTextView(
        editText: EditText, textInputLayout: TextInputLayout,
        hint: String?, desc: String?
    ) {
        editText.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (hasFocus) {
                Handler().postDelayed({ textInputLayout.hint = desc }, 100)
            } else {
                textInputLayout.hint = hint
            }
        }
    }

    protected fun convertDate(dateString: String?): String {
        if (dateString == null) return ""
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val month: String
        val dd: String
        val year: String
        var hour: String
        var min: String
        var date: Date? = Date()
        try {
            date = inputFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal = Calendar.getInstance()
        cal.time = date
        month = checkDigit(cal[Calendar.MONTH] + 1)
        dd = checkDigit(cal[Calendar.DATE])
        year = checkDigit(cal[Calendar.YEAR])
        return "$dd/$month/$year"
    }

    open fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    protected fun startCamera(requestCode: Int) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var pickerIntent: Intent? = null
            var file: File? = null
            val BASE_DIR = externalCacheDir!!.path
            if (requestCode == AppConstants.REQUEST_CODE_KEYS.REQUEST_CODE_TAKE_PICTURE) {
                pickerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                file = File(BASE_DIR, "img_" + System.currentTimeMillis().toString() + ".jpg")
            }
            //            mImageCaptureUri = FileProvider.getUriForFile(this, getPackageName(), file);
            mImageCaptureUri =
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file!!)
            pickerIntent!!.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                pickerIntent.clipData = ClipData.newRawUri("", mImageCaptureUri)
                pickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(pickerIntent, requestCode)
        } else {
            Log.v(javaClass.simpleName, "Media not mounted.")
        }
    }

    fun savingImage(finalBitmap: Bitmap): Uri {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/easy_patient/saved_images")
        if (!myDir.isDirectory) {
            myDir.mkdirs()
        }
        val fname = "Image-" + System.currentTimeMillis() + ".jpg"
        val file = File(myDir, fname)
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Uri.fromFile(file)
    }

    fun getFilePath(fileName: String?): String {
//        String fileName = "name" + "-" + System.currentTimeMillis();
        var directory: File? = null
        var file: File? = null
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                )
            }
        }
        val doc = Document()
        var dir: String? = ""
        val folderName = "EasyPatient"
        val sdcard = Environment.getExternalStorageState()
        dir = if (sdcard == Environment.MEDIA_MOUNTED) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
        } else {
            Environment.getRootDirectory().absolutePath
        }
        directory = File(dir, folderName)
        if (!directory.exists()) {
            directory.mkdir()
        }
        file = File(directory, fileName)
        return file.absolutePath
    }

    fun savePDF(url: String?, dirPath: String?, fileName: String, mContext: Context?) {
        PRDownloader.download(
            url,
            dirPath,
            "$fileName.pdf"
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    val downloadedFile = File(dirPath, "$fileName.pdf")
                    showToast(mContext, getString(R.string.pdf_save_str, fileName))
                }

                override fun onError(error: Error) {
                    showToast(mContext, "error")
                }
            })
    }

    fun imagePlaceHolder(drawable: Int): RequestOptions {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(drawable)
        requestOptions.error(drawable)
        return requestOptions
    }
}