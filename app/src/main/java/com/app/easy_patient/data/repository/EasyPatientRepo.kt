package com.app.easy_patient.data.repository

import com.app.easy_patient.data.remote.EasyPatientApiService
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.database.MealPlanModel
import com.app.easy_patient.database.OrientationListModel
import com.app.easy_patient.database.PrescriptionListModel
import com.app.easy_patient.ktx.critical
import com.app.easy_patient.ktx.notification
import com.app.easy_patient.model.StatusModel
import com.app.easy_patient.model.kotlin.*
import com.app.easy_patient.wrappers.Resource
import com.app.easy_patient.wrappers.callApi
import com.app.easy_patient.wrappers.data
import com.app.easy_patient.wrappers.succeeded
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import java.util.*
import javax.inject.Inject

interface EasyPatientRepo {
    suspend fun uploadPhoto(file: MultipartBody.Part): Flow<Resource<UploadImage>>
    suspend fun deletePhoto(): Flow<Resource<Status>>
    suspend fun getAppointments(): Flow<Resource<List<Appointment>>>
    fun getAppointment(id: Int): Appointment?
    suspend fun getAudios(): Flow<Resource<List<Audio>>>
    fun getAudio(id: Int): Audio?
    suspend fun getMedicine(): Flow<Resource<List<Medicine>>>
    suspend fun getMealPlan(): Flow<Resource<List<MealPlanModel>>>
    suspend fun getPrescription(): Flow<Resource<List<PrescriptionListModel>>>
    suspend fun getOrientation(): Flow<Resource<List<OrientationListModel>>>
    suspend fun getArchiveMealPlan(): Flow<Resource<List<MealPlanModel>>>
    suspend fun getArchivePrescription(): Flow<Resource<List<PrescriptionListModel>>>
    suspend fun getArchiveOrientation(): Flow<Resource<List<OrientationListModel>>>
    suspend fun getMedicineReminders(): MutableList<MedicineReminder>
    suspend fun updateMedicineReminderStatus(status: MedicineStatus, medicineReminder: MedicineReminder)
    fun getMedicineById(medicineId: Int): Medicine?
    suspend fun updateMedicineReminderTime(minutes: Int, medicineReminder: MedicineReminder): MedicineReminder
    suspend fun insertMedicineReminder(medicineReminder: MedicineReminder)
    suspend fun clearDatabase()
    suspend fun medicineRemindersForNotification(medicineId: Int): MutableList<MedicineReminder>
    suspend fun getArchiveAudios(): Flow<Resource<List<Audio>>>
    suspend fun unArchiveAudioDocument(id: Int): Flow<Resource<StatusModel>>
    suspend fun archiveAudioDocument(id: Int): Flow<Resource<StatusModel>>
    suspend fun getMenu(): Flow<Resource<MenuModel>>
    suspend fun insertMedicineReminders(medicineReminder: List<MedicineReminder>)
    fun getMedicineRemindersString(medicineId: Int): String
    suspend fun getLocalMedicines(): List<Medicine>
    fun getMedicineReminderById(reminderId: String): MedicineReminder?
    fun insertMedicine(medicine: Medicine)
    suspend fun saveAppointment(appointment: Appointment)
    suspend fun updateCount(type: String,id: Int): Flow<Resource<StatusModel>>
    fun getLocalAppointments(): List<Appointment>
    suspend fun futureMedicineReminders(): List<MedicineReminder>
    suspend fun futureMedicineReminders(medicineId: Int): List<MedicineReminder>
    suspend fun deleteMedicine(medicine: Medicine)
    suspend fun deleteMedicineReminders(medicineId: Int)
    suspend fun getAllMedicineRemindersByMedicineId(medicineId: Int): List<MedicineReminder>
    suspend fun updateMedicineReminders(medicine: Medicine)
    suspend fun deleteUserAccount(userName: String, password: String): Flow<Resource<ResponseModel>>
}

