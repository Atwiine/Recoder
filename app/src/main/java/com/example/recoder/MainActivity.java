package com.example.recoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class MainActivity extends AppCompatActivity  {
    private Button startbtn, stopbtn, playbtn, stopplay;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private Button sampleButton;
    private FileObserver mFileObserver;
    private Vector<String> audioFileNames;

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;

    boolean isRecording = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        audioFileNames = new Vector<String>();
//        LinearLayout finalContainer = new LinearLayout(this);
//        sampleButton = new Button(this);
//        sampleButton.setOnClickListener(this);
//        sampleButton.setText("Start Audio Intent");
//        finalContainer.addView(sampleButton);implements View.OnClickListener
//        setContentView(finalContainer);
//        addObserver();



    buttonStart = (Button) findViewById(R.id.button);
    buttonStop = (Button) findViewById(R.id.button2);
    buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
    buttonStopPlayingRecording = (Button)findViewById(R.id.button4);

      buttonStop.setEnabled(false);
      buttonPlayLastRecordAudio.setEnabled(false);
      buttonStopPlayingRecording.setEnabled(false);

    random = new Random();

      buttonStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(checkPermission()) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    AudioSavePathInDevice= getApplicationContext().getExternalFilesDir
                            (Environment.DIRECTORY_DCIM) + "/" + "AudioRecording.3gp" + ".jpeg";
                }
                else
                {
                    AudioSavePathInDevice=Environment.getExternalStorageDirectory()
                            .toString() + "/" + "AudioRecording.3gp" + ".jpeg";
                }

                MediaRecorderReady();
                
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    isRecording = true;
                    Toast.makeText(MainActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Recording startedee " +e.toString(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Recording startedwe " +e.toString(),
                            Toast.LENGTH_LONG).show();
                }

                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);


            } else {
                requestPermission();
            }

        }
    });

      buttonStop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (null != mediaRecorder) {
//                try{
//                    mediaRecorder.stop();
//                }catch(RuntimeException ex){
//                    //Ignore
//                    Toast.makeText(MainActivity.this, "cant stop" +  ex.toString(), Toast.LENGTH_SHORT).show();
//                }
//
//            }else {
//                Toast.makeText(MainActivity.this, "cant stopsss", Toast.LENGTH_SHORT).show();
//
//            }


            if(isRecording){
                Toast.makeText(MainActivity.this, "ff " + isRecording, Toast.LENGTH_SHORT).show();

                mediaRecorder.stop();
            }else {
                Toast.makeText(MainActivity.this, "cant stopsss"+ isRecording, Toast.LENGTH_SHORT).show();
            }
            mediaRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
            mediaRecorder.release(); // Now the object cannot be reused
            isRecording = false;


//            mediaRecorder.stop();
            buttonStop.setEnabled(false);
            buttonPlayLastRecordAudio.setEnabled(true);
            buttonStart.setEnabled(true);
            buttonStopPlayingRecording.setEnabled(false);

            Toast.makeText(MainActivity.this, "Recording Completed",
                    Toast.LENGTH_LONG).show();
        }
    });

      buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) throws IllegalArgumentException,
                SecurityException, IllegalStateException {

            buttonStop.setEnabled(false);
            buttonStart.setEnabled(false);
            buttonStopPlayingRecording.setEnabled(true);

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(AudioSavePathInDevice);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            Toast.makeText(MainActivity.this, "Recording Playing",
                    Toast.LENGTH_LONG).show();
        }
    });

      buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonStop.setEnabled(false);
            buttonStart.setEnabled(true);
            buttonStopPlayingRecording.setEnabled(false);
            buttonPlayLastRecordAudio.setEnabled(true);

            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
        }
    });

}

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
//        System.out.println(AudioSavePathInDevice);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


//        startbtn = (Button)findViewById(R.id.btnRecord);
//        stopbtn = (Button)findViewById(R.id.btnStop);
//        playbtn = (Button)findViewById(R.id.btnPlay);
//        stopplay = (Button)findViewById(R.id.btnStopPlay);
//        stopbtn.setEnabled(false);
//        playbtn.setEnabled(false);
//        stopplay.setEnabled(false);
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/AudioRecording.3gp";
//
//        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(CheckPermissions()) {
//                    stopbtn.setEnabled(true);
//                    startbtn.setEnabled(false);
//                    playbtn.setEnabled(false);
//                    stopplay.setEnabled(false);
//                    mRecorder = new MediaRecorder();
//                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    mRecorder.setOutputFile(mFileName);
//                    try {
//                        mRecorder.prepare();
//                        mRecorder.start();
//                    } catch (IOException e) {
//                        Log.e(LOG_TAG, "prepare() failed");
//                    }
////
//                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    RequestPermissions();
//                }
//            }
//        });
//        stopbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopbtn.setEnabled(false);
//                startbtn.setEnabled(true);
//                playbtn.setEnabled(true);
//                stopplay.setEnabled(true);
//
//                assert mRecorder != null;
//                mRecorder.stop();
//                mRecorder.release();
//                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
//            }
//        });
//        playbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopbtn.setEnabled(false);
//                startbtn.setEnabled(true);
//                playbtn.setEnabled(false);
//                stopplay.setEnabled(true);
//                mPlayer = new MediaPlayer();
//                try {
//                    mPlayer.setDataSource(mFileName);
//                    mPlayer.prepare();
//                    mPlayer.start();
//                    Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "prepare() failed");
//                }
//            }
//        });
//        stopplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPlayer.release();
//                mPlayer = null;
//                stopbtn.setEnabled(false);
//                startbtn.setEnabled(true);
//                playbtn.setEnabled(true);
//                stopplay.setEnabled(false);
//                Toast.makeText(getApplicationContext(),"Playing Audio Stopped", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

/*
    private void addObserver() {
        this.mFileObserver = new FileObserver("/sdcard/Sounds/") {
            @Override
            public void onEvent(int event, String path) {
                if (event == FileObserver.CREATE) {
                    if (path != null) {
                        int index = path.indexOf("tmp");
                        String tempFileName = (String) path.subSequence(0,
                                index - 1);
                        audioFileNames.add(tempFileName);

                    }
                } else if (event == FileObserver.DELETE) {
                    if (path != null) {
                        int index = path.indexOf("tmp");
                        String tempFileName = (String) path.subSequence(0,
                                index - 1);
                        if (audioFileNames.contains(tempFileName)) {
                            audioFileNames.remove(tempFileName);
                        }
                    }

                }
            }
        };
    }

    private void readFile(String fileName) {

        File attachment = new File("/sdcard/Sounds/" + fileName);
        if (attachment.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(attachment);
                byte[] bytes = new byte[(int) attachment.length()];
                try {
                    fis.read(bytes);
                    fis.close();

                    attachment.delete();

                    saveMedia("Test" + fileName, bytes);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mFileObserver.startWatching();
    }

    public void saveMedia(String fileName, byte[] data) {

        String imagePath = "/sdcard/sam/";
        System.out.println("Inside Folder");


        File file = new File(imagePath, fileName);
        System.out.println("File Created");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(
                    fileOutputStream);
            System.out.println("Writting File");
            dataOutputStream.write(data, 0, data.length);
            System.out.println("Finished writting File");
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 2) {
            if (mFileObserver != null) {
                mFileObserver.stopWatching();
            }
            Enumeration<String> audioFileEnum = audioFileNames.elements();
            while (audioFileEnum.hasMoreElements()) {
                readFile((String) audioFileEnum.nextElement());
            }
        }
    }}*/



/*    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }*/
//}