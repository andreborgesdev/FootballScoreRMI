
import java.io.*;
import java.net.*;
import java.util.*;


public class Connection extends Thread {
    
    private DataOutputStream output;
    private DataInputStream input;
    private ServerSocket server;
    private Socket connection;
    private static int DEFAULT_PORT=8082;
    private String message = ""; 
    private Jogo e;
    
    
    //Cria um Socket para cada nova liga��o e obt�m a refer�ncia das Streams de entrada e/ou sa�da
    public Connection(Socket clientConnection, Jogo e){
        this.connection = clientConnection;
        this.e = e;
       
        try{
            // cria uma stream para ler os dados que chegam do socket
             this.input = new DataInputStream (connection.getInputStream());
             
            //cria uma PrintStream para escrever para o socket
            this.output = new DataOutputStream(connection.getOutputStream());
            
        } catch(IOException ioException){
            System.err.println("Erro ao criar o servidor: " + ioException);
            ioException.printStackTrace();
            System.exit(-1);
        }
    }
    
    //L� e escreve no o Socket atrav�s de Streams
    public void run(){
        try{
            System.out.println("Conexao establecida com o cliente " + connection.getInetAddress().getHostName() + " na porta " + connection.getPort());
            //Armazena cada campo de mensagem do pedido do cliente em tokens diferentes e armazena o primeiro token
            String equipa1 = e.getNomeEquipa1();
            String equipa2 = e.getNomeEquipa2();
            int golos1 = e.currentGameState().getGolos1();
            int golos2 = e.currentGameState().getGolos2();
            int tamanhoNomeEquipa1 = e.getTamanhoNomeEquipa1();
            int tamanhoNomeEquipa2 = e.getTamanhoNomeEquipa2();
            int contadorServer = e.getCount();
            
            //L� o pedido feito pelo cliente
            byte [] resposta = new byte[100];
            input.read(resposta);
            byte [] golos1Array = new byte[1];
            System.arraycopy(resposta, 0, golos1Array, 0, 1);
            int goloseq1 = (int) golos1Array[0];
            byte [] golos2Array = new byte[1];
            System.arraycopy(resposta, 1, golos2Array, 0, 1);
            int goloseq2 = (int) golos2Array[0];
 
            byte [] tamanhoNomeArray = new byte[1];
            System.arraycopy(resposta, 2, tamanhoNomeArray, 0, 1);
            int tamanhoNome = (int) tamanhoNomeArray[0];
            
            String nome ="";
            for(int i = 0; i<tamanhoNome; i++){
                byte [] letra = new byte[1];
                System.arraycopy(resposta, 3+i, letra, 0, 1);
                String letraunica = new String (letra);
                nome += letraunica; 
            }
             if(contadorServer > 255){
                int quociente = contadorServer/255;
                int resto = (255 * quociente) - contadorServer;
                byte byte1 = (byte) ((int) quociente);
                byte[] byte1Array = {byte1};
                byte byte2 = (byte) ((int) resto);
                byte[] byte2Array = {byte2};
                byte [] contadorArray = new byte[2];
                System.arraycopy(byte1Array, 0, contadorArray, 0, 1);
                System.arraycopy(byte2Array, 0, contadorArray, 1, 1);
            }
            
            byte [] contadorArray = new byte[2];
            System.arraycopy(resposta, 3+tamanhoNome, contadorArray, 0, 2);
            
            short count = (short) contadorArray[0];
                        
            System.out.println("Pedido = " + Arrays.toString(resposta));
            
            
            
            //CRIA A RESPOSTA DO SERVIDOR
                byte[] asciiNomeEquipa1 = equipa1.getBytes();
                byte[] asciiNomeEquipa2 = equipa2.getBytes();
                byte g1 = (byte) ((int) golos1);
                byte[] golosEquipa1 = {g1};
                byte g2 = (byte) ((int) golos2);
                byte[] golosEquipa2 = {g2};
                byte t1 = (byte) ((int) tamanhoNomeEquipa1);
                byte[] tamanhoEquipa1 = {t1};
                byte t2 = (byte) ((int) tamanhoNomeEquipa2);
                byte[] tamanhoEquipa2 = {t2};
                byte cS = (byte) ((int) contadorServer);
                byte[] contadorArr = {cS};
                
                //MOSTRA RESULTADO
                if(count == 0){
                    
                    //Cria um array de destino, neste caso respostaServidor, que tem o tamanho do nome das 2 equipas
                    byte[] respostaServidor = new byte[asciiNomeEquipa1.length + asciiNomeEquipa2.length + 6];
            
                    // Copia os valores da vari�veis da resposta para um �nico array respostaServidor
                    System.arraycopy(tamanhoEquipa1, 0, respostaServidor, 0, 1);
                    System.arraycopy(asciiNomeEquipa1, 0, respostaServidor, 1, tamanhoNomeEquipa1);
                    System.arraycopy(tamanhoEquipa2, 0, respostaServidor, tamanhoNomeEquipa1 + 1, 1);
                    System.arraycopy(asciiNomeEquipa2, 0, respostaServidor, tamanhoNomeEquipa1 + 2, tamanhoNomeEquipa2);
                    System.arraycopy(golosEquipa1, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 2, 1);
                    System.arraycopy(golosEquipa2, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 3, 1);
                    System.arraycopy(contadorArr, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 4, 1);
                    
                    //Imprime no servidor a resposta que vai enviar
                    System.out.println("\n" + Arrays.toString(respostaServidor));
                   
                    //Envia resposta para o cliente
                    output.write(respostaServidor);
                  
                    
              //ATUALIZA RESULTADO      
            } if(count > 0){
                
                //N�o permite que 2 clientes alterem o resultado do jogo ao mesmo tempo
                e.adicionaLogs(goloseq1, goloseq2, nome);
                
                
                //Copia os valores da vari�veis da resposta para um �nico array respostaServidor
                byte[] respostaServidor = new byte[asciiNomeEquipa1.length + asciiNomeEquipa2.length + 6];
                System.arraycopy(tamanhoEquipa1, 0, respostaServidor, 0, 1);
                System.arraycopy(asciiNomeEquipa1, 0, respostaServidor, 1, tamanhoNomeEquipa1);
                System.arraycopy(tamanhoEquipa2, 0, respostaServidor, tamanhoNomeEquipa1 + 1, 1);
                System.arraycopy(asciiNomeEquipa2, 0, respostaServidor, tamanhoNomeEquipa1 + 2, tamanhoNomeEquipa2);
                System.arraycopy(golos1Array, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 2, 1);
                System.arraycopy(golos2Array, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 3, 1);
                System.arraycopy(contadorArr, 0, respostaServidor, tamanhoNomeEquipa1 + tamanhoNomeEquipa2 + 4, 1);
               
                //Imprime no servidor a resposta que vai enviar
                System.out.println(Arrays.toString(respostaServidor));
                
                //Envia resposta para o cliente
                output.write(respostaServidor);
            } 
            
            //Envia todos os outputs que estiverem em buffer e fecha a conex�o e as streams
            output.flush();
            output.close();
            input.close();
            connection.close();
        } catch(IOException ioException){
            System.err.println("Erro ao executar o servidor: " + ioException);
            ioException.printStackTrace();
            System.exit(-1);
        }
    }

}
