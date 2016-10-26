package rowley.androidwearlearning;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsActivity extends AppCompatActivity {

    final int BASIC_NOTIFICATION_ID = 001;
    final int CUSTOM_NOTIFICATION_ID = 002;

    private Unbinder unbinder;

//    @BindView(R.id.notification_input)
    EditText notificationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unbinder = ButterKnife.bind(this);

        findViewById(R.id.basic_notification_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBasicNotification();
            }
        });

        findViewById(R.id.custom_notification_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCustomNotification();
            }
        });

        notificationInput = (EditText) findViewById(R.id.notification_input);

        NotificationManagerCompat.from(this).cancelAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

//    @OnClick(R.id.basic_notification_button)
    void sendBasicNotification() {
        displayBasicNotification(getString(R.string.notification_title),
                getString(R.string.notification_message), BASIC_NOTIFICATION_ID);
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

//    @OnClick(R.id.custom_notification_button)
    void sendCustomNotification() {
        displayBasicNotification(getString(R.string.custom_notification_title),
                notificationInput.getText().toString(), CUSTOM_NOTIFICATION_ID);
    }

}
