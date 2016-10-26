package br.com.alura.searchdrink;

import android.content.Intent;

/**
 * Created by Birbara on 26/10/2016.
 */

public class DatasHelper {

    public String pegauId(Intent intent) {
        return intent.getExtras().getString("user_id");
    }

    public String pegaUrlFoto(Intent intent) {
        return intent.getExtras().getString("profile_picture");
    }
}
