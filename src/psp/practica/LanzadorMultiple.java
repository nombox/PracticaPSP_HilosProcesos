package psp.practica;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class LanzadorMultiple {

    private VentanaProcesos ventana;
    private Semaphore semaforo;
    private int lanzados = 0;
    private int enEjecucion = 0;
    private int terminados = 0;
    private int conError = 0;
    private final Object cerrojo = new Object();
    
    public LanzadorMultiple(VentanaProcesos ventana) {
        this.ventana = ventana;
    }
    
    public void lanzarTodos(List<String> comandos, int hilosPool, int maxSimultaneos) {
        this.semaforo = new Semaphore(maxSimultaneos);
        
        ExecutorService pool = Executors.newFixedThreadPool(hilosPool);
        
        for (String linea : comandos) {
            pool.submit(new TareaProceso(linea, this));
        }
        
        pool.shutdown();
        
    }
    
    public VentanaProcesos getVentana() {
        return this.ventana;
    }
    
    public Semaphore getSemaforo() {
        return this.semaforo;
    }
    
    public CountDownLatch getLatch() {
        return null;
    }
    
    public void procesoIniciado() {
        synchronized (cerrojo) {
            lanzados++;
            enEjecucion++;
            ventana.actualizarContadores(lanzados, enEjecucion, terminados, conError);
        }
        
    }
    
    public void procesoTerminado(boolean correcto) {
        synchronized (cerrojo) {
            enEjecucion--;
            if (correcto) {
                terminados++;
            } else {
                conError++;
            }
            ventana.actualizarContadores(lanzados, enEjecucion, terminados, conError);
        }
    }
}
