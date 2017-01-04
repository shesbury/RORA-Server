/**
 * Created by Ruby on 2016-11-29.
 */

package fr.utbm.tr54.roraserver.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import fr.utbm.tr54.roraserver.R;
import fr.utbm.tr54.roraserver.network.ClientScanResult;
import fr.utbm.tr54.roraserver.network.FinishScanListener;
import fr.utbm.tr54.roraserver.network.WifiApManager;
import fr.utbm.tr54.roraserver.server.Controller;

/**
 * Main and only activity of our app.
 * @author Ruby  Hossain Saib
 */
public class MainActivity extends AppCompatActivity {
    TextView textView1;
    WifiApManager wifiApManager;
    Controller controller;

    /**
     * Called when the activity is first created.
     * Setup the view and create all the necessary tools of our app :
     * Controller and wifiApManager
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView1);
        wifiApManager = new WifiApManager(this);
        controller = new Controller();

        scan();
    }

    /**
     * Trigger a scan of the connected device and draw them in the textView
     * starts a new Thread of the controller
     */
    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                textView1.setText("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    textView1.append("####################\n");
                    textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                    textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
                }

                Thread thread = new Thread(controller);
                thread.start();

            }
        });
    }

    /**
     * Manage creation of the overall view and option buttons.
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Manage buttons and here specifically refresh button to
     * update the connected devices and restart controller's thread
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                scan();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}