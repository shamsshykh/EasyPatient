package com.app.easy_patient.fragment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.app.easy_patient.R
import com.app.easy_patient.activity.ChangePasswordActivity
import com.app.easy_patient.activity.change_profile.ChangeProfileActivity
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.model.StatusModel
import com.app.easy_patient.model.UserDetailModel
import com.app.easy_patient.network.GetDataService
import com.app.easy_patient.network.RetrofitClientInstance
import com.app.easy_patient.util.InstantAutoComplete
import com.app.easy_patient.util.SharedPrefs
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment() {
    var tvChangePassword: TextView? = null
    var spinnerGender: InstantAutoComplete? = null
    var gender = ""
    var imageLink: String? = null
    var profileImage: ImageView? = null
    var mContext: Context? = null
    var sharedPrefs: SharedPrefs? = null
    var etName: EditText? = null
    var etEmail: EditText? = null
    var etCPF: EditText? = null
    var etBirthDate: EditText? = null
    var digits_before = 0
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    var progressDialog: ProgressDialog? = null
    var btnConfirm: Button? = null
    var birthDate: String? = null
    var ILCPF: TextInputLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        mContext = activity
        sharedPrefs = SharedPrefs(mContext)
        progressDialog = ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(getString(R.string.loading_str))
        etName = view.findViewById(R.id.et_name)
        etEmail = view.findViewById(R.id.et_email)
        etEmail?.keyListener = null
        etCPF = view.findViewById(R.id.et_cpf)
        ILCPF = view.findViewById(R.id.il_et_cpf)
        //        setEditTextView(etCPF, ILCPF, "000.000.000-00", "CPF");
        etBirthDate = view.findViewById(R.id.et_birth_date)
        profileImage = view.findViewById(R.id.profile_image)
        spinnerGender = view.findViewById(R.id.spinner_gender)
        tvChangePassword = view.findViewById(R.id.tv_change_password)
        btnConfirm = view.findViewById(R.id.btn_confirm)
        tvChangePassword?.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    mContext,
                    ChangePasswordActivity::class.java
                )
            )
        }
        view.findViewById<View>(R.id.et_birth_date)
            .setOnClickListener { v: View? -> onBirhdateClick() }
        val adapter = ArrayAdapter.createFromResource(
            mContext!!,
            R.array.gender_array,
            android.R.layout.simple_list_item_1
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinnerGender?.setAdapter(adapter)
        spinnerGender?.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
                when (position) {
                    1 -> gender = "m"
                    2 -> gender = "f"
                }
            }
        profileImage?.setOnClickListener {
            val intent = Intent(mContext, ChangeProfileActivity::class.java)
            intent.putExtra("IMAGE_LINK", imageLink)
            startActivity(intent)
        }
        etCPF?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                digits_before = etCPF?.getText().toString().length
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val digits = etCPF?.text.toString().length
                if (digits_before == 2 && digits == 3 || digits_before == 6 && digits == 7) {
                    etCPF?.append(".")
                }
                if (digits_before == 10 && digits == 11) {
                    etCPF?.append("-")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        btnConfirm?.setOnClickListener { v: View? ->
            val name = etName?.getText().toString()
            val email = etEmail?.getText().toString()
            // String cpf = etCPF.getText().toString();
            if (!isEmpty(name, getString(R.string.enter_name_str), etName)) if (!isEmpty(
                    email,
                    getString(R.string.enter_email_str),
                    etEmail
                )
            ) // if (!isEmpty(cpf, getString(R.string.enter_crf_str), etCPF))
                if (!isEmpty(
                        birthDate,
                        getString(R.string.enter_cpf_str),
                        etBirthDate
                    )
                ) if (gender.equals("", ignoreCase = true)) Toast.makeText(
                    mContext,
                    getString(R.string.select_gender_str),
                    Toast.LENGTH_SHORT
                ).show() else putUpdateProfile(name, email, birthDate)
        }
        etEmail?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                etEmail?.clearFocus()
                etBirthDate?.requestFocus()
                onBirhdateClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return view
    }

    private fun putUpdateProfile(name: String, email: String, birthDate: String?) {
        val api = RetrofitClientInstance.getRetrofitInstance(mContext).create(
            GetDataService::class.java
        )
        progressDialog!!.show()
        val loadUserDetailResponse = api.updateUserDetail(name, email, birthDate, gender)
        loadUserDetailResponse.enqueue(object : Callback<StatusModel> {
            override fun onResponse(call: Call<StatusModel>, response: Response<StatusModel>) {
                if (response.code() == 200) {
                    response.body()?.status?.let {
                        if (it) {
                            sharedPrefs!!.gender = gender
                            sharedPrefs!!.name = etName!!.text.toString()
                            Toast.makeText(
                                mContext,
                                getString(R.string.profile_updated_str),
                                Toast.LENGTH_SHORT
                            ).show()
                            activity!!.recreate()
                        }
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.server_error_str),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<StatusModel>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadProfileDetail()
    }

    private fun onBirhdateClick() {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            mContext!!,
            { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                birthDate = String.format("%02d", dayOfMonth) + "/" + String.format(
                    "%02d",
                    monthOfYear + 1
                ) + "/" + year
                etBirthDate!!.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                etBirthDate?.clearFocus()
                spinnerGender?.showDropDown()
                spinnerGender?.requestFocus()
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun loadProfileDetail() {
        val api = RetrofitClientInstance.getRetrofitInstance(mContext).create(
            GetDataService::class.java
        )
        progressDialog!!.show()
        val loadUserDetailResponse = api.userDetail
        loadUserDetailResponse.enqueue(object : Callback<UserDetailModel?> {
            override fun onResponse(
                call: Call<UserDetailModel?>,
                response: Response<UserDetailModel?>
            ) {
                if (response.isSuccessful) {
                    val userDetailModel = response.body()
                    etName!!.setText(userDetailModel!!.name)
                    etEmail!!.setText(userDetailModel.username)
                    if (userDetailModel.cpf != null && userDetailModel.cpf != "") etCPF!!.setText(
                        formatCPF(
                            userDetailModel.cpf
                        )
                    )
                    birthDate = userDetailModel.birth_date
                    if (birthDate != null) etBirthDate!!.setText(convertDate(birthDate!!))
                    gender = userDetailModel.gender.trim { it <= ' ' }
                    if (gender.equals(
                            "m",
                            ignoreCase = true
                        )
                    ) spinnerGender!!.setText(resources.getStringArray(R.array.gender_array)[0]) else if (gender.equals(
                            "f",
                            ignoreCase = true
                        )
                    ) spinnerGender!!.setText(
                        resources.getStringArray(R.array.gender_array)[1]
                    )
                    imageLink = userDetailModel.picture
                    sharedPrefs!!.imagePath = imageLink
                    if (imageLink != null && imageLink !== "") {
                        Glide.with(mContext!!)
                            .load(imageLink)
                            .placeholder(resources.getDrawable(R.drawable.place_holder))
                            .into(profileImage!!)
                    } else {
                        sharedPrefs!!.imagePath = null
                        if (sharedPrefs!!.gender == "m") profileImage!!.setImageResource(R.drawable.ic_profile_pic_male) else profileImage!!.setImageResource(
                            R.drawable.profile_pic_female
                        )
                    }
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<UserDetailModel?>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    private fun convertDate(birthDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val month: String
        val dd: String
        val year: String
        var date: Date? = Date()
        try {
            date = inputFormat.parse(birthDate)
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

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun formatCPF(cpf: String): String {
        val sb = StringBuilder(cpf)
        try {
            sb.insert(3, '.')
            sb.insert(7, '.')
            sb.insert(11, '-')
        } catch (e: ArrayIndexOutOfBoundsException) {
            return cpf
        }
        return sb.toString()
    }

    private fun isEmpty(value: String?, message: String, field: EditText?): Boolean {
        return if (TextUtils.isEmpty(value)) {
            field!!.error = message
            if (!field.requestFocus()) field.requestFocus()
            true
        } else false
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
}