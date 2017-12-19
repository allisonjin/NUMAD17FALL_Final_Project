package edu.neu.madcourse.zhiyaojin.finalproject.common.service;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

//import edu.neu.madcourse.zhiyaojin.assignments.activities.CongratsDialogActivity;
//import edu.neu.madcourse.zhiyaojin.assignments.activities.ScroggleActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

//        showHighestScoreNotification(remoteMessage);
    }
    // [END receive_message]


//    private void showHighestScoreNotification(final RemoteMessage message) {
//        if (!getActivity().getClass().equals(ScroggleActivity.class)) {
//            Intent intent = new Intent(this, CongratsDialogActivity.class);
//            if (message.getData().size() > 0) {
//                intent.putExtra("gameId", message.getData().get("data"));
//            }
//            intent.putExtra("body", message.getNotification().getBody());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//    }

//    public static Activity getActivity() {
//        try {
//            Class activityThreadClass = Class.forName("android.app.ActivityThread");
//            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
//            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
//            activitiesField.setAccessible(true);
//
//            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
//            if (activities == null)
//                return null;
//
//            for (Object activityRecord : activities.values()) {
//                Class activityRecordClass = activityRecord.getClass();
//                Field pausedField = activityRecordClass.getDeclaredField("paused");
//                pausedField.setAccessible(true);
//                if (!pausedField.getBoolean(activityRecord)) {
//                    Field activityField = activityRecordClass.getDeclaredField("activity");
//                    activityField.setAccessible(true);
//                    Activity activity = (Activity) activityField.get(activityRecord);
//                    return activity;
//                }
//            }
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}

