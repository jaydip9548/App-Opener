package com.example.assistance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
//Hello Developers ! How are you.

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ArrayList<String> apps;

    private  String pkg = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

       new Thread(new Runnable() {
           @Override
           public void run() {
               if (ContextCompat.checkSelfPermission(MainActivity.this,
                       Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                   ActivityCompat.requestPermissions(MainActivity.this,
                           new String[]{Manifest.permission.RECORD_AUDIO}, 1);
               }
           }
       }).start();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        }
                    }
                }).start();



//                start speech recognizer
                imageView.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_24));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.
                                LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                        try {
                            startActivityForResult(intent, 101);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Your Device doesn't support", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_off_24));
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (text != null) {
                Intent mainIntent = new Intent(Intent.ACTION_MAIN);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> pkgApp = this.getPackageManager().
                        queryIntentActivities(mainIntent, 0);

//                For getting Package name

                pkg = getAppAvailability(pkgApp, text.get(0));
                if (pkg != "") {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);

                    startActivity(intent);

                } else {
                    Toast.makeText(this, "" + text.toString() + " is not available in phone and make sure spelling must be correct",
                            Toast.LENGTH_LONG).show();
                }
                text = null;

            } else {
                Toast.makeText(this, "Speak your app name ", Toast.LENGTH_SHORT).show();
            }
        } else {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_off_24));
            Toast.makeText(this, "Speak your app name", Toast.LENGTH_SHORT).show();
        }
    }



    private String getAppAvailability(List<ResolveInfo> pkgApp, String s) {
        String apkPackage = "";

                boolean is = false;
                apps = new ArrayList<>();

                String s1[] = null;
                for (int i = 0; i < pkgApp.size(); i++) {
                    s1 = pkgApp.get(i).toString().split(" ", 2);
                    s1 = s1[1].split("/", 2);
                    apps.add(s1[0]);
                }
                String appName = "";
                String name = "";
                PackageManager packageManager = getApplicationContext().getPackageManager();
                for (int i = 0; i < apps.size(); i++) {
                    try {
//
                        appName = (String) packageManager.getApplicationLabel(
                                packageManager.getApplicationInfo(apps.get(i),
                                        PackageManager.GET_META_DATA));

                        if (s.equals(appName)) {
                            is = true;
                            apkPackage = apps.get(i);
                            break;
                        } else if (!is){
                            name = s;
                            name = name.toLowerCase();
                            name = name.replaceAll(" ", "");
                            if (apps.get(i).contains(name)) {
                                is = true;
                                apkPackage = apps.get(i);
                                break;
                            }

                            name = s;
                            name = name.toLowerCase();
                            name = name.replaceAll(" ","");
                            String app = appName.toLowerCase();
                            app = app.replaceAll(" ","");
                            if (name.equals(app)) {
                                is = true;
                                apkPackage = apps.get(i);
                                break;
                            }

                            name = s;
                            name = name.toUpperCase();

                            if (appName.equals(name)) {
                                is = true;
                                apkPackage = apps.get(i);
                                break;
                            }

                            name = s;
                            name = name.toLowerCase();
                            name = name.replaceAll(" ", "");

                            if (apps.get(i).contains(name)) {
                                is = true;
                                apkPackage = apps.get(i);
                                break;
                            }
                        }


                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

        return apkPackage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
//        if (requestCode == 2) {
//            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//            }//        }

    }
}