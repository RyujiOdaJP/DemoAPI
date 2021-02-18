package com.example.demo_api

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallApiAsyncTask().execute()
    }

    private inner class CallApiAsyncTask() : AsyncTask<Any, Void, String>() {
        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String = ""
            var connection: HttpURLConnection? = null

            try {
                val url = URL( "https://run.mocky.io/v3/7afe1d8f-9b48-4e59-a8ac-c361a9ee97930")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult: Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    result = stringBuilder.toString()

                } else {
                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
            } catch (e: Exception) {
                result = "Error:" + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)

                val responseData = Gson().fromJson(result, ResponseData::class.java)
                Log.i("Message", responseData.message)
                Log.i("User Id", "${responseData.user_id}")
                Log.i("Name", responseData.name)
                Log.i("Email", responseData.email)
                Log.i("Mobile", "${responseData.mobile}")

                // Profile Details
                Log.i("Is Profile Completed", "${responseData.profile_details.is_profile_completed}")
                Log.i("Rating", "${responseData.profile_details.rating}")

                // Data List Details.
                Log.i("Data List Size", "${responseData.data_list.size}")

                for (item in responseData.data_list.indices) {
                    Log.i("Value $item", "${responseData.data_list[item]}")

                    Log.i("ID", "${responseData.data_list[item].id}")
                    Log.i("Value", responseData.data_list[item].value)
                }


                val jsonObject = JSONObject(result)
                val id = jsonObject.optString("id")
                Log.i("id", id)
            }
        }

        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }
    }
}