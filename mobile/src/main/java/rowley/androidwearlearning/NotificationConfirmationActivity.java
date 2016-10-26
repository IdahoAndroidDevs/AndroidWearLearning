package rowley.androidwearlearning;

import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class NotificationConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_confirmation);
        NotificationManagerCompat.from(this).cancelAll();

        checkForVoiceResponse();
    }

    private void checkForVoiceResponse() {
        CharSequence response = getMessageText();

        if (!TextUtils.isEmpty(response)) {
            ((TextView) findViewById(R.id.confirmation_text_view))
                    .setText(String.format(getString(R.string.voice_reply_response_formatted), response));
        }
    }

    private String getMessageText() {
        String result = "";
        Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
        if (remoteInput != null) {
            result = remoteInput.getCharSequence(NotificationsActivity.EXTRA_VOICE_REPLY).toString();
        }

        return result;
    }
}
