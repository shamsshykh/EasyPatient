package com.app.easy_patient.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.app.easy_patient.model.kotlin.Address;
import com.app.easy_patient.model.kotlin.Appointment;
import com.app.easy_patient.model.kotlin.Audio;
import com.app.easy_patient.model.kotlin.AudioFile;
import com.app.easy_patient.model.kotlin.Medicine;
import com.app.easy_patient.model.kotlin.MedicineReminder;
import com.app.easy_patient.model.kotlin.converter.Converter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Medicine.class,
        Appointment.class,
        Address.class,
        OrientationListModel.class,
        PrescriptionListModel.class,
        MealPlanModel.class,
        Audio.class,
        AudioFile.class,
        MedicineReminder.class},
        version = 1,
        exportSchema = false)
@TypeConverters({Converter.class})
public abstract class
EasyPatientDatabase extends RoomDatabase {
    public abstract MedicineDetailDao medicineDetailDao();

    public abstract AppointmentDao appointmentDao();

    public abstract OrientationDetailDao orientationDetailDao();

    public abstract PrescriptionDetailDao prescriptionDetailDao();

    public abstract MealPlanDetailDao mealPlanDetailDao();

    public abstract AudioDao audioDao();

    public abstract MedicineReminderDao medicineReminderDao();

    private static volatile EasyPatientDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EasyPatientDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EasyPatientDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EasyPatientDatabase.class, "easy_patient_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
