package com.renanmateus.vendafridas.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HoraPedidoUtils {

	public static String converteDataHora() {
		LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String hojeFormatado = agora.format(formatter);
        return hojeFormatado;
	}
	
	
}
