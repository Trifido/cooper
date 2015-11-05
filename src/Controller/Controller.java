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
    private double minValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Square[][] world;
    
    public Controller(AgentID aid) throws Exception {
        super(aid);
        this.battery = 0;
        this.minValue= Double.POSITIVE_INFINITY;
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
    
    public String nextAction(Pair<Integer, Integer> npos){
        String act= new String();
        
        if (npos.first == 1 && npos.second == 1) {
            // NW
            act = "moveNW";
        } else if (npos.first == 1 && npos.second == 2) {
            // N
            act = "moveN";
        } else if (npos.first == 1 && npos.second == 3) {
            // NE
            act = "moveNE";
        } else if (npos.first == 2 && npos.second == 1) {
            // W
            act = "moveW";
        } else if (npos.first == 2 && npos.second == 3) {
            // E
            act = "moveE";
        } else if (npos.first == 3 && npos.second == 1) {
            // SW
            act = "moveSW";
        } else if (npos.first == 3 && npos.second == 2) {
            // S
            act = "moveS";
        } else if (npos.first == 3 && npos.second == 3) {
            //SE
            act = "moveSE";
        }
        
        return act;
    }
     
    public Pair Heuristic(){
        Pair<Integer, Integer> newpos = new Pair(2,2);
        
        if(this.battery < 2)
            System.out.println("REPOSTAR");
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2){
            System.out.println("ENCONTRADA");
        }
        else{
            for(int i=1; i<4; i++)
                for(int j=1; j<4; j++)
                    if(radar[i][j] != 1 && this.minValue > this.scanner[i][j]){
                        this.minValue= this.scanner[i][j];
                        newpos.first= i;
                        newpos.second= j;
                    }
        }
        
        return newpos;
    }
    
    @Override
    public void execute(){
        
    }
    
}
