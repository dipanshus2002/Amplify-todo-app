package com.example.myamplifyapp

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin

import com.amplifyframework.core.Amplify

class MyAmplifyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // Add these lines to add the AWSApiPlugin and AWSCognitoAuthPlugin plugins
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())

            Amplify.configure(applicationContext)

            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
}