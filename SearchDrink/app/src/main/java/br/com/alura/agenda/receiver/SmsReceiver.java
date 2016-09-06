package br.com.alura.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.alura.agenda.R;
import br.com.alura.agenda.dao.BarDAO;

/**
 * Created by Birbara on 08/08/2016.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        byte[] pdu = (byte[]) pdus[0];

        String format = (String) intent.getSerializableExtra("format");

        SmsMessage message = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            message = SmsMessage.createFromPdu(pdu, format);
        }
        String telefone = message.getOriginatingAddress();

        BarDAO dao = new BarDAO(context);

        if(dao.ehBar(telefone)) {
            Toast.makeText(context, "SMS recebido!", Toast.LENGTH_SHORT).show();

            MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
            mp.start();
        }

        dao.close();
    }
}
