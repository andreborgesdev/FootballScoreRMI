
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;



public class Server extends Thread{
    
    private static ServerSocket server;
    private static Socket connection;
    private static int DEFAULT_PORT=8082;
    private static String message = "";
    private static String response ="";
    
    
    static String SERVICE_NAME="/gestorJogoRemoto";
	
	static JogoRmi stub;
	
	private static void bindRMI(JogoRmi jogoRmi) throws RemoteException {
		System.getProperties().put( "java.security.policy", "./server.policy");

		if( System.getSecurityManager() == null) {
			System.setSecurityManager( new RMISecurityManager());
		}

		try { // start rmiregistry
			LocateRegistry.createRegistry( 1099);
		} catch( RemoteException e) {
			// if not start it
			// do nothing - already started with rmiregistry
		}

		LocateRegistry.getRegistry().rebind(SERVICE_NAME, jogoRmi);
		
	}
    
    public static void main(String[] args) throws IOException{
       Jogo e = new Jogo();
       HttpServer servidorhttp = HttpServer.create(new InetSocketAddress(8080), 0);
       servidorhttp.createContext("/", new MyHandler(e));
       servidorhttp.setExecutor(null); // creates a default executor
       servidorhttp.start();
        //Verifica se o utilizador passou parametros
       if (args.length != 2) {
            System.out.println("Erro: use java Server <Equipa1> <Equipa2>");
            System.exit(-1);
        }
        
       
        e.setNomeEquipa1(args[0]);
        e.setNomeEquipa2(args[1]);
        //Cria um ServerSocket na porta 8082
        try{
            server = new ServerSocket(DEFAULT_PORT);
        } catch(IOException ioException){
            System.err.println("Erro ao criar o servidor...");
            ioException.printStackTrace();
            System.exit(-1);
        }
       
        
        JogoRmi jogoRmi = null;
        try {
			jogoRmi = new JogoRmi(e);
		} catch (RemoteException e1) {
			System.err.println("unexpected error...");
			e1.printStackTrace();
		}
        try {
			bindRMI(jogoRmi);
		} catch (RemoteException e1) {
			System.err.println("erro ao registar o stub...");
			e1.printStackTrace();
		}
    
        //Aguarda pelo aparecimento de ligacoes e cria uma thread para cada uma delas atrav�s da class Connection
        while(true){
            try{
                System.out.println("A espera de um cliente");
                connection = server.accept();
                Connection c = new Connection(connection, e);
                c.start();
        } catch(IOException ioException){
            System.err.println("Erro ao criar o servidor...");
            ioException.printStackTrace();
            System.exit(-1);
            } 
        }
    }
    
   
         
     static class MyHandler implements HttpHandler {
        Jogo e;

        public MyHandler(Jogo e) {
            this.e = e;
        }

        public String criaMenu(String response){
             int golos1 = e.currentGameState().getGolos1();
             int golos2 = e.currentGameState().getGolos2();
             String eq1 = e.getNomeEquipa1();
             String eq2 = e.getNomeEquipa2();
             String nome = e.currentGameState().getNome();
              response = "<h1>Bem vido ao servidor de jogo "+eq1+"-"+eq2+"</h1>\n"
                      + " <ul>\n" +
                        "        \n" +
                        "    </ul>\n" +
                        "    <form action=\"http://192.168.43.234:8080/\" method=\"post\" >\n" +
                        "        <div>\n" +
                        "        <input type=\"text\" name=\"nickname\" value=\"anonymous\"\n" +
                        "        placeholder=\"Introduza o seu nickname\">\n" +
                        "        </div>\n" +
                        "        <div>\n" +
                        "        <textarea placeholder=\"Introduza o seu comentario.\"\n" +
                        "        name=\"comment\">\n" +
                        "        </textarea>\n" +
                        "        </div>\n" +
                        "        <div class=\"button\">\n" +
                        "        <button type=\"submit\">Submeta o seu comentario</button>\n" +
                        "        </div>\n" +
                        "    </form>";
             if(e.getCount()== 0){
                response+= "<h2>Resultado atual do jogo: "+golos1+"-"+golos2+"</h2>";
             }
             else if(e.getCount()!= 0){
                response +=  "<h2>Evento mais recente<br>O resultado foi atualizado para "+golos1+"-"+golos2+"</h2>";
         
                e.adicionarMensagem("Server", "O resultado foi atualizado para "+golos1+"-"+golos2+" por: "+nome);
            }
            if (e.tamanhoListaMensagens() < 20) {
                for(int i = 0; i < e.tamanhoListaMensagens(); i++){         
                response += e.getMensagens(i);
            } 
            }else{
                  for(int i = e.tamanhoListaMensagens() - 20; i < e.tamanhoListaMensagens(); i++){         
                response += e.getMensagens(i);   
                    
            
            }}

//  ARRAY DIFERENTE QUE PERMITE DUPLICADOS
                if (e.tamanhoListaComentarios() < 20) {
                     for(int i = 0; i < e.tamanhoListaComentarios(); i++){         
                response += e.getComentarios(i);
            }
                }else{
                     for(int i = e.tamanhoListaComentarios() - 20; i < e.tamanhoListaComentarios(); i++){         
                     response += e.getComentarios(i);
                }}
           
            
            return response;
        }
        
        
        
