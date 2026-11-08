package psp.practica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HiloProceso implements Runnable {

    //Parametros
    private String comando;
    private String[] argumentos;
    private File fichero;
    private VentanaProcesos ventana;

    public HiloProceso(String comando, String[] argumentos, File fichero, VentanaProcesos ventana) {
        this.comando = comando;
        this.argumentos = argumentos;
        this.fichero = fichero;
        this.ventana = ventana;
    }

    @Override
    public void run() {
        

        //CREACIÓN DEL PROCESSBUILDER
        List<String> comandoArgumentos = new ArrayList<>();
        StringBuilder contenidoStdout = new StringBuilder();
        ResultadoProceso repo = null;

        comandoArgumentos.add(comando);

        if (argumentos != null) {
            comandoArgumentos.addAll(Arrays.asList(argumentos));
        }

        ProcessBuilder pb = new ProcessBuilder(comandoArgumentos);

        if (fichero != null) {
            pb.redirectInput(fichero);
        }

        Log.registrar("=== Lanzado: " + comandoArgumentos);

        try {

            Process proceso = pb.start();

            proceso.getOutputStream().close();
            
            BlockingQueue<String> cola = new LinkedBlockingQueue<>();
 
            LectorSalida tareaLector = new LectorSalida(proceso.getInputStream(), cola);     
            Thread hiloLectorSalida = new Thread(tareaLector, "LectorSalida");
            
            PintorSalida tareaPintor = new PintorSalida(cola, ventana);
            Thread hiloPintorSalida = new Thread(tareaPintor, "PintorSalida");
            
            LectorErrores tareaErrores = new LectorErrores(proceso.getErrorStream());
            Thread hiloLectorErrores = new Thread(tareaErrores, "LectorErrores");
            
            hiloLectorSalida.start();
            hiloPintorSalida.start();
            hiloLectorErrores.start();
            
            int codigo = proceso.waitFor();
            
            hiloLectorSalida.join();
            hiloPintorSalida.join();
            hiloLectorErrores.join();
            
            Log.registrar("Codigo de salida"+codigo);
            ventana.mostrarFin("Codigo de salida: "+codigo);
                       
            
            

        } catch (IOException e) {
            ventana.mostrarFin("Error: "+e.getMessage());
            
        } catch (InterruptedException ex) {
            ventana.mostrarFin("Error: "+ex.getMessage());
        }

    }
}
