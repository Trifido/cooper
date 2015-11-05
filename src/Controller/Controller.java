package Controller;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * Clase Controller
 * Recibe mensajes del agente Listener y realiza la toma de decisiones
 * 
 * @author Alba Ríos
 */
public class Controller extends SingleAgent{
    
    private int battery;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Square[][] world;
    
    public Controller(AgentID aid) throws Exception {
        super(aid);
        this.battery = 0;
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Square[500][500];
        
        // Inicializar el mapa completo a valor "desconocido"
        for (Square[] row : this.world){
            for (Square s : row){
                s = Square.UNKNOWN;
            }
        }
    }
    
    @Override
    public void execute(){
    }
    
}
