package rowley.androidwearlearning;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.bitmap;
import static android.R.attr.data;

public class DataTestingActivity extends Activity {

    private final String TAG = DataTestingActivity.class.getSimpleName();

    @BindView(R.id.received_message_text)
    TextView textView;
    @BindView(R.id.received_image_view)
    ImageView imageView;

    private GoogleApiClient googleApiClient;

    private Unbinder unbinder;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_testing);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                unbinder = ButterKnife.bind(DataTestingActivity.this, stub);
            }
        });

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
        if (googleApiClient != null) {
            Log.d(TAG, "already connected");
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "onConnected: " + bundle);
                        Wearable.MessageApi.addListener(googleApiClient, messageListener);
                        Wearable.DataApi.addListener(googleApiClient, dataListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: " + connectionResult);
                    }
                })
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
    }

    MessageApi.MessageListener messageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equalsIgnoreCase("/message")) {
                Log.i(TAG, new String(messageEvent.getData()));
                Observable.just(messageEvent)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(messageEventObserved -> textView.setText(new String(messageEventObserved.getData())),
                                throwable -> Log.e(TAG, throwable.getMessage(), throwable));
            }
        }
    };

    DataApi.DataListener dataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            for (DataEvent dataEvent : dataEventBuffer) {
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED
                        && dataEvent.getDataItem().getUri().getPath().equals("/image")) {
                    DataMapItem item = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    Observable.just(item.getDataMap().getAsset("androidImage"))
                            .map(asset -> Wearable.DataApi.getFdForAsset(googleApiClient, asset).await().getInputStream())
                            .map(BitmapFactory::decodeStream)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bitmap -> {
                                        Log.d(TAG, "Image Received");
                                        imageView.setImageBitmap(bitmap);
                                    },
                                    throwable -> Log.e(TAG, throwable.getMessage(), throwable)
                            );
                }
            }
        }
    };
}
