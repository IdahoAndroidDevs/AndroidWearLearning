package rowley.androidwearlearning;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NotificationsActivity extends AppCompatActivity {

    private final int BASIC_NOTIFICATION_ID = 1;
    private final int CUSTOM_NOTIFICATION_ID = 2;
    private final int VOICE_REPLY_NOTIFICATION_ID = 4;
    private final int STACK_NOTIFICATION_ID = 8;

    private final int MAX_STACK_NOTIFICATION_COUNT = 2;
    private final String STACK_NOTIFICATIONS_GROUP = "stackNotificationsGroup";

    public static final String EXTRA_VOICE_REPLY = "extraVoiceReply";

    private Unbinder unbinder;

    @BindView(R.id.notification_input)
    EditText notificationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unbinder = ButterKnife.bind(this);

        NotificationManagerCompat.from(this).cancelAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.basic_notification_button)
    void sendBasicNotification() {
        displayBasicNotification(getString(R.string.notification_title),
                getString(R.string.notification_message), BASIC_NOTIFICATION_ID);
    }

    @OnClick(R.id.custom_notification_button)
    void sendCustomNotification() {
        displayBasicNotification(getString(R.string.custom_notification_title),
                notificationInput.getText().toString(), CUSTOM_NOTIFICATION_ID);
    }

    private void displayBasicNotification(String title, String message, int notificationId) {
        Intent intent = new Intent(this, NotificationConfirmationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action watchAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addAction(watchAction))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {250, 250})
                .build();

        NotificationManagerCompat notifMan = NotificationManagerCompat.from(this);
        notifMan.notify(notificationId, notification);
    }

    @OnClick(R.id.voice_reply_notification_button)
    void sendVoiceReplyNotification() {
        Intent intent = new Intent(this, NotificationConfirmationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(getString(R.string.voice_reply_instructions))
                .setChoices(getResources().getStringArray(R.array.notification_voice_options)).build();

        NotificationCompat.Action watchAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(getString(R.string.voice_reply_notification_message))
                .setContentTitle(getString(R.string.voice_reply_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addAction(watchAction))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {250, 250})
                .build();

        NotificationManagerCompat notifMan = NotificationManagerCompat.from(this);
        notifMan.notify(VOICE_REPLY_NOTIFICATION_ID, notification);
    }

    @OnClick(R.id.page_stack_notification_button)
    void sendPageStackNotifications() {
        Intent viewIntent1 = new Intent(this, NotificationConfirmationActivity.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, STACK_NOTIFICATION_ID + 1, viewIntent1, 0);

        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentText(String.format(getString(R.string.page_stack_notifications_message_formatted),
                        MAX_STACK_NOTIFICATION_COUNT))
                .setContentTitle(String.format(getString(R.string.page_stack_notifications_title_formatted),
                        MAX_STACK_NOTIFICATION_COUNT))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent1)
                .setVibrate(new long[] {250, 250})
                .setGroup(STACK_NOTIFICATIONS_GROUP)
                .setGroupSummary(true).build();

        NotificationCompat.Action watchAction1 = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.page_stack_notification_response_one), pendingIntent1)
                .build();

        Notification notification1 = new NotificationCompat.Builder(this)
                .extend(new NotificationCompat.WearableExtender().addAction(watchAction1))
                .setContentTitle(String.format(getString(R.string.page_stack_notification_random_title_formatted), 1))
                .setContentText(String.format(getString(R.string.page_stack_notification_random_message_formatted), 1))
                .setSmallIcon(R.mipmap.ic_launcher).setGroup(STACK_NOTIFICATIONS_GROUP).build();

        Intent viewIntent2 = new Intent(this, NotificationConfirmationActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, STACK_NOTIFICATION_ID + 2, viewIntent2, 0);

        NotificationCompat.Action watchAction2 = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.page_stack_notification_response_two), pendingIntent2)
                .build();

        Notification notification2 = new NotificationCompat.Builder(this)
                .extend(new NotificationCompat.WearableExtender().addAction(watchAction2))
                .setContentTitle(String.format(getString(R.string.page_stack_notification_random_title_formatted), 2))
                .setContentText(String.format(getString(R.string.page_stack_notification_random_message_formatted), 2))
                .setSmallIcon(R.mipmap.ic_launcher).setGroup(STACK_NOTIFICATIONS_GROUP).build();

        NotificationManagerCompat notifMan = NotificationManagerCompat.from(this);
        notifMan.notify(STACK_NOTIFICATION_ID, summaryNotification);
        notifMan.notify(STACK_NOTIFICATION_ID + 1, notification1);
        notifMan.notify(STACK_NOTIFICATION_ID + 2, notification2);
    }

}
