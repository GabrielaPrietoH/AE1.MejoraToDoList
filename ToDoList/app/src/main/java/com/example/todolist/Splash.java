package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    // Establece la duración de la pantalla de inicio
    public ProgressBar splash_screenProgressBar;
    public int MAX_VALUE = 30;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.cinematic_dark);
        mediaPlayer.start();

                // Establece la orientación vertical
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setContentView(R.layout.activity_splash);

                TextView txt = (TextView) findViewById(R.id.textView1);
                Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim1);
                txt.startAnimation(aniSlide);

                splash_screenProgressBar = (ProgressBar) findViewById(R.id.progressBar);
                splash_screenProgressBar.setMax(MAX_VALUE);

                // Agrega el código para actualizar la barra de progreso aquí
                new CountDownTimer(7000, 100) {
                    int progreso = 1;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        splash_screenProgressBar.setProgress(progreso);
                        progreso += (1);
                    }

                    @Override
                    public void onFinish() {
                        splash_screenProgressBar.setProgress(MAX_VALUE);

                        // Inicia la siguiente actividad
                        Intent mainIntent = new Intent().setClass(Splash.this, login.class);
                        startActivity(mainIntent);

                        // Cierra la actividad para que el usuario no pueda volver a esta actividad presionando el botón Atrás

                        finish();
                    }

                }.start();

            }

        }