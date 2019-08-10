package com.wposs.buc.restpapp.activitys;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wposs.buc.restpapp.bd.controler.ClsConexion;
import com.wposs.buc.restpapp.R;
import com.wposs.buc.restpapp.model.Usuarios;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CrearUsuarioActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks {

    EditText etDocumento, etNombre, etApellido;
    RadioButton rbAdmin, rbCaja, rbMesero;
    ClsConexion bd;

    private Uri mFileUri;
    private static final int TC_PICK_IMAGE = 101;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_CAMERA_PERMISSIONS = 102;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference mProfileStorageReference;

    private static final String[] cameraPerms = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mProfileStorageReference = mStorage.getReference().child("profile");

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("Crear usuarios");
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        bd = new ClsConexion(this);

        etDocumento = findViewById(R.id.etDocumento);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbCaja = findViewById(R.id.rbCaja);
        rbMesero = findViewById(R.id.rbMesero);
    }

    public void cancelarCrearUsuario(View view) {
    }

    public void aceptarCrearUsuario(View view) {
        String sNombre = etNombre.getText().toString().trim();
        String sApellido = etApellido.getText().toString().trim();
        String sDocumento = etDocumento.getText().toString().trim();

        if (!sNombre.isEmpty()) {
            if (!sApellido.isEmpty()) {
                if (!sDocumento.isEmpty()) {
                    if (verificarRadio()) {
                        String user = obtenerUsuario(sNombre, sApellido);
                        if (verificarUsuario(user)) {
                            String pass = crearPassword(sDocumento);
                            String name = sNombre +
                                    " " +
                                    sApellido;
                            String role = obtenerRole();
                            String status = "new";
                            createUser(user, pass, name, role, status, sDocumento);
                        }

                    }
                } else {
                    Toast.makeText(this, "Campo de documento vacio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Campo de apellido vacio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Campo de nombre vacio", Toast.LENGTH_SHORT).show();
        }

    }

    private String crearPassword(String sDocumento) {
        int len = sDocumento.length();
        return sDocumento.substring(len - 6, len);
    }

    private boolean verificarUsuario(String user) {
        boolean ret = true;
        /**
         * Aqui agregar verificacion de usuarios para no crear usuarios repetidos.
         */
//        try {
//            ArrayList<String> usuarios = bd.getAllUsuariosUser();
//            for (int i = 0; i < usuarios.size(); i++) {
//                if (usuarios.get(i).equals(user)) {
//                    ret = false;
//                    Toast.makeText(this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }catch (Exception e){
//            Log.e("Error ", ""+e);
//        }
        return ret;
    }

    private boolean verificarIdUser(int id) {
        boolean ret = true;
        /*try {
            ArrayList<Integer> usuarios = bd.getAllUsuariosId();
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i) == id) {
                    ret = false;
                    Toast.makeText(this, "Este documento ya tiene usuario registrado", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Error ", "" + e);
        }*/
        return ret;
    }

    private boolean verificarRadio() {
        boolean ret = true;
        if (!rbAdmin.isChecked()) {
            if (!rbCaja.isChecked()) {
                if (!rbMesero.isChecked()) {
                    ret = false;
                    Toast.makeText(this, "No ha seleccionado ninguna opcion de rol", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return ret;
    }

    private String obtenerRole() {
        String ret = null;
        if (rbAdmin.isChecked())
            ret = rbAdmin.getText().toString();
        if (rbCaja.isChecked())
            ret = rbCaja.getText().toString();
        if (rbMesero.isChecked())
            ret = rbMesero.getText().toString();
        return ret;
    }

    private String obtenerUsuario(String sNombre, String sApellido) {
        String ret;
        String primeraLetraDelNombre = sNombre.substring(0, 1).toLowerCase();
        String apellido;
        if (!sApellido.contains(" ")) {
            apellido = sApellido.trim().toLowerCase();
        } else {
            apellido = sApellido.trim().substring(0, sApellido.indexOf(" ")).toLowerCase();
        }

        ret = primeraLetraDelNombre +
                apellido + "@restoapp.com.co";

        return ret;
    }

    public void agregarFotoUsuario(View view) {
        Toast.makeText(this, "Aun no es posible agregar foto de usuario", Toast.LENGTH_SHORT).show();
        showImagePicker();
    }

    private void createUser(final String email, final String password, final String name, final String role,
                            final String status, final String id) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                final StorageReference photoRef =
                        mProfileStorageReference.child(id + ".jpg");

                UploadTask uploadTask = photoRef.putFile(mFileUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            Log.i("The URL : ", downloadUrl.toString());
                            if (task.isSuccessful()) {
                                Usuarios usuarios = new Usuarios();
                                usuarios.setUser(email);
                                usuarios.setPass(password);
                                usuarios.setName(name);
                                usuarios.setRole(role);
                                usuarios.setStatus(status);
                                usuarios.setPhotoUrl(downloadUrl.toString());
                                usuarios.setId(id);
                                mFirestore.collection("usuarios").document(email)
                                        .set(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("Creacion de Cuenta", "Documento del usuario creado correctamente");
                                        } else {
                                            Log.e("Creacion de Cuenta", "Fallo creacion del documento del usuario ");
                                        }
                                    }
                                });
                                Log.d("Creacion de Cuenta", "Creada exitosamente con email " + email);
                                Toast.makeText(CrearUsuarioActivity.this, "Nuevo usuario creado con exito. " + email + " y contraseña" + password, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.d("Creacion de Cuenta", "No fue posible crear la cuenta " + task.getException().toString());
                            }
                        }
                    }
                });

            }
        });
    }


    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker() {
        // Check for camera permissions
        if (!EasyPermissions.hasPermissions(this, cameraPerms)) {
            EasyPermissions.requestPermissions(this,
                    "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        // Choose file storage location
        File file = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        mFileUri = Uri.fromFile(file);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new
                Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, TC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TC_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isCamera;
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                if (!isCamera) {
                    mFileUri = data.getData();
                }
                Log.d("DATA IMG", "Received file uri: " + mFileUri.getPath());



                /*photoRef.putFile(mFileUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> photoUpload = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        Log.d("DATA IMG", "Url de imagen: " + photoUpload.toString());
                        Toast.makeText(CrearUsuarioActivity.this, "Foto subida correctamente" + photoUpload.toString(), Toast.LENGTH_SHORT).show();
                    }
                });*/


//                mTaskFragment.resizeBitmap(mFileUri, THUMBNAIL_MAX_DIMENSION);
//                mTaskFragment.resizeBitmap(mFileUri, FULL_SIZE_MAX_DIMENSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
