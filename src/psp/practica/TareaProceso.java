package psp.practica;

import java.io.IOException;
import java.util.Arrays;

public class TareaProceso implements Runnable {

    private String lineaComando;
    private LanzadorMultiple lanzador;

    public TareaProceso(String lineaComando, LanzadorMultiple lanzador) {
        this.lineaComando = lineaComando;
        this.lanzador = lanzador;
    }

    @Override
    public void run() {

        String[] partes = lineaComando.split("\\s");
        String comando = partes[0];
        String[] argumentos = Arrays.copyOfRange(partes, 1, partes.length);

        lanzador.getVentana().anadirActividad(">> [" + Thread.currentThread().getName() + "] EMPIEZA: " + lineaComando);

        lanzador.getVentana().anadirActividad("..[" + Thread.currentThread().getName() + "] esperando plaza...");
        try {
            lanzador.getSemaforo().acquire();
            lanzador.procesoIniciado();
            

            ResultadoProceso resultado = GestorProcesos.ejecutar(comando, argumentos, null);
            
            boolean fueCorrecto= (resultado.codigoSalida()==0);
            lanzador.procesoTerminado(fueCorrecto);
            
            lanzador.getVentana().anadirActividad(">> [" + Thread.currentThread().getName() + "] TERMINA: " + lineaComando + " -> codigo" + resultado.codigoSalida());

        } catch (IOException ex) {
            lanzador.getVentana().anadirActividad("TERMINA" + ex.getMessage());
            lanzador.procesoTerminado(false);
        } catch (InterruptedException ex) {
            lanzador.getVentana().anadirActividad("TERMINA" + ex.getMessage());
            lanzador.procesoTerminado(false);
        } finally {
            lanzador.getSemaforo().release();
        }

    }
}
