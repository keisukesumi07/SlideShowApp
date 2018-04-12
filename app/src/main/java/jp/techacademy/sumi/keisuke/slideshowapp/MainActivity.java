package jp.techacademy.sumi.keisuke.slideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Cursor cursor;
    Handler mHandler;
    Timer mTimer;
    Boolean play=true;

    Button mStartButton,nextButton,prevButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        findViewById(R.id.prev_button).setOnClickListener(prevbuttonClickListener);
        findViewById(R.id.next_button).setOnClickListener(nextbuttonClickListener);
        findViewById(R.id.start_button).setOnClickListener(startbuttonClickListener);

    }


    View.OnClickListener prevbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // ここに処理
                    if (cursor.moveToPrevious()) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(imageUri);
                    }else{
                        cursor.moveToLast();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(imageUri);
                    }
                }
            });
        }
    };

    View.OnClickListener nextbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // ここに処理
                    if (cursor.moveToNext()) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(imageUri);
                    }else{
                        cursor.moveToFirst();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(imageUri);
                    }
                }
            });

        }
    };

    View.OnClickListener startbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mStartButton = (Button) findViewById(R.id.start_button);
            nextButton = (Button) findViewById(R.id.next_button);
            prevButton = (Button) findViewById(R.id.prev_button);
            if(play){
                mStartButton.setText("停止");
                nextButton.setEnabled(false);
                prevButton.setEnabled(false);
                play=false;
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler = new Handler(Looper.getMainLooper());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (cursor.moveToNext()) {
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                                        imageVIew.setImageURI(imageUri);
                                    }else{
                                        cursor.moveToFirst();
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                                        imageVIew.setImageURI(imageUri);
                                    }
                                }
                            });
                        }
                    }, 2000, 2000);
                }
            }else{
                mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mStartButton.setText("開始");
                        nextButton.setEnabled(true);
                        prevButton.setEnabled(true);
                        play=true;
                    }
                });
                mTimer.cancel();
                mTimer=null;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
