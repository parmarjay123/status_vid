package com.example.boozzapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.boozzapp.controls.CustomDialog
import com.example.boozzapp.controls.CustomProgressDialog
import com.example.boozzapp.utils.StoreUserData


open class BaseActivity : AppCompatActivity() {
    lateinit var activity: AppCompatActivity
    lateinit var storeUserData: StoreUserData
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var alert: CustomDialog

    fun showAlert(message: String) {
        alert = CustomDialog(activity)
        alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok") {
            alert.dismiss()
        }
    }

    fun showAlert(message: String, finish: Boolean) {
        alert = CustomDialog(activity)
        alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok") {
            alert.dismiss()
            if (finish) {
                finish()
            }
        }
    }

    fun showAlert(message: String, finish: Boolean, java: Class<*>) {
        alert = CustomDialog(activity)
        alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok") {
            alert.dismiss()
            if (finish) {
                startActivity(Intent(activity, java))
            }
        }
    }


    fun showAlert(title: String, message: String) {
        alert = CustomDialog(activity)
        alert.setCancelable(false)
        alert.show()
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setPositiveButton("ok") {
            alert.dismiss()

        }
    }

    fun showProgress() {
        progressDialog = CustomProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun dismissProgress() {
        progressDialog.dismiss()
    }

    fun showProgress(message: String) {
        progressDialog = CustomProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setTitle(message)
        progressDialog.show()
    }


}