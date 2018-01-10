import java.rmi.Remote;
import java.rmi.RemoteException;



public interface RmiInterface extends Remote{
    
    public String sincronizarResultado(String golos1, String golos2, String nome, String contador) throws RemoteException;
    
}
