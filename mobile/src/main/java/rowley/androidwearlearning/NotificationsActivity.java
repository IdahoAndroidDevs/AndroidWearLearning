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

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsActivity extends AppCompatActivity {

    final int NOTIFICATION_ID = 001;

    private Unbinder unbinder;

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
        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        displayBasicNotification(pendingIntent);
    }

    private void displayBasicNotification(PendingIntent pendingIntent) {
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, getString(R.string.notification_title), pendingIntent).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(getString(R.string.notification_message))
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .build();

        NotificationManagerCompat notifMan = NotificationManagerCompat.from(this);
        notifMan.notify(NOTIFICATION_ID, notification);
    }

}
