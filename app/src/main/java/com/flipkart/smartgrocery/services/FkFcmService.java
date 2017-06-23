package com.flipkart.smartgrocery.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.activities.BarCodeScannerActivity;
import com.flipkart.smartgrocery.activities.MainActivity;
import com.flipkart.smartgrocery.activities.NextTimeBuyActivity;
import com.flipkart.smartgrocery.activities.ReceiptScanEntryActivity;
import com.flipkart.smartgrocery.activities.ShoppingCartActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by satyanarayana.p on 22/06/17.
 */

public class FkFcmService extends FirebaseMessagingService {


    private static final String TAG = "FkFcmService";

    private static final String ACTION_BARCODE_SCANNER = "barcodeScanner";

    private static final String ACTION_RECEIPT_SCANNER = "receiptScanner";

    private static final String ACTION_SHOW_CART_ITEMS = "showCartItems";

    private static final String ACTION_SHOW_ADD_TO_CART = "addToCart";

    private static final String ACTION_SHOW_REMIND_ME_LATER = "remindMeLater";

    private static final String ACTION_SHOW_CHECKOUT = "checkout";

    private static final String KEY_TITLE = "title";

    private static final String KEY_DESCRIPTION = "description";

    private static final String KEY_PRODUCT = "product";

    private static final String ACTION1_TITLE = "action1Title";
    private static final String ACTION2_TITLE = "action2Title";

    private static final String ACTION1_CODE = "action1Code";
    private static final String ACTION2_CODE = "action2Code";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        sendNotification(message.getData());

    }

    private void sendNotification(Map<String, String> messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(this);

        notificationBuilder2.setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationBuilder2.setSmallIcon(R.drawable.fk_notification_secondaryicon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody.get(KEY_TITLE))
                        .setSummaryText(messageBody.get(KEY_DESCRIPTION)))
                .setContentTitle(messageBody.get(KEY_TITLE))
                .setContentText(messageBody.get(KEY_DESCRIPTION))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(messageBody.get(ACTION1_CODE))) {
            notificationBuilder2.addAction(-1, messageBody.get(ACTION1_TITLE), getIntentForAction(messageBody.get(ACTION1_CODE)));
        }
        if (!TextUtils.isEmpty(messageBody.get(ACTION2_CODE))) {
            notificationBuilder2.addAction(-1, messageBody.get(ACTION2_TITLE), getIntentForAction(messageBody.get(ACTION2_CODE)));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder2.build());
    }

    private PendingIntent getIntentForAction(String action) {
        Intent intent;
        PendingIntent pendingIntent = null;
        if (TextUtils.equals(action, ACTION_BARCODE_SCANNER)) {
            intent = new Intent(this, BarCodeScannerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (TextUtils.equals(action, ACTION_RECEIPT_SCANNER)) {
            intent = new Intent(this, ReceiptScanEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (TextUtils.equals(action, ACTION_SHOW_CART_ITEMS)) {
            intent = new Intent(this, NextTimeBuyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (TextUtils.equals(action, ACTION_SHOW_ADD_TO_CART)) {

        } else if (TextUtils.equals(action, ACTION_SHOW_REMIND_ME_LATER)) {

        } else if (TextUtils.equals(action, ACTION_SHOW_CHECKOUT)) {
            intent = new Intent(this, ShoppingCartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        return pendingIntent;
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

}
