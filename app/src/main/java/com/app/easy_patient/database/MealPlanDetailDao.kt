package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.easy_patient.database.MealPlanModel

@Dao
interface MealPlanDetailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMealPlanDetail(mealPlanDetail: List<MealPlanModel>)

    @get:Query("SELECT * FROM Meal_Plan_Detail")
    val mealPlanDetailData: List<MealPlanModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMealPlanItem(mealPlanItem: MealPlanModel)

    @Query("DELETE FROM Meal_Plan_Detail WHERE id = :id")
    fun deleteMealPlanItem(id: Int): Int

    @Query("SELECT * FROM Meal_Plan_Detail WHERE id= :id")
    fun getMealPlanById(id: Int): MealPlanModel?
}