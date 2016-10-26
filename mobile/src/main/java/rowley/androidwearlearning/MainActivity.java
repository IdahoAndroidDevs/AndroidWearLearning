package rowley.androidwearlearning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        Log.d("JAR", "Unbinder is not null? " + (unbinder != null));

        findViewById(R.id.notifications_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNotificationsActivity();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

//    @OnClick(R.id.notifications_button)
    void goToNotificationsActivity() {
        Log.d("JAR", "Clicked on the Notifications Test button");
        startActivity(new Intent(this, NotificationsActivity.class));
    }
}
