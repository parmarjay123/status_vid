package com.example.boozzapp.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.boozzapp.R
import com.example.boozzapp.controls.CTextView
import java.util.*


object Utils {


    fun internetAlert(activity: Context) {
        AlertDialog.Builder(activity)
            .setMessage("Please check internet connection.")
            .setPositiveButton(activity.getString(R.string.Ok)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    fun isOnline(context: Context): Boolean {
        val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        for (i in info.indices)
            if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        return false
    }

    fun isValidEmail(email: EditText): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
    }

    fun selectDate(activity: Activity, btnDate: CTextView) {
        DatePickerDialog(
            activity, R.style.my_dialog_theme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                btnDate.text =
                    (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DATE, dayOfMonth)
                //calendar.getTimeInMillis() + "";
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    fun selectTime(activity: Activity, btnTime: CTextView) {
        TimePickerDialog(
            activity, R.style.my_dialog_theme,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                btnTime.text = "$selectedHour:$selectedMinute"
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            false
        ).show() //is24HourView
    }

    fun isEmpty(view: View): Boolean {
        if (view is EditText) {
            if (view.text.toString().isEmpty()) {
                return true
            }
        } else if (view is Button) {
            if (view.text.toString().isEmpty()) {
                return true
            }
        } else if (view is TextView) {
            if (view.text.toString().isEmpty()) {
                return true
            }
        }
        return false
    }

    fun showListDialog(activity: Activity, btnShow: Button, list: ArrayList<String>) {

        val builder = AlertDialog.Builder(activity)
        val dataAdapter = ArrayAdapter(
            activity,
            android.R.layout.simple_dropdown_item_1line, list
        )
        //pass custom layout with single textview to customize list
        builder.setAdapter(dataAdapter) { dialog, which -> btnShow.text = list[which] }
        val dialog = builder.create()
        dialog.show()
    }

    fun hideKB(activity: Activity, view: View?) {
        //View view = this.getCurrentFocus();
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setTheme(activity: Activity) {
        if (StoreUserData(activity).getString(Constants.USER_LANGUAGE).isEmpty()) {
            StoreUserData(activity).setString(Constants.USER_LANGUAGE, "ar")
        }
        val locale = Locale(StoreUserData(activity).getString(Constants.USER_LANGUAGE))
        Locale.setDefault(locale)
        val config = Configuration()

        config.setLocale(locale)
        activity.baseContext.resources.updateConfiguration(
            config,
            activity.baseContext.resources.displayMetrics
        )

    }


}