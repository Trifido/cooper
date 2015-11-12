package Controller;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;
import GUI.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * Clase Controller
 * Recibe mensajes del agente Listener y realiza la toma de decisiones
 * 
 * @author Alba Ríos and Vicente Martínez
 */
public class Controller extends SingleAgent{
    
    private int battery;
    private double minValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Integer[][] world;
    
    private ACLMessage out;
    JsonObject key, answer, msg;
    
    Interface gui;
    
    /**
     * Constructor del Agente Controller.
     * 
     * @param aid ID del agente para Magentix.
     * @throws Exception Error en la creación.
     * @author Alba Rios
     */
    public Controller(AgentID aid) throws Exception {
        super(aid);
        this.battery = 0;
        this.msg= new JsonObject();
        this.key= new JsonObject();
        this.minValue= Double.NEGATIVE_INFINITY;
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Integer[500][500];
        
        // Inicializar el mapa completo a valor "desconocido"
        /*for (Pair[] row : this.world){
            for (Pair p : row){
                
                p = new Pair( Square.UNKNOWN, 1 );
                
            }
        }*/
        
        for(int i=0; i<500; i++)
            for(int j=0; j<500; j++)
                this.world[i][j]= 1;

        
        // Inicializacion de la interfaz:
        // ------------------------------------------------------------
        // Alberto dice: "El frame se debe escalar al modelo del mapa
        // @see Frame.java | Interface.java
        this.gui = new Interface( 500,500 );
        
        // ------------------
        // TESTING
        this.gui.paint( 40,40,Color.RED );
        this.gui.paint( 10,10,Color.RED );
        this.gui.paint( 20,25,Color.BLUE );
        // ------------------
        
    }
    
    //public 
    /**
     * Función encargada de obtener el beneficio de cada casilla.
     * 
     * @param i
     * @param j
     * @return 
     * @author Vicente Martínez
     */
    public double getBenefit(int i, int j){
        if (i == 1 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second - 1];
        else if (i == 1 && j == 2)
            return this.scanner[i][j] * this.world[gps.first][gps.second - 1];
        else if (i == 1 && j == 3) 
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second - 1];
        else if (i == 2 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second];
        else if (i == 2 && j == 3)
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second];
        else if (i == 3 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second + 1];
        else if (i == 3 && j == 2)
            return this.scanner[i][j] * this.world[gps.first][gps.second + 1];
        else
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second + 1];
    }
    
    /**
     * Función encargada de establecer la acción elegida y guardar las visitas
     * realizadas en el mapa "world".
     * 
     * @param npos
     * @return 
     * @author Vicente Martínez
     */
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
     
    public void mostrarRadar(){
        for(int i=1; i<4; i++){
            for(int j=1; j<4; j++){
                System.out.print(radar[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * Función encargada de elegir la mejor acción posible a partir de un 
     * algorítmo Greedy.
     * @return 
     * @author Vicente Martínez
     */
    public String Heuristic(){
        //if(true)
        //    return "moveS";
        
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        
        if(this.battery < 2){
            System.out.println("REFUEL");
            return "REFUEL";
        }
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2){
            System.out.println("FOUND");
            return "FOUND";
        }
        else{
            mostrarRadar();
            for(int i=1; i<4; i++){
                for(int j=1; j<4; j++){
                    if((i!=2 || j!=2) && radar[i][j] != 1){ 
                        benefit= getBenefit(i, j);
                        if(this.minValue > benefit){
                            this.minValue= benefit;
                            newpos.first= i;
                            newpos.second= j;
                        }
                    }
                    else {
                        System.out.println("OBSTACULO en ["+i+"]["+j+"]");
                        world[i][j]= -1;
                    }
                }
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
    public void sendAction(String action)
    {
        // Composición del Json de sendLocalization
        System.out.println("Key: " + this.key.get( "result" ).toString());
        
        this.msg = Json.object().add( "command", action );
        this.msg.add( "key", this.key.get( "result" ).asString() );
        
       
        
        // Creación del ACL
        this.out = new ACLMessage();
        this.out.setSender( this.getAid() );
        this.out.setReceiver( new AgentID( "Furud" ) );
        
        this.out.setContent( this.msg.toString() );
        
        this.send( out );
    }
    
    /**
     * Espera a la recepción de un objeto JSON del listener
     * 
     * @author Andrés Ortiz
     * @throws java.lang.InterruptedException
     * 
     */
    public void receiveMessage() throws InterruptedException{
      
       ACLMessage in = new ACLMessage();
       in = this.receiveACLMessage();
       JsonObject message= JsonObject.readFrom(in.getContent());
       battery= (int) message.getFloat("battery", -1); //in case of problem, battery is one
       JsonObject gpsObject=message.get("gps").asObject();
       gps.first=(int) gpsObject.getFloat("x",gps.first);
       gps.second=(int) gpsObject.getInt("y",gps.second);
       JsonArray rad=message.get("radar").asArray();
       int j=0,i2=0;
       for(int i=0;i<rad.size();i++){
           radar[j][i2]=(int)rad.get(i).asFloat();
           i2++;
           if(i2==5){
               j++;
               i2=0;
           }
       }
       
       JsonArray scan=message.get("scanner").asArray();
       j=0;
       i2=0;
       for(int i=0;i<scan.size();i++){
           scanner[j][i2]=scan.get(i).asFloat();
           i2++;
           if(i2==5){
               j++;
               i2=0;
           }
       }
    }
    /**
     * Espera a la recepción de un objeto JSON del listener
     * 
     * @author Andrés Ortiz
     * @throws java.lang.InterruptedException
     * 
     */
    private void receiveKey() throws InterruptedException{
        
        ACLMessage in = new ACLMessage();
        in = this.receiveACLMessage();
        //JsonObject message= JsonObject.readFrom(in.getContent());
        //key=message.get("result").asObject(); 
        
        this.key = Json.parse( in.getContent() ).asObject(); 
        
    }
    
    /**
     * Recibir el check de continuar o finalizar la ejecución.
     * @author Vicente Martínez
     */
    private boolean receiveCheck() throws InterruptedException{
        ACLMessage check = new ACLMessage();
        check = this.receiveACLMessage();
        
        JsonObject message= JsonObject.readFrom(check.getContent());
        String var= message.get("check").asString();
        
        if(var.equals("continue"))
            return false;
        else
            return true;
    }
    
    
    @Override
    public void execute(){
        boolean fin= false;
        
        while(!fin){ 
            try {
                // Recojo la Key y la guardo.
                this.receiveKey();
                System.out.println( "Ha funcionado la recepcion de la key en el controller! " );
                fin= this.receiveCheck();
                System.out.println( "Ha funcionado el check en el controller! " );
                // Recibo los sensores.
                if(!fin)
                    receiveMessage();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String aux = Heuristic();
            
            System.out.println("ACTION: " + aux);
            
            sendAction(aux);
            
            
            System.out.println( "Ha funcionado el controller! " ); 
        }
    }
}
