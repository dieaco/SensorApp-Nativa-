package com.ciego.sensorappnativa.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ciego on 06/04/2015.
 */
public class UserData {

    //Consultar estado de sesión
    public static boolean isLogged(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        return preferences.getBoolean("IS_LOGGED", false);
    }

    //Consulta número de usuario
    public static int getUserId(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        return preferences.getInt("LOCAL_USER_ID", 0);
    }

    //Consulta reg Id del dispositivo
    public static  String getRegId(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        return preferences.getString("LOCAL_REG_ID", "");
    }

    //escribir una preferencia
    public static void setLocalUserData(Context context, int localUserId, boolean isLogged, String localRegId){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("IS_LOGGED", isLogged);
        edit.putInt("LOCAL_USER_ID", localUserId);
        edit.putString("LOCAL_REG_ID", localRegId);
        edit.commit();
    }

    public static void setLocalRegId(Context context, int localUserId, String localRegId){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt("LOCAL_USER_ID", localUserId);
        edit.putString("LOCAL_REG_ID", localRegId);
        edit.commit();
    }

    //Eliminar preferencias
    public static void deleteLocalUserData(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences("USER DATA", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("IS_LOGGED");
        edit.remove("LOCAL_USER_ID");
        edit.remove("LOCAL_REG_ID");
        edit.commit();
    }

}
