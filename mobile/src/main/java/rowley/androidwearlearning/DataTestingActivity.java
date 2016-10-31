package rowley.androidwearlearning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataTestingActivity extends AppCompatActivity {

    private final String TAG = DataTestingActivity.class.getSimpleName();

    @BindView(R.id.send_message_input)
    EditText sendMessageInput;

    private Unbinder unbinder;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_testing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGoogleApiClient();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void initGoogleApiClient() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "Connected");
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "onConnected: " + bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
    }

    @OnClick(R.id.send_message_button)
    void sendMessage() {
        Observable.just(sendMessageInput.getText().toString())
                .map(messageText -> {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                    for (Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi
                                .sendMessage(googleApiClient, node.getId(), "/message", messageText.getBytes()).await();
                    }

                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> sendMessageInput.getText().clear(),
                        throwable -> Log.e(TAG, throwable.getMessage(), throwable));
    }

    @OnClick(R.id.send_image_button)
    void sendImage() {
        // TODO: 10/30/16
    }

}
