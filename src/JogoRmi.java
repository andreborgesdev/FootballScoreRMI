
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JogoRmi extends UnicastRemoteObject implements RmiInterface { 
Jogo jogo;
    
    public JogoRmi(Jogo jogo) throws RemoteException{
       this.jogo = jogo;
    }


    
    
    
    public String sincronizarResultado(String golos1, String golos2, String nome, String contador) throws RemoteException{
        String equipa1 = jogo.getNomeEquipa1();
        String equipa2 =  jogo.getNomeEquipa2();
        int golosEquipa1 =  jogo.currentGameState().getGolos1();
        int golosEquipa2 =  jogo.currentGameState().getGolos2();
        String nomeCliente =  jogo.currentGameState().getNome();
        if(Integer.parseInt(contador) == 0){
            return "JOGO: \n" + equipa1 + " " + golosEquipa1 + " - " + golosEquipa2 + " " + equipa2;
        } else if(Integer.parseInt(contador) > 0){
            jogo.adicionaLogs(Integer.parseInt(golos1), Integer.parseInt(golos2), nome);
            return "JOGO: \n" + equipa1 + " " + golos1 + " - " + golos2 + " " + equipa2 + "\n alterado por: " + nome;
        } else {
            return "Nao e possivel definir o contador com um valor negativo";
        }
        
 }
    


}
