package com.app.easy_patient.activity.dashboard

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.database.MealPlanModel
import com.app.easy_patient.database.OrientationListModel
import com.app.easy_patient.database.PrescriptionListModel
import com.app.easy_patient.ktx.critical
import com.app.easy_patient.ktx.notification
import com.app.easy_patient.ktx.stringToDate
import com.app.easy_patient.ktx.toMedicineReminder
import com.app.easy_patient.model.kotlin.*
import com.app.easy_patient.ui.home.*
import com.app.easy_patient.util.Utility
import com.app.easy_patient.wrappers.Resource
import com.app.easy_patient.wrappers.data
import com.app.easy_patient.wrappers.succeeded
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.IDN
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val repository: EasyPatientRepo): ViewModel() {

    var backPressedOnce = false

    private val _appointmentList = MutableLiveData<Resource<List<Appointment>>>()
    val appointmentList: LiveData<Resource<List<Appointment>>> = _appointmentList

    private val _filteredAppointmentList = MutableLiveData<Resource<List<Appointment>>>()
    val filteredAppointmentList: LiveData<Resource<List<Appointment>>> = _filteredAppointmentList

    private val _filtered5DaysAppointmentList = MutableLiveData<Resource<List<Appointment>>>()
    val filtered5DaysAppointmentList: LiveData<Resource<List<Appointment>>> = _filtered5DaysAppointmentList

    private val _medicineList = MutableLiveData<Resource<List<Medicine>>>()
    val medicineList: LiveData<Resource<List<Medicine>>> = _medicineList

    private val _notificationList = MutableLiveData<Resource<List<Notification>>>()
    val notificationList: LiveData<Resource<List<Notification>>> = _notificationList

    private val _medicineReminderList = MutableLiveData<Resource<MutableList<MedicineReminder>>>()
    val medicineReminderList: LiveData<Resource<MutableList<MedicineReminder>>> = _medicineReminderList

    private val _mealPlanList = MutableLiveData<Resource<List<MealPlanModel>>>()
    val mealPlanList: LiveData<Resource<List<MealPlanModel>>> = _mealPlanList

    private val _prescriptionList = MutableLiveData<Resource<List<PrescriptionListModel>>>()
    val prescriptionList: LiveData<Resource<List<PrescriptionListModel>>> = _prescriptionList

    private val _orientationList = MutableLiveData<Resource<List<OrientationListModel>>>()
    val orientationList: LiveData<Resource<List<OrientationListModel>>> = _orientationList

    private var _audioList = MutableLiveData<Resource<List<Audio>>>()
    val audioList: LiveData<Resource<List<Audio>>> = _audioList

    private val _archiveMealPlanList = MutableLiveData<Resource<List<MealPlanModel>>>()
    val archiveMealPlanList: LiveData<Resource<List<MealPlanModel>>> = _archiveMealPlanList

    private val _archivePrescriptionList = MutableLiveData<Resource<List<PrescriptionListModel>>>()
    val archivePrescriptionList: LiveData<Resource<List<PrescriptionListModel>>> = _archivePrescriptionList

    private val _archiveOrientationList = MutableLiveData<Resource<List<OrientationListModel>>>()
    val archiveOrientationList: LiveData<Resource<List<OrientationListModel>>> = _archiveOrientationList

    private var _archiveAudioList = MutableLiveData<Resource<List<Audio>>>()
    val archiveAudioList: LiveData<Resource<List<Audio>>> = _archiveAudioList

    var _menuList = MutableLiveData<Resource<MutableList<Menu>>>()
    val menuList: LiveData<Resource<MutableList<Menu>>> = _menuList

    private val _medicineNotification = MutableLiveData<List<MedicineReminder>>()
    val medicineNotification: LiveData<List<MedicineReminder>>
        get() = _medicineNotification

    var medicineReminder: MedicineReminder? = null

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean>
        get() = _progress

    private val _updateCount = MutableLiveData<Boolean>()
    val updateCount: LiveData<Boolean>
        get() = _updateCount

    val isLoading = ObservableBoolean()
    val isEnable = ObservableBoolean()

    private val _uiStates = MutableLiveData<HomeState>()
    val uiStates: LiveData<HomeState>
        get() = _uiStates

    private val _appointmentNotification = MutableLiveData<Appointment>()
    val appointmentNotification: LiveData<Appointment>
        get() = _appointmentNotification

    init {
        //  updateStaticData()
        _medicineList.value = Resource.Loading
        _medicineReminderList.value = Resource.Loading
        _mealPlanList.value = Resource.Loading
        _prescriptionList.value = Resource.Loading
        _orientationList.value = Resource.Loading
        _audioList.value = Resource.Loading
        loadAppointments()
        loadMedicines()
        loadRecommendations()
        loadMealPlans()
        loadPrescriptions()
        loadOrientations()
        loadMenu()
        swipeEnable(true)
//        loadArchiveMealPlans()
//        loadArchiveOrientations()
//        loadArchivePrescriptions()
    }


    fun swipeEnable(isTrue : Boolean){
        isEnable.set(isTrue)
    }



    fun onRefresh(){
        loadAppointments()
        loadMedicines()
        loadRecommendations()
        loadMealPlans()
        loadPrescriptions()
        loadOrientations()
        loadMenu()
        isLoading.set(true)
    }

    private fun updateStaticData() {
        val suggestedMedicine1 = SuggestedMedicine(id = 1, name = "Omeprazol", date = "Próxima dose: Amanhã às 07:00h", daysOfWeek = "Todos os dias", dosage = "1 pílula")
        val suggestedMedicine2 = SuggestedMedicine(id = 2, name = "Omeprazol", date = "Próxima dose: Amanhã às 07:00h", daysOfWeek = "Todos os dias", dosage = "1 pílula")
        val notification = listOf(Notification(id = 1, title = "Dr. Rogério te enviou um pacote de lembretes.", name = "Pacote de Cuidados", desc = "Ative os lmebretes de medicamentos ou suolementos que Dr. Rogerio enviou para voce.", suggestedMedicines = listOf(suggestedMedicine1, suggestedMedicine2)))
        _notificationList.value = Resource.Success(notification)
    }

    fun loadAppointments() {
        viewModelScope.launch {
            repository.getAppointments().collect {
                _appointmentList.value = it // all appointment only
                if (it.succeeded()) {
                    _progress.value = false
                    it.data!!.forEach { appointment ->
                        _uiStates.value = CreateAppointmentNotification(appointment)
                    }
                    val appointments = it.data!!.filter { appointment ->
                        val calendar = Calendar.getInstance()
                      //  appointment.date = "2022-05-24 00:00:00" // 22
                        calendar.time = appointment.date!!.stringToDate()
                        calendar.timeInMillis > Calendar.getInstance().timeInMillis
                    }
                    // for filter or recent appointment only
                    val updateFilteredList = appointments.filter { appointment ->
                        val currentDateCalendar = Calendar.getInstance()
                        currentDateCalendar.add(Calendar.DAY_OF_YEAR, 5)

                        val appointmentDateCalendar = Calendar.getInstance()
                        appointmentDateCalendar.time = appointment.date!!.stringToDate()

                        appointmentDateCalendar.timeInMillis > currentDateCalendar.timeInMillis
                    }.sortedWith( compareBy { sortApp ->
                        val calendar = Calendar.getInstance()
                        calendar.time = sortApp.date!!.stringToDate()
                        calendar.timeInMillis
                    })
                    _filteredAppointmentList.value = if (updateFilteredList.isNullOrEmpty()) Resource.Empty else Resource.Success(updateFilteredList)

                    getFiveDaysAppointmentList()
                } else {
                    _filteredAppointmentList.value = it
                }
            }
        }
    }

    private fun getFiveDaysAppointmentList(){
        val calendar5Days = Calendar.getInstance()
        calendar5Days.add(Calendar.DATE,5) // 28

        val appointments = _appointmentList.value?.data!!.filter { appointment ->
            val calendar = Calendar.getInstance()
            calendar.time = appointment.date!!.stringToDate()
            calendar5Days.timeInMillis >= calendar.timeInMillis &&
            calendar.timeInMillis > Calendar.getInstance().timeInMillis
        }.sortedWith( compareBy {
            val calendar = Calendar.getInstance()
            calendar.time = it.date!!.stringToDate()
            calendar.timeInMillis
        })
        // five or less than 5 days appointment
        _filtered5DaysAppointmentList.value = if (appointments.isEmpty()) Resource.Empty else Resource.Success(appointments)
    }

    fun loadMedicines() {
        viewModelScope.launch {
            repository.getMedicine().collect {
                _medicineList.value = it
                if (it is Resource.Empty)
                    _medicineReminderList.value = Resource.Empty
            }
        }
    }

    fun loadRecommendations(loader: Boolean = false) {
        viewModelScope.launch {
            repository.getAudios().collect {
                _progress.value = it is Resource.Loading && loader
                if (it.succeeded()) {
                    _menuList.value!!.data!!.find { item -> item.id == 4}!!.archiveCount = it.data!!.size
                    _menuList.value = _menuList.value
                } else if (it is Resource.Empty) {
                    _menuList.value!!.data!!.find { item -> item.id == 4}!!.archiveCount = 0
                    _menuList.value = _menuList.value
                }
                _audioList.value = it
            }
        }
    }

    fun loadOrientations() {
        viewModelScope.launch {
            repository.getOrientation().collect { resources ->
                if (resources is Resource.Success) {
                    _menuList.value!!.data!!.find { item -> item.id == 3}!!.archiveCount = resources.data!!.size
                    _menuList.value = _menuList.value
                } else if (resources is Resource.Empty) {
                    _menuList.value!!.data!!.find { item -> item.id == 3}!!.archiveCount = 0
                    _menuList.value = _menuList.value
                }
                _orientationList.value = resources
            }
        }
    }

    fun loadPrescriptions() {
        viewModelScope.launch {
            repository.getPrescription().collect {
                if (it is Resource.Success) {
                    _menuList.value!!.data!!.find { item -> item.id == 2}!!.archiveCount = it.data!!.size
                    _menuList.value = _menuList.value
                } else if (it is Resource.Empty) {
                    _menuList.value!!.data!!.find { item -> item.id == 2}!!.archiveCount = 0
                    _menuList.value = _menuList.value
                }
                _prescriptionList.value = it
            }
        }
    }

    fun loadMealPlans() {
        viewModelScope.launch {
            repository.getMealPlan().collect {
                _progress.value = it is Resource.Loading
                if (it is Resource.Success) {
                    _menuList.value!!.data!!.find { it -> it.id == 1}!!.archiveCount = it.data!!.size
                    _menuList.value = _menuList.value
                } else if (it is Resource.Empty) {
                    _menuList.value!!.data!!.find { item -> item.id == 1}!!.archiveCount = 0
                    _menuList.value = _menuList.value
                }
                _mealPlanList.value = it
                _progress.value = false
            }
        }
    }

    private fun loadArchiveOrientations() {
        viewModelScope.launch {
            repository.getArchiveOrientation().collect {
                _archiveOrientationList.value = it
            }
        }
    }

    private fun loadArchivePrescriptions() {
        viewModelScope.launch {
            repository.getArchivePrescription().collect {
                _archivePrescriptionList.value = it
            }
        }
    }

    private fun loadArchiveMealPlans() {
        viewModelScope.launch {
            repository.getArchiveMealPlan().collect {
                _archiveMealPlanList.value = it
            }
        }
    }

    fun getAllMedicineReminders() {
        if (_medicineList.value is Resource.Success) {
            viewModelScope.launch {
                try {
                    var medicineReminderList = mutableListOf<MedicineReminder>()
                    _medicineList.value!!.data!!.forEach { medicine ->
                        val durationType = medicine.days_of_the_week!!.split(",").toTypedArray()[1].toInt()
                        val frequencyType = medicine.days_of_the_week!!.split(",").toTypedArray()[0].toInt()

                        if (frequencyType > 2 || durationType > 2) {
                            return@forEach
                        }

                        val currentDateCalendar = Calendar.getInstance()

                        val startTime = Calendar.getInstance()
                        startTime.set(Calendar.HOUR_OF_DAY, 0)
                        startTime.set(Calendar.MINUTE, 0)
                        startTime.set(Calendar.SECOND, 0)

                        val endTime = Calendar.getInstance()
                        endTime.set(Calendar.HOUR_OF_DAY, 23)
                        endTime.set(Calendar.MINUTE, 59)
                        endTime.set(Calendar.SECOND, 59)

                        val startDateCalendar = Calendar.getInstance()
                        startDateCalendar.time = medicine.start_time!!.stringToDate()

                        val dayFirstReminder = Calendar.getInstance()
                        dayFirstReminder.time = medicine.start_time!!.stringToDate()
                        if (frequencyType == 0) {
                            do {
                                dayFirstReminder.add(Calendar.HOUR_OF_DAY, medicine.frequency!!)
                            } while (startDateCalendar.get(Calendar.DAY_OF_YEAR) == dayFirstReminder.get(Calendar.DAY_OF_YEAR))
                        }
                        currentDateCalendar.set(Calendar.HOUR_OF_DAY, dayFirstReminder.get(Calendar.HOUR_OF_DAY))
                        currentDateCalendar.set(Calendar.MINUTE, dayFirstReminder.get(Calendar.MINUTE))
                        currentDateCalendar.set(Calendar.SECOND, 0)
                        currentDateCalendar.set(Calendar.MILLISECOND, 0)

                        val endDateCalendar = Calendar.getInstance()
                        endDateCalendar.time = medicine.start_time!!.stringToDate()

                        when (durationType) {
                            0 -> endDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.number_of_days!!)
                            1 -> endDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.number_of_days!!)
                            2 -> endDateCalendar.add(Calendar.MONTH, medicine.number_of_days!!)
                        }
                        do {
                            if (currentDateCalendar.timeInMillis >= startDateCalendar.timeInMillis &&
                                currentDateCalendar.timeInMillis <= endDateCalendar.timeInMillis) {
                                Log.i("Medicine_Reminder", DashboardActivity.dateFormat.format(currentDateCalendar.time))
                                val localReminder = repository.getMedicineReminderById(medicine.medicineReminderId(currentDateCalendar.timeInMillis))
                                if (localReminder != null) {
                                    if (localReminder.reminderTime() < endTime.timeInMillis) {
                                        if (localReminder.status != MedicineStatus.Taken) {
                                            localReminder.status = if (localReminder.reminderTime() > Calendar.getInstance().timeInMillis) MedicineStatus.Future else MedicineStatus.Missed
                                        }
                                    }
                                    medicineReminderList.add(localReminder)
                                } else {
                                    medicineReminderList.add(medicine.toMedicineReminder(
                                        status = if (currentDateCalendar.timeInMillis > Calendar.getInstance().timeInMillis) MedicineStatus.Future else MedicineStatus.Missed,
                                        timeInMillis = currentDateCalendar.timeInMillis)
                                    )
                                }
                            }
                            when (frequencyType) {
                                0 -> currentDateCalendar.add(Calendar.HOUR_OF_DAY, medicine.frequency!!)
                                1 -> currentDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.frequency!!)
                                2 -> currentDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.frequency!!)
                            }
                        } while (
                            currentDateCalendar.timeInMillis >= startTime.timeInMillis &&
                            currentDateCalendar.timeInMillis <= endTime.timeInMillis
                        )
                    }

                    medicineReminderList = medicineReminderList.sortedWith(
                        compareBy { it.reminderTime() }
                    ).toMutableList()

                    medicineReminderList.firstOrNull { it.status == MedicineStatus.Future }?.apply {
                        status = MedicineStatus.UpComing
                    }
                    _medicineReminderList.value = if (medicineReminderList.isNotEmpty()) Resource.Success(medicineReminderList) else Resource.Empty
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    _medicineReminderList.value = Resource.Empty
                }
            }
        } else {
            _medicineReminderList.value = Resource.Empty
        }
    }

    fun getMedicineById(medicineId: Int): Medicine? {
        return repository.getMedicineById(medicineId)
    }

    fun updateMedicineReminderStatus(status: MedicineStatus, medicineReminder: MedicineReminder) {
        viewModelScope.launch {
            repository.updateMedicineReminderStatus(status, medicineReminder)
            val reminderList = _medicineReminderList.value!!.data
            reminderList!!.first { it.id == medicineReminder.id }.apply { this.status = MedicineStatus.Taken }
            _medicineReminderList.value = Resource.Success(reminderList)
        }
    }

    fun updateMedicineReminderTime(minutes: Int, medicineReminder: MedicineReminder) {
        viewModelScope.launch {
            val updatedReminder = repository.updateMedicineReminderTime(minutes = minutes, medicineReminder = medicineReminder)
            _uiStates.value = CreateReminderNotification(updatedReminder)
            getAllMedicineReminders()
        }
    }

    fun cancelAllPendingAlarms() {
        viewModelScope.launch {
            _medicineNotification.value = repository.futureMedicineReminders()

            _appointmentList.value?.data?.forEach { appointment ->
                _appointmentNotification.value = appointment
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.clearDatabase()
        }
    }

    fun archiveAudioDocument(audio: Audio) {
        viewModelScope.launch {
            _progress.value = true
            repository.archiveAudioDocument(audio.id!!).collect { resource ->
                if (resource is Resource.Success) {
                    loadRecommendations(loader = true)
                }
            }
        }
    }

    fun updateCount(type : String, id: Int){
        viewModelScope.launch {
            repository.updateCount(type,id).collect {
                if (it is Resource.Success){
                    _updateCount.value = true
                    loadMenu()
                }
            }
        }
    }

    fun loadMenu() {
        viewModelScope.launch {
            repository.getMenu().collect { resource ->
                if (resource is Resource.Success) {
                    _menuList.value!!.data!!.first { it.id == 1 }.notificationCount = resource.data.mealPlan.new
                    _menuList.value!!.data!!.first { it.id == 2 }.notificationCount = resource.data.prescription.new
                    _menuList.value!!.data!!.first { it.id == 3 }.notificationCount = resource.data.orientation.new
                    _menuList.value!!.data!!.first { it.id == 4 }.notificationCount = resource.data.recommendations.new
                    isLoading.set(false)
                    _menuList.value = _menuList.value
                }
            }
        }
    }

    fun createUpComingNotificationForUnKnownMedicine(serverList: List<Medicine>) {
        viewModelScope.launch {
            val localMedicine = repository.getLocalMedicines()

            val (_, discard)  = localMedicine.partition { it.id in serverList.map { item -> item.id } }
            val insert = serverList.filterNot { it.id in localMedicine.map { item -> item.id } }

            deleteMedicineAndReminder(discard)

            insert?.forEach { medicine ->
                repository.insertMedicine(medicine)
                _uiStates.value = CallMedicineReminderWorker(medicine)
            }
        }
    }

    private fun deleteMedicineAndReminder(medicines: List<Medicine>) {
        viewModelScope.launch {
            medicines.forEach { medicine ->
                repository.deleteMedicine(medicine)
                val notificationReminders = repository.futureMedicineReminders(medicine.id)
                notificationReminders.forEach { reminder ->
                    _uiStates.value = DeleteReminderNotification(reminder)
                }
                repository.deleteMedicineReminders(medicine.id)
            }
        }
    }

    fun loadLocalMedicines() {
        viewModelScope.launch {
            val medicines = repository.getLocalMedicines()
            _medicineList.value = if (medicines.isNullOrEmpty()) Resource.Empty else Resource.Success(medicines)
        }
        loadMedicines()
    }

    fun getUnArchiveAudios(loader: Boolean = false) {
        viewModelScope.launch {
            repository.getArchiveAudios().collect { resource ->
                _progress.value = resource is Resource.Loading && loader
                _archiveAudioList.value = resource
            }
        }
    }

    fun unArchiveAudio(audio: Audio) {
        viewModelScope.launch {
            _progress.value = true
            repository.unArchiveAudioDocument(audio.id!!).collect { resource ->
                if (resource is Resource.Success) {
                    getUnArchiveAudios(loader = true)
                    loadRecommendations(loader = false)
                }
            }
        }
    }
}