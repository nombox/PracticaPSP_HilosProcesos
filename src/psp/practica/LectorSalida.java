package psp.practica;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class LectorSalida implements Runnable {

    public static final String FIN = "<<FIN>>";

    public LectorSalida(InputStream flujo, BlockingQueue<String> cola) {
    }

    @Override
    public void run() {
    }
}
