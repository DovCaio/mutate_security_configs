package com.caio.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetData {

    public static String mothDayMinutsAndSecs(){
         LocalDateTime agora = LocalDateTime.now();

        DateTimeFormatter formatterCompleto = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        String dataHoraFormatada = agora.format(formatterCompleto);
        return dataHoraFormatada;
    }
    
}
