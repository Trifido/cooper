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
    private int[][] world;
    
    public Controller(AgentID aid) throws Exception {
        super(aid);
        this.battery = 0;
        this.minValue= Double.POSITIVE_INFINITY;
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new int[500][500];
        
        // Inicializar el mapa completo a valor "desconocido"
        for (int[] row : this.world){
            for (int s : row){
                s = 1;
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
     
    public String Heuristic(){
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        
        if(this.battery < 2)
            return "REPOSTAR";
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2)
            return "ENCONTRADO";
        else{
            for(int i=1; i<4; i++)
                for(int j=1; j<4; j++)
                    if((i!=2 && j!= 2) && radar[i][j] != 1){ 
                        benefit= this.scanner[i][j]/this.world[i][j];
                        if(this.minValue > benefit){
                            this.minValue= benefit;
                            newpos.first= i;
                            newpos.second= j;
                        }
                    }
            
            world[newpos.first][newpos.second]++;
            
            return nextAction(newpos);
        }
    }
    
    @Override
    public void execute(){
        
    }
    
}