class EasyPatientRepoImpl @Inject constructor(
    private val apiService: EasyPatientApiService,
    private val database: EasyPatientDatabase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EasyPatientRepo {

    override suspend fun uploadPhoto(file: MultipartBody.Part): Flow<Resource<UploadImage>>  = flow{
        val result = callApi {
            apiService.uploadProfilePicture(file)
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun deletePhoto(): Flow<Resource<Status>> = flow {
        val result = callApi {
            apiService.deleteProfilePicture()
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun getAppointments(): Flow<Resource<List<Appointment>>> = flow {
        val result = callApi {
            apiService.getAppointments()
        }
        emit(result)
    }.map { resource ->
        if (resource.succeeded()) {
            resource.data!!.onEach { appointment ->
                database.appointmentDao().insertAppointment(appointment)
            }
        }
        resource
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.appointmentDao().getAllAppointments
        }
        emit(result)
    }

    override fun getAppointment(id: Int): Appointment? {
        return database.appointmentDao().getAppointmentById(id)
    }

    override suspend fun getAudios(): Flow<Resource<List<Audio>>> = flow {
        emit(Resource.Loading)
        val result = callApi {
            apiService.getAudios()
        }
        emit(result)
    }.map { resource ->
        if (resource.succeeded()) {
            resource.data!!.onEach { audio ->
                database.audioDao().insertAudio(audio)
            }
        }
        resource
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.audioDao().getAllAudios
        }
        emit(result)
    }

    override fun getAudio(id: Int): Audio? {
        return database.audioDao().getAudioById(id)
    }

    override suspend fun getMedicine(): Flow<Resource<List<Medicine>>> = flow {
        val result = callApi {
            apiService.getMedicines()
        }
        emit(result)
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.medicineDetailDao().medicineDetailData
        }
        emit(result)
    }

    override suspend fun getMealPlan(): Flow<Resource<List<MealPlanModel>>> = flow {
        val result = callApi {
            apiService.getMealPlan()
        }
        emit(result)
    }.map { resource ->
        if (resource.succeeded()) {
            resource.data!!.onEach { mealPlan ->
                database.mealPlanDetailDao().insertMealPlanItem(mealPlan)
            }
        }
        resource
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.mealPlanDetailDao().mealPlanDetailData
        }
        emit(result)
    }

    override suspend fun getPrescription(): Flow<Resource<List<PrescriptionListModel>>> = flow {
        val result = callApi {
            apiService.getPrescriptions()
        }
        emit(result)
    }.map { resource ->
        if (resource.succeeded()) {
            resource.data!!.onEach { prescription ->
                database.prescriptionDetailDao().insertPrescriptionItem(prescription)
            }
        }
        resource
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.prescriptionDetailDao().prescriptionDetailData
        }
        emit(result)
    }

    override suspend fun getOrientation(): Flow<Resource<List<OrientationListModel>>> = flow {
        val result = callApi {
            apiService.getOrientations()
        }
        emit(result)
    }.map { resource ->
        if (resource.succeeded()) {
            resource.data!!.onEach { orientation ->
                database.orientationDetailDao().insertOrientationItem(orientation)
            }
        }
        resource
    }.flowOn(coroutineDispatcher).catch {
        val result = callApi {
            database.orientationDetailDao().orientationDetailData
        }
        emit(result)
    }

    override suspend fun getArchiveMealPlan(): Flow<Resource<List<MealPlanModel>>> = flow {
        val result = callApi {
            apiService.getMealPlanArchive()
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun getArchivePrescription(): Flow<Resource<List<PrescriptionListModel>>> = flow {
        val result = callApi {
            apiService.getPrescriptionsArchive()
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun getArchiveOrientation(): Flow<Resource<List<OrientationListModel>>> = flow {
        val result = callApi {
            apiService.getOrientationsArchive()
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun getMedicineReminders(): MutableList<MedicineReminder> {
        var medicineReminders = database.medicineReminderDao().medicineReminderList()
        return if (medicineReminders.isNullOrEmpty())
            mutableListOf()
        else {
            val startTime = Calendar.getInstance()
            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.SECOND, 0)

            val endTime = Calendar.getInstance()
            endTime.set(Calendar.HOUR_OF_DAY, 23)
            endTime.set(Calendar.MINUTE, 59)
            endTime.set(Calendar.SECOND, 59)

            medicineReminders = medicineReminders.filter { reminder ->
                reminder.reminderTime() > startTime.timeInMillis && reminder.reminderTime() < endTime.timeInMillis
            }.sortedWith(compareBy { it.reminderTime() }).toMutableList()

            return if (medicineReminders.isNullOrEmpty())
                mutableListOf()
            else
                medicineReminders
        }
    }

    override suspend fun updateMedicineReminderStatus(status: MedicineStatus, medicineReminder: MedicineReminder) {
        medicineReminder.status = status
        database.medicineReminderDao().insertMedicineReminder(medicineReminder)
    }

    override fun getMedicineById(medicineId: Int): Medicine? {
        return database.medicineDetailDao().getMedicineDetail(medicineId)
    }

    override suspend fun updateMedicineReminderTime(minutes: Int, medicineReminder: MedicineReminder): MedicineReminder {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = medicineReminder.reminderTime()
        calendar.add(Calendar.MINUTE, minutes)

        medicineReminder.status = MedicineStatus.Future
        medicineReminder.setUpdateTime(calendar.timeInMillis)

        database.medicineReminderDao().insertMedicineReminder(medicineReminder)
        return medicineReminder
    }

    override suspend fun insertMedicineReminder(medicineReminder: MedicineReminder) {
        database.medicineReminderDao().insertMedicineReminder(medicineReminder)
    }

    override suspend fun clearDatabase() {
        database.clearAllTables()
    }

    override suspend fun medicineRemindersForNotification(medicineId: Int): MutableList<MedicineReminder> {
        return database.medicineReminderDao().medicineReminderListForNotification(medicineId, Calendar.getInstance().timeInMillis)
    }

    override suspend fun getArchiveAudios(): Flow<Resource<List<Audio>>> = flow {
        emit(Resource.Loading)
        val result = callApi {
            apiService.getArchiveAudios()
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun unArchiveAudioDocument(id: Int): Flow<Resource<StatusModel>> = flow {
        emit(Resource.Loading)
        val result = callApi {
            apiService.unArchiveAudioDocument(id)
        }
        emit(result)
    }.flowOn(coroutineDispatcher)

    override suspend fun archiveAudioDocument(id: Int): Flow<Resource<StatusModel>> = flow {
        emit(Resource.Loading)
        val result = callApi {
            apiService.archiveAudioDocument(id)
        }
        emit(result)
    }.onEach {
        if (it.succeeded()) {
            if (it.data!!.status) {
                database.audioDao().deleteAudioDocument(id)
            }
        }
    }.flowOn(coroutineDispatcher)

    override suspend fun getMenu(): Flow<Resource<MenuModel>> = flow {
        val result = callApi {
            apiService.getMenu()
        }
        emit(result)
    }

    override suspend fun insertMedicineReminders(medicineReminder: List<MedicineReminder>) {
        database.medicineReminderDao().insertMedicineReminders(medicineReminder)
    }

    override fun getMedicineRemindersString(medicineId: Int): String {
        val medicineReminderList: List<MedicineReminder> =
            database.medicineReminderDao().medicineReminderList(medicineId)
        val gson = Gson()
        return gson.toJson(medicineReminderList)
    }

    override suspend fun getLocalMedicines(): List<Medicine> {
        return database.medicineDetailDao().medicineDetailData
    }

    override fun getMedicineReminderById(reminderId: String): MedicineReminder? {
        return database.medicineReminderDao().medicineReminderById(reminderId)
    }

    override fun insertMedicine(medicine: Medicine) {
        database.medicineDetailDao().insertMedicineDetail(medicine)
    }

    override suspend fun saveAppointment(appointment: Appointment) {
        database.appointmentDao().insertAppointment(appointment)
    }

    override suspend fun updateCount(type: String,id: Int): Flow<Resource<StatusModel>> = flow {
        val result = callApi {
            apiService.updateCount(type,id)
        }
        emit(result)
    }

    override fun getLocalAppointments(): List<Appointment> {
        return database.appointmentDao().getAllAppointments
    }

    override suspend fun futureMedicineReminders(): List<MedicineReminder> {
        var medicineReminders = database.medicineReminderDao().medicineReminderList()
        return if (medicineReminders.isNullOrEmpty())
            mutableListOf()
        else {
            medicineReminders = medicineReminders.filter { reminder ->
                reminder.reminderTime() > Calendar.getInstance().timeInMillis
            }.sortedWith(compareBy { it.reminderTime() }).toMutableList()

            return if (medicineReminders.isNullOrEmpty())
                mutableListOf()
            else
                medicineReminders
        }
    }

    override suspend fun futureMedicineReminders(medicineId: Int): List<MedicineReminder> {
        var medicineReminders = database.medicineReminderDao().medicineReminderList(medicineId)
        return if (medicineReminders.isNullOrEmpty())
            mutableListOf()
        else {
            medicineReminders = medicineReminders.filter { reminder ->
                reminder.reminderTime() > Calendar.getInstance().timeInMillis
            }.sortedWith(compareBy { it.reminderTime() }).toMutableList()

            return if (medicineReminders.isNullOrEmpty())
                mutableListOf()
            else
                medicineReminders
        }
    }

    override suspend fun deleteMedicine(medicine: Medicine) {
        database.medicineDetailDao().deleteMedicineEntry(medicine)
    }

    override suspend fun deleteMedicineReminders(medicineId: Int) {
        database.medicineReminderDao().deleteAllMedicineReminders(medicineId)
    }

    override suspend fun getAllMedicineRemindersByMedicineId(medicineId: Int): List<MedicineReminder> {
        return database.medicineReminderDao().medicineReminderList(medicineId)
    }

    override suspend fun updateMedicineReminders(medicine: Medicine) {
        database.medicineReminderDao().updateMedicineReminderExceptTime(
            medicineId = medicine.id,
            name = medicine.name!!,
            dosage = medicine.dosage!!,
            notification = medicine.notification(),
            pictureLink = medicine.picture_link,
            defaultIcon = medicine.default_icon?.toInt() ?: 1,
            critical = medicine.critical())
    }

    override suspend fun deleteUserAccount(
        userName: String,
        password: String
    ): Flow<Resource<ResponseModel>> = flow {
        val result = callApi {
            apiService.deleteAccount(userName, password)
        }
        emit(result)
    }
}