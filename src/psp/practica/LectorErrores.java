package psp.practica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static psp.practica.GestorProcesos.CHARSET_CONSOLA;

public class LectorErrores implements Runnable {
    
    InputStream flujo;
    
    public LectorErrores(InputStream flujo) {
    this.flujo = flujo;
    }

    @Override
    public void run() {
            try(BufferedReader br = new BufferedReader(
            new InputStreamReader( flujo, GestorProcesos.CHARSET_CONSOLA))){
            String linea;
            while((linea = br.readLine())!=null){
                Log.registrar("[stderr] "+linea);
            }
        } catch (IOException ex) {
            System.getLogger(LectorErrores.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
