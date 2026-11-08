package psp.practica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import static psp.practica.GestorProcesos.CHARSET_CONSOLA;

public class LectorSalida implements Runnable {
    private InputStream flujo;
    private BlockingQueue<String> cola;

    public static final String FIN = "<<FIN>>";

    public LectorSalida(InputStream flujo, BlockingQueue<String> cola) {
        this.flujo = flujo;
        this.cola = cola;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(flujo, GestorProcesos.CHARSET_CONSOLA))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                cola.put(linea);
                
            }
        } catch (IOException ex) {
            System.getLogger(LectorSalida.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (InterruptedException ex) {
            System.getLogger(LectorSalida.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } finally{
            cola.offer(FIN);
        }
    }
}
