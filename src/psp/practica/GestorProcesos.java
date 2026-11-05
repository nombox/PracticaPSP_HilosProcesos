package psp.practica;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorProcesos {
    //7
    public static final Charset CHARSET_CONSOLA = Charset.forName("CP850");

    public static ResultadoProceso ejecutar(String comando, String[] argumentos, File ficheroEntrada)
            throws IOException, InterruptedException {
        
        List<String> comandoArgumentos = new ArrayList<>();
        StringBuilder contenidoStdout = new StringBuilder();
        ResultadoProceso repo = null;
        
        comandoArgumentos.add(comando);
        
        //Para pasar los argumentos
        if(argumentos != null){
            //1
            comandoArgumentos.addAll(Arrays.asList(argumentos));
        }
        
        ProcessBuilder pb = new ProcessBuilder(comandoArgumentos);
        
        //2. Si ficheroEntrada != null → pb.redirectInput(ficheroEntrada)
        if(ficheroEntrada !=null){
            pb.redirectInput(ficheroEntrada);
        }
        
        //3.
        Log.registrar("=== Lanzado: "+comandoArgumentos);
        
        //4 stderr al log sin borrar
        pb.redirectError(ProcessBuilder.Redirect.appendTo(Log.FICHERO));

        try{
        //5
        Process proceso = pb.start();
        
        //6
        proceso.getOutputStream().close();
        
        //7
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader( proceso.getInputStream(), CHARSET_CONSOLA))){
            String linea;
            while((linea = br.readLine())!=null){
                contenidoStdout.append(linea);
                contenidoStdout.append(System.lineSeparator());
            }
        }
        
        //9
        int codigo = proceso.waitFor();
        
        //agrego el mensaje para saber si 
        Log.registrar("Codigo de salida: "+ codigo);
        
        repo = new ResultadoProceso(contenidoStdout.toString(), codigo);
        }catch(IOException e){
            Log.registrar(e.getMessage());
            throw e;
        }
        
        
        //10
        return repo;
    }
}
