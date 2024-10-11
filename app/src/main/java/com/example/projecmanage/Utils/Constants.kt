package com.example.projecmanage.Utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
//import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

object Constants {

    const val USERS :String ="Users"
    const val BOARD : String ="Boards"

    const val GALLERY : Int = 1
    const val CAMERA : Int = 2

    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val EMAIL : String = "email"
    const val DOCUMENT_ID = "documentId"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"
    const val TASK_LIST : String = "taskList"
    const val Board_Detail : String ="board_details"
    const val ID : String ="id"

    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "CARD_list_item_position"

    const val PROJEMANAGE_PREFERENCES = "ProjemanagePrefs"
    const val FCM_TOKEN_UPDATE = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val BOARD_MEMBERS_LIST : String = "board_members_list"
    const val SELECT : String ="select"
    const val UN_SELECT : String = "UnSelect"

    const val FCM_BASE_URL :String = "https://fcm.googleapis.com/v1/projects/projecmanage-ffe79/messages:send"
    const val FCM_AUTHORIZATION :String = "authorization"
    const val FCM_KEY : String = "key"
    const val FCM_SERVER_KEY : String =""
    const val FCM_KEY_TITLE : String ="title"
    const val FCM_MESSAGE : String ="message"
    const val FCM_KEY_DATA : String ="data"
    const val FCM_KEY_TO:String="to"

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"


   /* fun getAccessToken():String{
        try{
            val jsonString ="{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"projecmanage-ffe79\",\n" +
                    "  \"private_key_id\": \"b175ba01bf4ef83282b125f085f8e7faf9f451ba\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDfYPlbcUYruNir\\n868I7jJCkqrpfI938yxpp0CVCqZOByP6JDXZLkyW5Ym40Qm1wUUunIwCLFGP3N9H\\nhpmNHUB/klxnR8eZLyN6xe0yyx0S+g5EngMVX24YBazFLYdmtqRye2Gmv8rCeuec\\nx/sYUQOkisyV5EVTqdWkWFoXrFv08o5XwWp4By32veuT2s5FoAlkQxKvWmorAsLz\\nagBmsIbwZY5lKDsqdeF7P6bIWtZpGaP6NHkG7K1Zb4FHkF1fYTXaKlbT5uxDD12R\\n37L7pKKxmZo8yQnGviln9Js9zw57GUDmWXcgT6aRY02EEcKIgiADUq251B3IuQOv\\nH4IqxIK/AgMBAAECggEAArSq1eaNhN+50Xy4/ODfFKWwfUsyyln/NTkUlARA1xwe\\nCEciVy2eOYuxOzReVj3E5+zMg1rkaAYzDCuUtjwJo8ydjZYLKboDnNaYlMTCEZCc\\nK0zwrknCzHaxIjhMILyLSbm13pkJzIju6ka/2LUOgr04VNki1Sxu9+J8FjenwNPz\\nhCpCLrNZmM7lMCUVhS4YU1/IK85KFOFiaCL9NwSX1hcc+P1Rm2o++xurccZj4Xg8\\nbvHxitUrOkFSem4b9mKdzgezwjn5aPh3SUurhecDT0+oFWAaNSWGe00o7/UEVmIg\\ny8zsCXw1xrLvGqcfRUFUDwdSFy4jororR8xo/7rOoQKBgQD7eO6qB5UMmZ7Q60sX\\nXFuI+SP3sh2zYEv9gMWdC9yXBcuRL9PK9ZBSRd4rwE8WWu4+eDn2BQgxNVkgcrBn\\n0jwg4SiIrUurgrcKSP73WoR2Lzxr+Tjp3V/K2tjMvsW6fofXNi0VfWo+uiLK4ulU\\nl9hbt5C4+ASnu2UKAzugPUTEYQKBgQDjZo4Ow0Kuxsq4t6Fl4dAwnPbw++f9hdIJ\\nRUJK83q1mEbEeLBsUK2IwInhwtGLiQ19hztV4LdDg/UIfx6AmSis7HtKD+dQyTMa\\nVdhaYGbkHTjGFzcJvbRFPPqe/9SdORlWJaYsFHBd0cF16sXhiX1S4XvUq2CV3Ch9\\nr6UQKMqbHwKBgEs5+QNqMz9KsUTELnl1inhmN0lAwjvHwdO68uKxCrMu65+qFAxx\\n+1NxTS3/YGlKmFydsJEVHkdCM0BOrtDQBma7lFmWpb1P5BuyjHtIW7nTFZsjOdBU\\nBfT0FMjjBNluQoqxzTjRhLfxDLluuegxaGq/dg6vOw1GNMqS/wJHccvhAoGAFNtk\\nLjB0nkk3cMfO8uRKTxsmfyXrulIa2KTUlsFMQPmti/mx3M1tjTxzqRRQWecBKMFN\\njOvmgzfIMrZOGudxVqL+D+XzhnJd0I2NIYT0CvovKeQZNTiEH1GffxPh8WW6UC91\\n1VcukzxY7TLSYYRVzVNHI1NwsW4W/KgukJPdOWsCgYB84E2dkhuvB7I7jOpVs40g\\nPK98kYlVxnOkRIKwZxKvoRNqJlvEPZtpkcwv64GTWJej2iq0Q1/ua+cseCPJcoL0\\nFPoSqjDLP/ui6WMz4Xeq1x5aPcait8ar+UbePNU05882i+igpbSekbq9h7OXjGpq\\nxziOQRh8Q0vPaC/o10Khtg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-v5fki@projecmanage-ffe79.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"108448380788272794293\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-v5fki%40projecmanage-ffe79.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredential = GoogleCredentials.fromStream(stream)
              .createScoped(arrayListOf(firebaseMessagingScope))
            googleCredential.refresh()
            return googleCredential.accessToken.tokenValue
        }catch (e:Exception){
            Log.d("error", "error in accessToken : ${e.message}")
            return null.toString()
        }
    }

    */


    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
            activity.contentResolver.getType(uri!!))
    }

    fun showDialogAlert(activity:Activity,message: String) {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setMessage(message)
        alertDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("GO TO SETTINGS"){
                _,_->
            try{
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",activity.packageName,null)
                intent.data=uri
                activity.startActivity(intent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        alertDialog.show()
    }




}