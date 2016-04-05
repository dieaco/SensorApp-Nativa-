package com.ciego.sensorappnativa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciego.sensorappnativa.data.UserData;
import com.ciego.sensorappnativa.parsers.JSONParser;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	//Controles del layout
	private EditText etEmail;
	private EditText etPassword;
	private EditText etRegId;
	private CheckBox cbRecordar;
	private Button btnEntrar;
	//Intent para lanzar dashboard
	Intent intent;
	//GCM registro
	GoogleCloudMessaging gcm;
	String regId;
	String PROJECT_NUMBER = "1041241833422";
    //URL de webService
    String url = "http://sensorsafe.com.mx/phonegap/";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        
        initViews();
    }
    
    //Inicializaci�n de componentes
    public void initViews(){
    	etEmail = (EditText)findViewById(R.id.etEmail);
    	etPassword = (EditText)findViewById(R.id.etPassword);
    	etRegId = (EditText)findViewById(R.id.etRegId);
    	cbRecordar = (CheckBox)findViewById(R.id.cbRecordar);
    	btnEntrar = (Button)findViewById(R.id.btnEntrar);
    	
    	//Inhabilitar campo de text Registration Id
    	etRegId.setKeyListener(null);
    	
    	//Solicitar Registration Id
    	getRegId();
    	
    	//Evento click del bot�n iniciar sesi�n
    	btnEntrar.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startSession();
			}
		});
    }
    
    public void startSession(){
    	if(etEmail.getText().toString().equals("")){
    		etEmail.setError(getString(R.string.error_campo_obligatorio));
    	}else if(etPassword.getText().toString().equals("")){
    		etPassword.setError(getString(R.string.error_campo_obligatorio));
    	}else if(etRegId.getText().toString().equals("")){
    		etRegId.setError(getString(R.string.error_regId));
    	}else if(!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+")){
    		etEmail.setError(getString(R.string.error_correo));
    	}else{
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            new validateLogin(email, password).execute();
    	}
    }
    
    //Registra dispositivo en Google Cloud Messaging
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @SuppressWarnings("deprecation")
			@Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(PROJECT_NUMBER);
                    msg = "" + regId;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }

    //Proceso de validación de login asíncrono
    private class validateLogin extends AsyncTask<Void, Void, Boolean>{

        //JSONParser
        JSONParser jsonParser = new JSONParser();
        //Parámetros de clase
        String etEmailText = "";
        String etPasswordText = "";
        String regId = MainActivity.this.etRegId.getText().toString();
        //Dialogo de arranque
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //Sentencia sSQL
        String sSQL = "";
        //Variables extraídas del servidor JSON
        int userId;
        int ERROR;

        public validateLogin(String email, String password) {
            validateLogin.this.etEmailText = email;
            validateLogin.this.etPasswordText = password;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.setMessage("Conectando...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);

            this.sSQL = "USP_SENSORSAPP_VALIDATE_LOGIN('"+this.etEmailText+"', '"+this.etPasswordText+"')";

            parameters.add(new BasicNameValuePair("sSQL", this.sSQL));

            JSONArray jsonArray = jsonParser.makeHttpRequestWithParams(url + "queryData.php", parameters);

            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(0);
                this.ERROR = jsonObject.getInt("ERROR");
                if(this.ERROR == 0){
                    this.userId = jsonObject.getInt("idEmpleado");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if(result == true){
                if(this.ERROR == 0){
                    Log.d("REG_ID"," - "+this.regId);
                    new setRegistrationIdOnServer(this.userId, this.regId).execute();
                    //Sí el checkbox está checado se almcenan las preferencias de usuario
                    if(MainActivity.this.cbRecordar.isChecked())
                        UserData.setLocalUserData(getApplicationContext(), this.userId, true, this.regId);
                    else
                        UserData.setLocalRegId(getApplicationContext(), this.userId, this.regId);
                }else if(this.ERROR == 1){
                    Toast.makeText(getBaseContext(), "La contraseña es incorrecta", Toast.LENGTH_LONG).show();
                    MainActivity.this.etPassword.requestFocus();
                }else if(this.ERROR == 2){
                    Toast.makeText(getBaseContext(), "El usuario no existe", Toast.LENGTH_LONG).show();
                    MainActivity.this.etEmail.requestFocus();
                }
            }else{
                Toast.makeText(getBaseContext(), "Ocurrió algún error con la conexión", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class setRegistrationIdOnServer extends AsyncTask<Void, Void, Boolean> {

        //JSONParser
        JSONParser jsonParser = new JSONParser();
        //Propiedades de clase
        int userId;
        String regId;
        //Dialogo de arranque
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //Sentencia SQL
        String sSQL = "";
        //Variables de servidor
        int ERROR;

        public setRegistrationIdOnServer(int userId, String regId) {
            setRegistrationIdOnServer.this.userId = userId;
            setRegistrationIdOnServer.this.regId = regId;
        }

        @Override
        protected  void onPreExecute(){
            progressDialog.setMessage("Conectando...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);

            this.sSQL = "USP_SENSORAPP_SET_MOBILE_SESSION("+this.userId+", '"+this.regId+"')";

            parameters.add(new BasicNameValuePair("sSQL", this.sSQL));

            Log.d("URL", "- - - " + url + "queryData.php - " + parameters);
            JSONArray jsonArray = jsonParser.makeHttpRequestWithParams(url +"queryData.php", parameters);
            Log.d("JSONARRAY"," - "+jsonArray);
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(0);
                this.ERROR = jsonObject.getInt("ERROR");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if(result == true){
                intent = new Intent(getApplicationContext(), DashbordActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                Log.d("FINAL", "Se termino de realizar"+result);
            }else{
                Log.d("FINAL", "NO Se termino de realizar"+result);
            }
        }
    }

}
