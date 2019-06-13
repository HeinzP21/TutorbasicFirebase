package com.example.tutorbasic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tutorbasic.Modelo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private EditText eNombre;
    private EditText eApellidos;
    private EditText eCorreo;
    private EditText eClave;
    private Button btnSave;
    private ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        eNombre = (EditText) findViewById(R.id.Nombre);
        eApellidos = (EditText) findViewById(R.id.Apellidos);
        eCorreo = (EditText) findViewById(R.id.Correo);
        eClave = (EditText) findViewById(R.id.Clave);

        btnSave= (Button) findViewById(R.id.buttonSave);

        progressDialog = new ProgressDialog(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_nombre = eNombre.getText().toString();
                String txt_apellido = eApellidos.getText().toString();
                String txt_contraseña = eClave.getText().toString();
                String txt_correo = eCorreo.getText().toString();

                if (TextUtils.isEmpty(txt_nombre) || TextUtils.isEmpty(txt_correo) || TextUtils.isEmpty(txt_apellido) || TextUtils.isEmpty(txt_contraseña )) {
                    Toast.makeText(Main2Activity.this, "ESTE CAMPO NO PUEDE ESTAR VACIO", Toast.LENGTH_SHORT).show();
                }else if (txt_contraseña.length()<6){
                    Toast.makeText(Main2Activity.this,"ERROR: LA CONTRASEÑA DEBE TENER MINIMO 6 CARACTERES",Toast.LENGTH_SHORT).show();
                }else {
                    register(txt_nombre,txt_correo, txt_contraseña, txt_apellido);
                }

            }
        });


    }

    private void register(final String nombre, final String correo, final String contraseña, final String apellido){

        auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("usuarios").child(userid);


                            User user = new User();
                            user.setId(userid);
                            user.setNombre(nombre);
                            user.setNombre(apellido);
                            user.setCorreo(correo);


                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Main2Activity.this,"tienes problemas con el correo o contrasena",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}