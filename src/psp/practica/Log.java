package psp.practica;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import static java.nio.file.StandardOpenOption.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Log {

    public static final File FICHERO = new File("errores.log");

    public static void registrar(String texto) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String linea = String.format("[%s][%s]%s%n",ahora.format(formato),Thread.currentThread().getName(),texto ); 
        
        try{
            Files.writeString(FICHERO.toPath(), linea, StandardCharsets.UTF_8, CREATE, APPEND);
            
            
        }catch(IOException EX){
            System.err.println("Error al escribir el fichero Log");
        }
        
    }

}
