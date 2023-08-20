package com.example.boozzapp.fragments

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.boozzapp.activities.MainActivity
import com.example.boozzapp.controls.CustomDialog
import com.example.boozzapp.controls.CustomProgressDialog
import com.example.boozzapp.utils.StoreUserData


open class BaseFragment : Fragment() {
    lateinit var mActivity: FragmentActivity
    lateinit var storeUserData: StoreUserData
    lateinit var progressDialog: CustomProgressDialog
    lateinit var alert: CustomDialog

    fun showAlert(message: String) {
        alert = CustomDialog(mActivity)
        //alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok", View.OnClickListener {
            alert.dismiss()
        })
    }

    fun showAlert(message: String, finish: Boolean) {
        alert = CustomDialog(mActivity)
        alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok", View.OnClickListener {
            alert.dismiss()
            if (finish) {
                startActivity(Intent(mActivity, MainActivity::class.java))
                mActivity.finish()
            }
        })
    }

    fun showAlert(message: String, finish: Boolean, java: Class<*>) {
        alert = CustomDialog(mActivity)
        alert.setCancelable(false)
        alert.show()
        alert.setMessage(message)
        alert.setPositiveButton("ok", View.OnClickListener {
            alert.dismiss()
            if (finish) {
                startActivity(Intent(mActivity, java).putExtra("isFinish", true))
            }
        })
    }

    fun dismissProgress() {
        progressDialog.dismiss()
    }

    fun showProgress() {
        progressDialog = CustomProgressDialog(mActivity)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun showProgress(message: String) {
        progressDialog = CustomProgressDialog(mActivity)
        progressDialog.setCancelable(false)
        progressDialog.setTitle(message)
        progressDialog.show()
    }
}