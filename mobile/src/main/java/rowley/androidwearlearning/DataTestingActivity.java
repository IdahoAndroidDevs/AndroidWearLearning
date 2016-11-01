package rowley.androidwearlearning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataTestingActivity extends AppCompatActivity {

    private final String TAG = DataTestingActivity.class.getSimpleName();
    private final String IMAGE_URL = "http://www.androidcentral.com/sites/androidcentral.com/" +
            "files/styles/w550h500/public/wallpapers/batdroid-blj.jpg";

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
                        Wearable.MessageApi.sendMessage(
                                googleApiClient, node.getId(), "/message", messageText.getBytes()).await();
                    }

                    return messageText;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageText -> sendMessageInput.getText().clear(),
                        throwable -> Log.e(TAG, throwable.getMessage(), throwable));
    }

    @OnClick(R.id.send_image_button)
    void sendImage() {
        Observable.just(IMAGE_URL)
                .map(imageUrl -> {
                    PutDataMapRequest request = PutDataMapRequest.create("/image");
                    DataMap map = request.getDataMap();
                    URL url = new URL(imageUrl);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);

                    Asset asset = Asset.createFromBytes(bos.toByteArray());
                    int randomInt = new Random().nextInt(1000);
                    map.putInt("integer", randomInt);
                    map.putAsset("androidImage", asset);
                    Wearable.DataApi.putDataItem(googleApiClient, request.asPutDataRequest());

                    bmp.recycle();

                    return request;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(request -> {},
                        throwable -> Log.e(TAG, throwable.getMessage(), throwable));
    }

}
