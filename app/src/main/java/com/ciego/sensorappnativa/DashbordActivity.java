package com.ciego.sensorappnativa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ciego.sensorappnativa.data.UserData;
import com.ciego.sensorappnativa.fragments.HistorialFragment;
import com.ciego.sensorappnativa.fragments.NuevosFragment;
import com.ciego.sensorappnativa.fragments.PerfilFragment;
import com.ciego.sensorappnativa.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashbordActivity extends ActionBarActivity {

    //Controles
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private TableRow btnPerfil;
    private TableRow btnHistorial;
    private TableRow btnNuevos;
    private TableRow btnCerrarSesion;

    //Cuadro de progreso
    ProgressDialog progressDialog;

    //URL de webService
    String url = "http://sensorsafe.com.mx/phonegap/queryData.php";
    RequestQueue query;

    //Variables auxiliares para Navegador Cajón
    private String currentTitle;
    private boolean isMenuOpened;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashbord);

        //Inicialización del objeto volley
        query = Volley.newRequestQueue(this);

        //Menu cerrado de inicio
        isMenuOpened = false;
        //Se inicializa navegación de cajón y todos los controles
        initSlidignMenu();
        initViews();
        //Mostramos algo en pantalla por primera vez
        showScreen(R.id.menuOpcion1);
	}

    //Validamos si se reciben o no extras para mostrar dialogo
    public void existExtras(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String mensaje = bundle.getString("mensaje");
            String titulo = bundle.getString("titulo");
            Log.d("GCM NOTIFICATION: ","Mensaje: "+mensaje+" - Título: "+titulo);
            Toast.makeText(this, "Mensaje: "+mensaje+" - Título: "+titulo, Toast.LENGTH_LONG).show();
        }
    }

    //Inicialización de Navegación de cajón
    private void initSlidignMenu() {
        final ActionBar actionBar = getSupportActionBar();

        currentTitle = getString(R.string.perfil);
        actionBar.setTitle(currentTitle);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //Activar la navegación
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.open, R.string.close){
            //Evento que sucede cuando  se cierra el menu
            @Override
            public void onDrawerClosed(View drawerView){
                isMenuOpened = false;
                actionBar.setTitle(currentTitle);
                super.onDrawerClosed(drawerView);
            }

            //Evento que sucede cuando se abre el menú
            @Override
            public void onDrawerOpened(View drawerView){
                isMenuOpened = true;
                actionBar.setTitle(R.string.drawerClosed);
                //Tiene que volver a construir las opciones en el menú
                supportInvalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    //Se inicializan los objetos de la clase MenuListener
    private void initViews(){
        btnPerfil = (TableRow)findViewById(R.id.menuOpcion1);
        btnHistorial = (TableRow)findViewById(R.id.menuOpcion2);
        btnNuevos = (TableRow)findViewById(R.id.menuOpcion3);
        btnCerrarSesion = (TableRow)findViewById(R.id.menuOpcion4);

        progressDialog = new ProgressDialog(this);

        MenuListener menuLister = new MenuListener();

        btnPerfil.setOnClickListener(menuLister);
        btnHistorial.setOnClickListener(menuLister);
        btnNuevos.setOnClickListener(menuLister);
        btnCerrarSesion.setOnClickListener(menuLister);
    }

    //Muestra en pantalla el fragmento correspondiente a la opción seleccionada
    private void showScreen(int screen){
        Fragment fragment = null;

        if(screen == R.id.menuOpcion4){
            launchConfirmationDialog().show();
        }else{
            switch (screen){
                case R.id.menuOpcion1:
                    fragment = new PerfilFragment();
                    currentTitle = getString(R.string.perfil);
                    break;
                case R.id.menuOpcion2:
                    fragment = new HistorialFragment();
                    currentTitle = getString(R.string.historial);
                    break;
                case R.id.menuOpcion3:
                    fragment = new NuevosFragment();
                    currentTitle = getString(R.string.nuevos);
                    break;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentFrame, fragment).commit();
        }

        drawerLayout.closeDrawers();
    }

    //Lanza dialogo de confirmación para cierre de sesión
    private AlertDialog launchConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar su sesión?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeSession();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    //Cierrar la sesión desde el servidor
    public void closeSession(){
        // Etiqueta utilizada para cancelar la petición
        String  tag_string_req = "string_req";

        progressDialog.setTitle("Aviso");
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    public void onResponse(String response) {
                        Log.d("TAG", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int ERROR = jsonObject.getInt("ERROR");
                            String MESSAGE = jsonObject.getString("MESSAGE");
                            if(ERROR == 0){
                                Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_LONG).show();
                                UserData.deleteLocalUserData(getApplicationContext());
                                launchMainActivity();
                            }else{
                                Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                        progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                //Obtenemos datos locales de usuario
                int userId = UserData.getUserId(DashbordActivity.this);
                String regId = UserData.getRegId(DashbordActivity.this);
                Log.d("PARAMETROS:","UserId:"+userId+" - RegId:"+regId);
                Map<String, String> params = new HashMap<String, String>();
                params.put("sSQL", "USP_SENSORAPP_DELETE_MOBILE_SESSION("+userId+",'"+regId+"')");
                return params;

            }

        };

        queue.add(putRequest);

    }

    //Lanza MainActivity
    public void launchMainActivity(){
        Intent intent = new Intent(DashbordActivity.this, MainActivity.class);
        startActivity(intent);
        DashbordActivity.this.finish();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashbord, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

    //Para sincronizar la acción cuando el dispositivo está en Landscpae o Portrait
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private class MenuListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            showScreen(v.getId());
        }
    }
}
