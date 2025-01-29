package com.app.easy_patient.ui.delete_account

sealed class DeleteAccountStates

object LogoutUser: DeleteAccountStates()

object InvalidCredentials: DeleteAccountStates()

object Failure: DeleteAccountStates()