package com.adretsoftwares.cellsecuritycare;

/**
 * @author Sanat
 * MailSend class to access MailJet and send mail
 */

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.adretsoftwares.cellsecuritycare.SecurityService;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;

public class MailSend {

    static String HTMLContent="Temp",base64send;
    String timestamp=String.valueOf(new Timestamp(System.currentTimeMillis())); //Get the current time-stamp
    MailSend(String base64){

        //This uses a pre-defined HTML template for body of the e-mail
        HTMLContent="<p style=\"text-align: center;\"><span style=\"font-size: 24px;\"><strong><span style=\"color: rgb(255, 0, 0);\">Cell Security Intrusion Alert</span></strong></span></p>\n" +
                "<p style=\"text-align: center;\"><u>A Wrong PIN Was Entered On Your Phone</u></p>\n" +
                "<p style=\"text-align: center;\">Location:Latitude-10.000,Longitude:-120.00</p>\n" +
                "<p style=\"text-align: center;\">Device:"+SecurityService.deviceDetails+"</p>\n" +
                "<p style=\"text-align: center;\">Time Stamp:"+timestamp+"</p>\n" +
                "<p style=\"text-align: center;\">&nbsp;Intruders Image has been attached.</p>\n" +
                "<p style=\"text-align: center;\"><br></p>";
        base64send=base64;
        try {
            if(SecurityService.senderEmail!=null){
                SendMail(HTMLContent);
            }
        } catch (Exception e) {
            Log.d("Hawk.MailSend",e.getMessage());
        }
    }

    /**
     * Send the mail and checks response
     * @param HTML The predefined HTML string
     */
    static void SendMail(String HTML) throws JSONException, MailjetSocketTimeoutException, MailjetException {
        Log.d("base64",base64send);
        Retrievedata rt = new Retrievedata();
        rt.execute(base64send);

    }

   static class Retrievedata extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                MailjetClient client;
                MailjetRequest request;
                MailjetResponse response;
                client = new MailjetClient("29554f36589fd85d43fce4a071a584c7", "63e0671b5a27db6766a859ff8c46580f", new ClientOptions("v3.1"));
                //The request contains various JSON objects and JSON key-value pairs
                try {
                    request = new MailjetRequest(Emailv31.resource)
                            .property(Emailv31.MESSAGES, new JSONArray()
                                    .put(new JSONObject()
                                            .put(Emailv31.Message.FROM, new JSONObject()
                                                    .put("Email", "yatin7610@gmail.com")
                                                    .put("Name", "Cell Admin"))
                                            .put(Emailv31.Message.TO, new JSONArray()
                                                    .put(new JSONObject()
                                                            .put("Email", SecurityService.senderEmail)
                                                            .put("Name", "Client")))
                                            .put(Emailv31.Message.SUBJECT, "Greetings from Cell Security")
                                            .put(Emailv31.Message.TEXTPART, "Incorrect Pin Alert")
                                            .put(Emailv31.Message.HTMLPART, HTMLContent)
                                            .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                                    .put(new JSONObject()
                                                            .put("ContentType", "image/bmp")
                                                            .put("Filename", "Intruder.bmp")
                                                            .put("Base64Content", base64send)))));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    response = client.post(request);  //2XX: OK,  4XX: Not-OK
                } catch (MailjetException e) {
                    throw new RuntimeException(e);
                } catch (MailjetSocketTimeoutException e) {
                    throw new RuntimeException(e);
                }
                Log.d("response", String.valueOf(response.getStatus()));
                Log.d("response", String.valueOf(response.getData()));
                Log.d("mailSent to",SecurityService.senderEmail);
            }catch (Exception e){

            }
            return null;
        }
    }
}
