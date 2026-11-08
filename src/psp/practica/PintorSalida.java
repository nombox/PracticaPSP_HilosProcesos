package psp.practica;

import java.util.concurrent.BlockingQueue;

public class PintorSalida implements Runnable {

    BlockingQueue<String> cola;
    VentanaProcesos ventana;

    public PintorSalida(BlockingQueue<String> cola, VentanaProcesos ventana) {
        this.ventana = ventana;
        this.cola = cola;
    }

    @Override
    public void run() {
        try {
            while (true) {

                String linea = cola.take();

                if (linea.equals(LectorSalida.FIN)) {
                    break;
                }

                ventana.anadirSalida(linea);

            }
        } catch (InterruptedException ex) {
            System.getLogger(PintorSalida.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

}
