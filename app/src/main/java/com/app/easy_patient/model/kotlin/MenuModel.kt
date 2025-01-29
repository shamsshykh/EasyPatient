package com.app.easy_patient.model.kotlin

data class MenuModel(
    val orientation: MenuSubModel,
    val prescription: MenuSubModel,
    val mealPlan: MenuSubModel,
    val recommendations: MenuSubModel
)

data class MenuSubModel(
    val new: Int = 0
)