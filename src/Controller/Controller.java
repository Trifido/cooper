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
    private double maxValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private int[][] world;
    
    public Controller(AgentID aid) throws Exception {
        super(aid);
        this.battery = 0;
        this.maxValue= Double.NEGATIVE_INFINITY;
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
    
    public double getBenefit(int i, int j){
        if (i == 1 && j == 1)
            return this.scanner[i][j]/this.world[gps.first - 1][gps.second - 1];
        else if (i == 1 && j == 2)
            return this.scanner[i][j]/this.world[gps.first][gps.second - 1];
        else if (i == 1 && j == 3) 
            return this.scanner[i][j]/this.world[gps.first + 1][gps.second - 1];
        else if (i == 2 && j == 1)
            return this.scanner[i][j]/this.world[gps.first - 1][gps.second];
        else if (i == 2 && j == 3)
            return this.scanner[i][j]/this.world[gps.first + 1][gps.second];
        else if (i == 3 && j == 1)
            return this.scanner[i][j]/this.world[gps.first - 1][gps.second + 1];
        else if (i == 3 && j == 2)
            return this.scanner[i][j]/this.world[gps.first][gps.second + 1];
        else
            return this.scanner[i][j]/this.world[gps.first + 1][gps.second + 1];
    }
    
    public String nextAction(Pair<Integer, Integer> npos){
        String act= new String();
        
        if (npos.first == 1 && npos.second == 1){
            act = "moveNW";
            this.world[gps.first - 1][gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 2){
            act = "moveN";
            this.world[gps.first][gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 3){
            act = "moveNE";
            this.world[gps.first + 1][gps.second - 1]++;
        }
        else if (npos.first == 2 && npos.second == 1){
            act = "moveW";
            this.world[gps.first - 1][gps.second]++;
        }
        else if (npos.first == 2 && npos.second == 3){
            act = "moveE";
            this.world[gps.first + 1][gps.second]++;
        }
        else if (npos.first == 3 && npos.second == 1){
            act = "moveSW";
            this.world[gps.first - 1][gps.second + 1]++;
        }
        else if (npos.first == 3 && npos.second == 2){
            act = "moveS";
            this.world[gps.first][gps.second + 1]++;
        }
        else{
            act = "moveSE";
            this.world[gps.first + 1][gps.second + 1]++;
        }
        
        return act;
    }
     
    public String Heuristic(){
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.maxValue= Double.NEGATIVE_INFINITY;
        double benefit;
        
        if(this.battery < 2)
            return "REFUEL";
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2)
            return "FOUND";
        else{
            for(int i=1; i<4; i++)
                for(int j=1; j<4; j++)
                    if((i!=2 && j!= 2) && radar[i][j] != 1){ 
                        benefit= getBenefit(i, j);
                        if(this.maxValue < benefit){
                            this.maxValue= benefit;
                            newpos.first= i;
                            newpos.second= j;
                        }
                    }
                    else if(i!=2 && j!= 2){
                        world[i][j]= -1;
                    }
            
            return nextAction(newpos);
        }
    }
    
    /**
     * Función que envía al servidor la nueva localización
     * 
     * @author Rafael Ruiz
     * @param localization Nueva posición para el agente
     */
    public void sendLocalization(String localization)
    {
        
    }
    
    @Override
    public void execute(){
        
    }
    
}
