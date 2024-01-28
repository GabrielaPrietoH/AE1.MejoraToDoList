package com.example.todolist;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {

    Button botonLogin;
    TextView botonRegistro;

    private FirebaseAuth mAuth;

    EditText emailText, passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        findViewById(R.id.botonLogin).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaPassword);

        botonLogin = findViewById(R.id.botonLogin);

        //3. Lo ponemos a la escucha con un listener. De param creamos un objeto de la interface
        //y sobreescribimos si método (Override)
        botonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //4. AL hacer click, pasamos a la MainActivity. Especofocamos que va
                //desde el objeto this de esta clase y pasa a la Main, mediante los 2 param
                //**FIREBASE AUTHENTICATOR-Login
                //Antes creo las variables para traer el contenido de las cajas. Según las variables de el código de Firebase.
                String email = emailText.getText().toString();
                String password = passText.getText().toString();
                if(email.isEmpty()){
                    emailText.setError("campo obligatorio");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailText.setError("campo incorrecto");
                }
                else if(password.length() < 6) {
                    passText.setError("minimo 6 caracteres");
                }
                else{
                    // Si se han hecho todas las validaciones
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Un toast para avisar que las credenciales son incorrectas
                                        Intent intent = new Intent(login.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(login.this , "Authentication failed.",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }

            }
        });


        botonRegistro = findViewById(R.id.botonRegistro);
        botonRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // CREAR USUARIO EN FIREBASE

                String email = emailText.getText().toString();
                String password = passText.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information


                                    Intent intent = new Intent(login.this, MainActivity.class);

                                    /*
                                    Toast Comunicación entre activities
                                    No ha sido necesario la implementación del Interface ni sobreescribir el método
                                    Ya que tenemos el método onComplete.
                                    */
                                    EditText nombreUsuario = findViewById(R.id.cajaCorreo);
                                    String cadena = nombreUsuario.getText().toString();
                                    String[] partes = cadena.split("@");
                                    String nombre = partes[0];
                                    intent.putExtra("nombre", nombre);


                                    startActivity(intent);

                                } else {
                                    //Un toast para avisar que las credenciales son incorrectas
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(login.this , "Authentication failed.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                botonRegistro.setPaintFlags(botonRegistro.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            }
        });

    }

        @Override
        public void onClick (View v){
            Intent intent = new Intent(this, MainActivity.class);
            EditText nombreUsuario = findViewById(R.id.cajaCorreo);
            intent.putExtra("Nombre", nombreUsuario.getText().toString());
            startActivity(intent);
        }

}

