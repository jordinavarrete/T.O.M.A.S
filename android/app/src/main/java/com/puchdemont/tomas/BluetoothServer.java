package com.puchdemont.tomas;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServer {
    public static class Helper {
        private static BluetoothServerSocket mmServerSocket;
        private static AppCompatActivity c;
        private static BluetoothAdapter adapter;
        public  static void initialise(AppCompatActivity context) {
            c = context;
            adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT},1);
                    return;
                }
                tmp = adapter.listenUsingRfcommWithServiceRecord("Hal", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Log.e("TAG", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;

            listen();
        }

        public static void listen() {

                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned.
                while (true) {
                    try {
                        socket = mmServerSocket.accept();
                    } catch (IOException e) {
                        Log.e("a", "Socket's accept() method failed", e);
                        break;
                    }

                    if (socket != null) {
                        Log.i("DEV", "CONNECTED!");
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        try {
                            OutputStream s = socket.getOutputStream();

                            String message = "GÃ¨nesi\n" +
                                    "Els orÃ\u00ADgens (1-11)\n" +
                                    "La creaciÃ³\n" +
                                    "\n" +
                                    "1\n" +
                                    "1 Al\n" +
                                    "\n" +
                                    "principi, DÃ©u va crear el cel i la terra. 2 La terra era caÃ²tica i desolada, les tenebres cobrien la superfÃ\u00ADcie de\n" +
                                    "l'oceÃ , i l'Esperit de DÃ©u planava sobre les aigÃ¼es.\n" +
                                    "3 DÃ©u diguÃ©:\n" +
                                    "--Que existeixi la llum.\n" +
                                    "I la llum va existir. 4 DÃ©u veiÃ© que la llum era bona, i separÃ  la llum de les tenebres. 5 DÃ©u va donar a la llum el nom\n" +
                                    "de dia, i a les tenebres, el de nit.\n" +
                                    "Hi haguÃ© un vespre i un matÃ\u00AD, i fou el primer dia.\n" +
                                    "6 DÃ©u diguÃ©:\n";


                            s.write(message.getBytes());
                            s.flush();
                            s.close();
                            socket.close();
                            mmServerSocket.close();

                            break;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                }
            }
                initialise(c);
        }

    }
}
