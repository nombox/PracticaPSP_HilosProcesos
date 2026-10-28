package psp.practica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorProcesos {

    public static ResultadoProceso ejecutar(String comando, String[] argumentos, File ficheroEntrada)
            throws IOException, InterruptedException {
        List<String> lista = new ArrayList<>();
        
        lista.add(comando);
        
        //Para pasar los argumentos
        if(argumentos != null){
            lista.addAll(Arrays.asList(argumentos));
        }
        
        ProcessBuilder pb = new ProcessBuilder(lista);
        
        //Si ficheroEntrada != null → pb.redirectInput(ficheroEntrada)
        if(ficheroEntrada !=null){
            pb.redirectInput(ficheroEntrada);
        }
        
        return null;
    }
}
