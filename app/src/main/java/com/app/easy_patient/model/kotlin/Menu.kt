package com.app.easy_patient.model.kotlin

data class Menu(val id: Int,
                val name: String,
                var archiveCount: Int = 0,
                var notificationCount: Int = 0)
