package cz.weissar.weartracker.service;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeviceBroadcaster {

    public static void notifyDevices(final Context context) {
        final Task<List<Node>> connectedNodes = Wearable.getNodeClient(context).getConnectedNodes();
        // TODO přepsat do nového vlákna                // https://github.com/tutsplus/get-wear-os-and-android-talking/blob/master/wear/src/main/java/com/jessicathornsby/datalayer/MainActivity.java

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Node> nodes = Tasks.await(connectedNodes);
                    for (Node node : nodes) {
                        Wearable.getMessageClient(context).sendMessage(node.getId(), "TEST", null);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
