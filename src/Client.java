

import java.io.*;
import java.net.*;



public class Client extends Thread{
    
    private static DataInputStream input;
    private static DataOutputStream output;
    private static Socket connection;
    private static final int DEFAULT_PORT=8082;
    private static final String DEFAULT_HOST="192.168.43.234"; 
    
    
    public static void main(String[] args){

        //Verifica se o utilizador passou parametros
        if (args.length != 4) {
            System.out.println("Erro: use java Client <GolosEquipa1> <GolosEquipa2> <NomeCliente> <Contador>");
            System.exit(-1);
        }
        
        //Cria um objeto do tipo InetAdress com base no ip do servidor
        InetAddress address = null;
        try {
            address = InetAddress.getByName(DEFAULT_HOST);
        } catch(UnknownHostException unknownHostException) {
            System.out.println("Endereco Desconhecido " + unknownHostException);
            System.exit(-1);
        }
        
        //Cria um socket para establecer uma liga��o com o servidor e porta pretendidos
        try{
            System.out.println("A conectar ao servidor");
            connection = new Socket(address, DEFAULT_PORT);
            System.out.println("Conexao establecida com o servidor " + address.getHostName() + " na porta " + DEFAULT_PORT);
        } catch(IOException ioException){
            System.err.println("Erro ao criar a conexao com o servidor...");
            ioException.printStackTrace();
            System.exit(-1);
        }

        try{
            //cria uma stream para ler os dados que chegam do socket
            input = new DataInputStream(connection.getInputStream());
            //cria uma stream para escrever para o socket
            output = new DataOutputStream(connection.getOutputStream());

           
          

            //Constroi o pedido para enviar ao servidor passado inteiros para arrays de bytes - 
            //No fim junta se todos os arrays num so
            
            int golos1 = Integer.parseInt(args[0]);
            byte g1 = (byte) ((int) golos1);
            byte[] golosEquipa1 = {g1};
            int golos2 = Integer.parseInt(args[1]);
            byte g2 = (byte) ((int) golos2);
            byte[] golosEquipa2 = {g2};
            String nome = args[2];
            byte[] asciiNome = nome.getBytes();
            int tamanhoNome = nome.length();
            byte g3 = (byte) ((int) tamanhoNome);
            byte[] tamanhoNomeCliente = {g3};
            short contador = Short.parseShort(args[3]);
            byte g4 = (byte) ((short) contador);
            byte[] contadorJogo = {g4};
            
            // Cria um array de destino para todos os arrays
            byte[] pedido = new byte[golosEquipa1.length + golosEquipa2.length + tamanhoNomeCliente.length + asciiNome.length + contadorJogo.length];
            
            // copia os arrays para esse array de destino)
            System.arraycopy(golosEquipa1, 0, pedido, 0, golosEquipa1.length);
            System.arraycopy(golosEquipa2, 0, pedido, 1, golosEquipa2.length);
            System.arraycopy(tamanhoNomeCliente, 0, pedido, 2, tamanhoNomeCliente.length);
            System.arraycopy(asciiNome, 0, pedido, 3, asciiNome.length);
            System.arraycopy(contadorJogo, 0, pedido, 3 + asciiNome.length, contadorJogo.length);


        
            
            //Envia o pedido ao servidor
            output.write(pedido);
            output.flush();
            
            //Espera pela respota do servidor
            byte [] resposta = new byte[100];
            input.read(resposta);
            byte [] numCaracteresEquipa1Array = new byte[1];
            System.arraycopy(resposta, 0, numCaracteresEquipa1Array, 0, 1);
            int numCaracteresEquipa1 = (int) numCaracteresEquipa1Array[0];
            String nomeEquipa1 = "";
          
            //Itera sobre as varias letras do nome da Equipa1
            for(int i = 0; i<numCaracteresEquipa1; i++){
                byte [] letra = new byte[1];
                System.arraycopy(resposta, 1+i, letra, 0, 1);
                String letraunica = new String (letra);
                nomeEquipa1 += letraunica;
            }
           
            byte [] numCaracteresEquipa2Array = new byte[1];
            System.arraycopy(resposta, numCaracteresEquipa1 + 1, numCaracteresEquipa2Array, 0, 1);
            int numCaracteresEquipa2 = (int) numCaracteresEquipa2Array[0];
            String nomeEquipa2 = "";
            
            //Itera sobre as varias letras do nome da Equipa2
             for(int i = 0; i<numCaracteresEquipa2; i++){
                byte [] letra = new byte[1];
                System.arraycopy(resposta, numCaracteresEquipa1+2+i, letra, 0, 1);
                String letraunica = new String (letra);
                nomeEquipa2 += letraunica; 
            }
            byte [] golosEquipa1Array = new byte[1];
            System.arraycopy(resposta, numCaracteresEquipa1 + numCaracteresEquipa2 + 2,  golosEquipa1Array,0 , 1);
            int golosEquipa1Server = (int)  golosEquipa1Array[0];
            byte [] golosEquipa2Array = new byte[1];
            System.arraycopy(resposta,numCaracteresEquipa1 + numCaracteresEquipa2 + 3,  golosEquipa2Array,0 , 1);
            int golosEquipa2Server = (int)  golosEquipa2Array[0];
            byte [] contadorArray = new byte[1];
            System.arraycopy(resposta, numCaracteresEquipa1 + numCaracteresEquipa2 + 4, contadorArray,0, 1);
            int contadorServer = (int) contadorArray[0];
            
            //Imprime no dliente a Respota do Servidor
            System.out.println("JOGO: \n" + nomeEquipa1 + " " + golosEquipa1Server + " - " + golosEquipa2Server + " " + nomeEquipa2);
             
            //Fecha a conex�o e as streams
            output.close();
            input.close();
            connection.close();
            System.out.println("A conexao foi terminada");
            
        } catch(IOException ioException){
            System.err.println("Erro ao comunicar com o servidor: " + ioException);
            ioException.printStackTrace();
            System.exit(-1);
        }

    
    
}
}
