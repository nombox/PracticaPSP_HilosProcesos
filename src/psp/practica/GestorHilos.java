package psp.practica;

import java.io.File;

public class GestorHilos {

    public static Thread lanzarEnHilo(String comando, String[] argumentos, File fichero, VentanaProcesos ventana) {
        HiloProceso miProceso = new HiloProceso(comando, argumentos, fichero, ventana);

        Thread hiloProceso = new Thread(miProceso, "HiloProceso");

        hiloProceso.start();

        return hiloProceso;
    }

    public static void cancelar(Thread hilo) {
        if (hilo != null && hilo.isAlive()) {
            hilo.interrupt();
        }
    }

    public static String estado(Thread hilo) {

        if (hilo == null) {
            return "sin lanzar";
        }

        return hilo.getState() + "(Vivo: " + hilo.isAlive() + " )";
    }
}
