package com.example.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore miBaseDatos;

    private String idUser;

    ListView listViewTareas;
    ArrayAdapter<String> mAdapterTareas;
    List<String> listaTareas = new ArrayList<>();
    List<String> listaIDTareas = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        idUser = mAuth.getCurrentUser().getUid();

        miBaseDatos = FirebaseFirestore.getInstance();

        listViewTareas = findViewById(R.id.listTareas);

        actualizarUI();

        //Comunicación entre activities (meter en método)
        String nombre = getIntent().getStringExtra("nombre");
        //TextView etiquetaNomUser = findViewById(R.id.cajaCorreo);
        if (nombre != null && !nombre.isEmpty()) {
            Toast.makeText(MainActivity.this, "Hola " + nombre, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.mas){
            //activar el cuadro de diálogo para añadir tarea

            final EditText taskEditText = new EditText(this);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Nuevo Libro")
                    .setMessage("¿Cómo se llama el Libro?")
                    .setView(taskEditText)
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //AÑADIR TAREA A LA BASE DE DATOS Y AL LISTVIEW

                            String tarea = taskEditText.getText().toString();
                            if(tarea.isEmpty()){
                                Toast.makeText(MainActivity.this, "Campo vacio", Toast.LENGTH_SHORT).show();
                                return;
                            }else {

                                Map<String, Object> data = new HashMap<>();
                                data.put("nombreTarea", tarea);
                                data.put("idUsuario", idUser);

                                miBaseDatos.collection("Tareas")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(MainActivity.this, "Tarea añadida", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "Fallo al crear la tarea", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .setNegativeButton("Cancelar",null)
                    .create();
            dialog.show();
            return true;

        }else if(item.getItemId() == R.id.logout){
            //cierre de sesión en Firebase

            mAuth.signOut();

            startActivity(new Intent(MainActivity.this, login.class));
            return true;

        }else return super.onOptionsItemSelected(item);

    }

    private void actualizarUI() {
        miBaseDatos.collection("Tareas")
                .whereEqualTo("idUsuario", idUser)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if(e != null){

                            return;
                        }

                        listaIDTareas.clear();
                        listaTareas.clear();


                        for(QueryDocumentSnapshot doc :  value) {
                            listaTareas.add(doc.getString("nombreTarea"));
                            listaIDTareas.add(doc.getId());
                        }

                        if(listaTareas.size() == 0) {
                            listViewTareas.setAdapter(null);
                        }else{
                            mAdapterTareas = new ArrayAdapter<>(MainActivity.this, R.layout.item_tarea,R.id.textViewTarea,listaTareas);
                            listViewTareas.setAdapter(mAdapterTareas);
                        }
                    }

                });
    }

    //BORRAR TAREA + Añadir en XML android:onClick="borrarTarea"
    public void borrarTarea(View view){

        //El view es el botón clickeado
        View parent = (View) view.getParent(); //del view=botón obtenemos el padre
        //Mediante el padre consigo el hijo, es decir el TextView= textViewTarea
        // Ella lo llama "nombreTarea" cuidado por si da error
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        //variable para mostrar el contenido de la caja.
        String tarea = tareaTextView.getText().toString();

        //AHora mediante los dos ArrayList paralelos que creamos antes, mediante
        //la posición de listaTareas, obtendremos la posición de su Id en el
        //otro Array. SOn paralelos=misma posición. Le pasamos la tarea=posiicón
        int posicion = listaTareas.indexOf(tarea);

        //Borrado de datos en la BBDD
        //mediante la BBDD accedemos a la colección y al documentos, mediante
        //el identificador de tareas, en la posición que será la misma que hemos
        //guardado antes para el array parelelo de listaTareas.
        miBaseDatos.collection("Tareas").document(listaIDTareas.get(posicion)).delete();
        Toast.makeText(MainActivity.this,"Tarea eliminada correctamente", Toast.LENGTH_SHORT).show();

        //Añadir en el botón-XML el código: android:onClick="borrarTarea"

    }


    //ACTUALIZAR TAREA
    public void updateTarea(View view){
        //Hay que meterle un dialog, pero en lugar de estar vacío debería
        //tener el texto a editar.
        //AlertDialog dialog = new AlertDialog.Builder(this)
        //.set() actualiza todo el documento y .update() solo algunos campos.

        View parent = (View) view.getParent();

        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);

        String tarea = tareaTextView.getText().toString();


        int posicion = listaTareas.indexOf(tarea);

        //Alert para el cuadro de diálogo donde se modifican los datos

        final EditText taskEditText = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edita el Libro")
                .setMessage("¿Cómo quieres modificar el libro?")
                .setMessage(tarea)
                .setView(taskEditText)
                //los dos botones, el positive y el negative + listeners. Sobreescribimos el métood de positive.
                .setPositiveButton("modificar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Creo primero las variables. Las obtengo de la view del cuadro de dialogo creada más arriba.
                        String tarea = taskEditText.getText().toString();
                        if (tarea.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Campo no modificado", Toast.LENGTH_SHORT).show();
                            return;
                        } else {

                            Map<String, Object> data = new HashMap<>();
                            data.put("nombreTarea", tarea);
                            data.put("idUsuario", idUser);

                            miBaseDatos.collection("Tareas").document(listaIDTareas.get(posicion))
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast trasladado aquí
                                            Toast.makeText(MainActivity.this, "Tarea modificada correctamente", Toast.LENGTH_SHORT).show();

                                            return;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast
                                            Toast.makeText(MainActivity.this, "Fallo al modificar la tarea", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("cancelar", null)
                .create();
        dialog.show();

        //Añadir en el botón-XML el código: android:onClick="updateTarea
    }
}