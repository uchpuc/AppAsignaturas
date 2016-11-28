package app.example.mx.asignaturas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class DetalleAsignatura extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView asignaturaTextView;
    private TextView profesorTextView;
    private TextView semestreTextView;
    private TextView horasTextView;

    private long asignaturaId;
    public static final String EXTRA_ASIGNATURA_ID = "asignatura.id.extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_asignatura);

        asignaturaTextView = (TextView) findViewById(R.id.asignatura_text_view);
        profesorTextView = (TextView) findViewById(R.id.profesor_text_view);
        semestreTextView=(TextView) findViewById(R.id.semestre_text_view);
        horasTextView=(TextView) findViewById(R.id.horas_text_view);


        Intent intencion = getIntent();
        asignaturaId = intencion.getLongExtra(MainActivity.EXTRA_ID_ASIGNATURA, -1L);

        getSupportLoaderManager().initLoader(0, null, this);

        findViewById(R.id.boton_eliminar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new DeleteAsignaturaTask(DetalleAsignatura.this, asignaturaId).execute();
                    }
                }
        );

        findViewById(R.id.boton_actualizar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(DetalleAsignatura.this, AgregarAsignatura.class);
                        intent.putExtra(EXTRA_ASIGNATURA_ID, asignaturaId);
                        startActivity(intent);

                    }
                }
        );

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

            asignaturaTextView.setText(nombreAsignatura);
            profesorTextView.setText(nombreProfesor);
            semestreTextView.setText(nombreSemestre);
            horasTextView.setText(numeroHoras);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class DeleteAsignaturaTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<Activity> weakActivity;
        private long asignaturaId;

        public DeleteAsignaturaTask(Activity activity, long id){
            weakActivity = new WeakReference<Activity>(activity);
            asignaturaId = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = weakActivity.get();
            if (context == null){
                return false;
            }

            Context appContext = context.getApplicationContext();

            int filasAfectadas = AsignaturasDatabase.eliminaConId(appContext, asignaturaId);
            return  (filasAfectadas != 0);
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
