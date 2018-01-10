
import java.rmi.registry.LocateRegistry;
import java.util.Iterator;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Andre
 */
public class RmiClient {
    	static String SERVICE_NAME="/gestorJogoRemoto";

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Erro: use java Client <GolosEquipa1> <GolosEquipa2> <NomeCliente> <Contador>");
			System.exit(-1);
		}

		System.setProperty("java.rmi.server.hostname","192.168.43.234");

		try {
			RmiInterface stub =  (RmiInterface) LocateRegistry.getRegistry("192.168.43.234").lookup(SERVICE_NAME);
                       String line = "";
                           line = stub.sincronizarResultado(args[0],args[1],args[2],args[3]);
                        System.out.println(line);
		} catch (Exception e) {
			System.err.println("Erro ao realizar a operacao:");
			e.printStackTrace();
		}
	}    
}