        @Override
        public void handle(HttpExchange t) throws IOException {
            String metodo = t.getRequestMethod();
            
            if(metodo.equals("GET")){
            criaMenu(response);

            t.sendResponseHeaders(200, criaMenu(response).length());
            OutputStream os = t.getResponseBody();
            os.write(criaMenu(response).getBytes());
            os.close();
            
            }
            if(metodo.equals("POST")){
//                try (InputStream input = t.getRequestBody()) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(input));
//                    String content = br.readLine();
//                    content = content.replaceAll("[\\[\\]\\(\\)]", "");
//                    content = content.replaceAll("\"", "");
//                    List<String> items = Arrays.asList(content.split("\\s*,\\s*"));
//                   e.adicionarMensagem(items.get(0), items.get(1));
//
////  ADICIONA OS COMENTARIOS A ESSE ARRAY DIFERENTE
////                    e.adicionarComentario(items.get(0), items.get(1));
//                    input.close();
                
                 Map<String, Object> parameters = new HashMap<String, Object>();
                 InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                 BufferedReader br = new BufferedReader(isr);
                 String query = br.readLine();
                 parseQuery(query, parameters);
                 

                 String nome =  (String) parameters.get("nickname");
                 String comentario = (String) parameters.get("comment");
                 if(comentario != null && nome != null){
                   e.adicionarComentario(nome,comentario);  
                 }
                 
                 t.sendResponseHeaders(200, criaMenu(response).length());
                 OutputStream os = t.getResponseBody();
                 os.write(criaMenu(response).getBytes());
                 os.close();
                 
                 
                
//                } catch(IOException ioException){
//                     System.err.println("Erro ao executar o servidor: " + ioException);
//                     ioException.printStackTrace();
//                     System.exit(-1);
//                }
            }
            
        }
    }
      public static void parseQuery(String query, Map<String, 
	Object> parameters) throws UnsupportedEncodingException {

         if (query != null) {
                 String pairs[] = query.split("[&]");
                 for (String pair : pairs) {
                          String param[] = pair.split("[=]");
                          String key = null;
                          String value = null;
                          if (param.length > 0) {
                          key = URLDecoder.decode(param[0], 
                          	System.getProperty("file.encoding"));
                          }

                          if (param.length > 1) {
                                   value = URLDecoder.decode(param[1], 
                                   System.getProperty("file.encoding"));
                          }

                          if (parameters.containsKey(key)) {
                                   Object obj = parameters.get(key);
                                   if (obj instanceof List<?>) {
                                            List<String> values = (List<String>) obj;
                                            values.add(value);

                                   } else if (obj instanceof String) {
                                            List<String> values = new ArrayList<String>();
                                            values.add((String) obj);
                                            values.add(value);
                                            parameters.put(key, values);
                                   }
                          } else {
                                   parameters.put(key, value);
                          }
                 }
         }
         }
}

