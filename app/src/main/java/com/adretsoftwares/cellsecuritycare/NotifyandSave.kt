package com.adretsoftwares.cellsecuritycare

import com.adretsoftwares.cellsecuritycare.util.LocationService.lattitude

import com.adretsoftwares.cellsecuritycare.util.LocationService.longitude


import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.adretsoftwares.cellsecuritycare.CardAdapter.isWrongPasswordEnterd
import com.adretsoftwares.cellsecuritycare.util.UtilityAndConstant
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar


class NotifyandSave {
    companion object {
        fun LoginRegister(context: Context, mobile_number: String, message: String,image:String,image2: String) {
//            var progressBar = ProgressDialog(context)
//            progressBar.setCancelable(true)
//            progressBar.setMessage("Please wait ....")
//            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//            progressBar.setProgress(0)
//            progressBar.setMax(100)
//         val service = Intent(context, LocationService::class.java)
//            startService(service)

            val cord = "$lattitude,$longitude"


            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val current = formatter.format(time)
            //Initializing ProgressBar
            //val progressBar = context.findViewById<View>(R.id.progressBar1) as ProgressBar
            class Login : AsyncTask<Void?, Void?, String?>() {

                override fun doInBackground(vararg p0: Void?): String? {
                    UtilityAndConstant.init(context)
                    UtilityAndConstant.saveString("wrongPin","wrongPinEntry")
                    isWrongPasswordEnterd = true
                    val requestHandler = RequestHandler()
                    Log.e("mobile_number", mobile_number)
                    //creating request parameters
                    val params = HashMap<String, String>()
                    params["message"] = message
                    params["mobile"] = mobile_number
                    params["coordinates"] = cord         //"321.00,123.40"
                    params["image"] = image
                    params["image2"] = image2
                    // params.put("token", token);

                    //returing the response
                    return requestHandler.sendPostRequest(
                        "https://cellsecuritycare.com/api/index.php",
                        params
                    )
                }

                override fun onPostExecute(s: String?) {
                    super.onPostExecute(s)
                  //  progressBar.hide()
                    try {
                        //converting response to json object
                        Log.e("Error ", s!!)
                      //  val obj = JSONObject(s)

                        //if no error in response
//                        if (obj.getBoolean("error")) {
//                            Toast.makeText(
//                                context,
//                                obj.getString("message"),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Toast.makeText(
//                                context,
//                                obj.getString("message"),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            val ul = Login()
            ul.execute()
        }
    }
}