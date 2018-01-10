
import java.util.*;



public class Jogo {

    private String nomeEquipa1;
    private String nomeEquipa2;
    private int count = 0;
    private Hashtable<Integer, Logs> Logs = new Hashtable<Integer, Logs>();
    private ArrayList<String> mensagens = new ArrayList<String>();
    private Set<String> s = new LinkedHashSet<>(mensagens);
    private ArrayList<String> comentarios = new ArrayList<String>();
    
    
    public Jogo() {
        Logs l = new Logs();
        l.setGolos1(0);
        l.setGolos2(0);
        l.setNome("");
        Logs.put(count, l);
    }

    public synchronized void adicionaLogs(int golos1, int golos2, String nome){
        count = count + 1;
        Logs l = new Logs();
        l.setGolos1(golos1);
        l.setGolos2(golos2);
        l.setNome(nome);
        Logs.put(count, l);
    }
    
    public Logs currentGameState(){
        return Logs.get(count);
    }
    
    public int getTamanhoNomeEquipa1() {
        return nomeEquipa1.length();
    }
    
    public int getTamanhoNomeEquipa2() {
        return nomeEquipa2.length();
    }
    
    public String getNomeEquipa1() {
        return nomeEquipa1;
    }
    
    public String getNomeEquipa2() {
        return nomeEquipa2;
    }

    public int getCount() {
        return count;
    }

    public void setNomeEquipa1(String nomeEquipa1) {
        this.nomeEquipa1 = nomeEquipa1;
    }

    public void setNomeEquipa2(String nomeEquipa2) {
        this.nomeEquipa2 = nomeEquipa2;
    }
    
    public void adicionarMensagem(String nome, String comentario){
        String mensagem = "<p>" + nome + " - " + comentario + "</p>";
        mensagens.add(mensagem);
        s.addAll(mensagens);
        mensagens.clear();
        mensagens.addAll(s);
     }
    
    public void adicionarComentario(String nome, String comentario){
        String mensagem = "<p>" + nome + " - " + comentario + "</p>";
        comentarios.add(mensagem);
     }
    public int tamanhoListaComentarios(){
        return comentarios.size();
    }
     public String getComentarios(int index){
        return comentarios.get(index);
    }
    
    public int tamanhoListaMensagens(){
        return mensagens.size();
    }
    
    public String getMensagens(int index){
        return mensagens.get(index);
    }
    
    
}
class Logs{
    private int golos1 ;
    private int golos2;
    private String nome;

    
    public int getGolos1() {
        return golos1;
    }

    public void setGolos1(int golos1) {
        this.golos1 = golos1;
    }

    public int getGolos2() {
        return golos2;
    }

    public void setGolos2(int golos2) {
        this.golos2 = golos2;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}