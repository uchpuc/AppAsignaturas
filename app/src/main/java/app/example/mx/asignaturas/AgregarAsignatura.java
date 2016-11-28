package app.example.mx.asignaturas;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class AgregarAsignatura extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private long asignaturaId;
    private EditText asignaturaEditText;
    private EditText profesorEditText;
    private EditText semestreEditText;
    private EditText horasEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_asignatura);

        asignaturaEditText = (EditText) findViewById(R.id.asignatura_edit_text);
        profesorEditText = (EditText) findViewById(R.id.profesor_edit_text);
        semestreEditText=(EditText) findViewById(R.id.semestre_edit_text);
        horasEditText=(EditText) findViewById(R.id.horas_edit_text);

        asignaturaId = getIntent().getLongExtra(DetalleAsignatura.EXTRA_ASIGNATURA_ID, -1L);

        if (asignaturaId != -1L){
            getSupportLoaderManager().initLoader(0, null, this);
        }

        findViewById(R.id.boton_agregar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String nombreAsignatura = asignaturaEditText.getText().toString();
        String profesorAsignatura = profesorEditText.getText().toString();
        String semestreAsignatura=semestreEditText.getText().toString();
        String horasAsignatura=horasEditText.getText().toString();

        new CreateAsignaturaTask(this, nombreAsignatura, profesorAsignatura, semestreAsignatura, horasAsignatura, asignaturaId).execute();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsignaturaLoader(this, asignaturaId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && data.moveToFirst()){

            int asignaturaIndex = data.getColumnIndexOrThrow(AsignaturasDatabase.COL_ASIGNATURA);
            String nombreAsignatura = data.getString(asignaturaIndex);

            int profesorIndex = data.getColumnIndexOrThrow(AsignaturasDatabase.COL_PROFESOR);
            String nombreProfesor = data.getString(profesorIndex);

            int semestreIndex = data.getColumnIndexOrThrow(AsignaturasDatabase.COL_SEMESTRE);
            String nombreSemestre = data.getString(semestreIndex);

            int horasIndex = data.getColumnIndexOrThrow(AsignaturasDatabase.COL_HORAS);
            String numeroHoras = data.getString(horasIndex);

            asignaturaEditText.setText(nombreAsignatura);
            profesorEditText.setText(nombreProfesor); //CAAAMBIARRR!!
            semestreEditText.setText(nombreSemestre);
            horasEditText.setText(numeroHoras);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public static class CreateAsignaturaTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<Activity> weakActivity;
        private String nombreAsignatura;
        private String nombreProfesor;
        private String nombreSemestre;
        private String numeroHoras;
        private long asignaturaId;


       /* public CreateAnimalTask(Activity activity, String name, String desc){
            weakActivity = new WeakReference<Activity>(activity);
            animalName = name;
            animalDesc = desc;
            animalId = -1L;
        }*/

        public CreateAsignaturaTask(Activity activity, String asig, String prof, String sem, String hor, long asignaturaId){
            weakActivity = new WeakReference<Activity>(activity);
            nombreAsignatura = asig;
            nombreProfesor = prof;
            nombreSemestre=sem;
            numeroHoras=hor;
            this.asignaturaId = asignaturaId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = weakActivity.get();
            if (context == null){
                return false;
            }

            Context appContext = context.getApplicationContext();

            Boolean success = false;

            if (asignaturaId != -1L){
                int filasAfectadas = AsignaturasDatabase.actualizaAsignatura(appContext, nombreAsignatura, nombreProfesor, nombreSemestre, numeroHoras, asignaturaId);
                success = (filasAfectadas != 0);
            } else {
                long id = AsignaturasDatabase.insertaAsignatura(appContext, nombreAsignatura, nombreProfesor, nombreSemestre, numeroHoras);
                success = (id != -1L);
            }
            return  success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Activity context = weakActivity.get();
            if(context == null){
                return;
            }
            if (aBoolean){
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
            }
            context.finish();
        }
    }
}
