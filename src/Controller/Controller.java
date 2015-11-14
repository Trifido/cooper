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
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * Clase Controller
 * Recibe mensajes del agente Listener y realiza la toma de decisiones
 * 
 * @author Alba Ríos, Vicente Martínez, Alberto Meana
 */
public class Controller extends SingleAgent{
    
    private int battery;
    private double minValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Integer[][] world;
    private String listenerName;
    private String controllerName;
    private String worldToSolve;
    
    private ACLMessage out;
    private ACLMessage result;
    private JsonObject key, answer, msg;
    
    public Interface gui;
    public WorldDialog ask;
    
    /**
     * Constructor del Agente Controller.
     * 
     * @param aid ID del agente para Magentix.
     * @param nameListener 
     * @param nameController
     * @throws Exception Error en la creación.
     * @author Alba Rios, Alberto Meana
     */
    public Controller(AgentID aid, String nameListener, String nameController) throws Exception {
        
        super(aid);
        this.battery = 0;
        this.msg= new JsonObject();
        this.key= new JsonObject();
        this.minValue= Double.NEGATIVE_INFINITY;
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Integer[500][500];
        this.listenerName = nameListener;
        this.controllerName = controllerName;
        this.out = new ACLMessage();
        this.result = new ACLMessage();
        
        // Inicializar el mapa completo a valor "desconocido"
        /*for (Pair[] row : this.world){
            for (Pair p : row){
                
                p = new Pair( Square.UNKNOWN, 1 );
                
            }
        }*/
        
        for(int i=0; i<500; i++)
            for(int j=0; j<500; j++)
                this.world[i][j]= 1;

       
        this.worldToSolve = "map" + (new WorldDialog( new JFrame(), true) ).getWordl();
        System.out.println( worldToSolve );
        // Inicializacion de la interfaz:
        // ------------------------------------------------------------
        // Alberto dice: "El frame se debe escalar al modelo del mapa"
        // @see Frame.java | Interface.java
        this.gui = new Interface( 500,500 );
        
    }
    
    /**
     * Función para logearse en el sistema con el controlador
     * 
     * @author Alberto Meana
     */
    private void login(){
    
        //Composición de Json de logeo.
        this.msg = Json.object().add( "command","login" );
        this.msg.add( "world" ,this.worldToSolve );
        this.msg.add( "radar", this.listenerName );
        this.msg.add( "scanner", this.listenerName );
        this.msg.add( "battery", this.listenerName );
        this.msg.add( "gps", this.listenerName );
        
        // Creación del ACL
        this.out.setSender( this.getAid() );
        this.out.setReceiver( new AgentID( "Furud" ) );
        
        this.out.setContent( msg.toString() );
        
        this.send( out );
        
    }
    
    /**
     * Función que desloguea al bot del sistema
     * 
     * @author Alberto Meana
     */
    private void logout(){
    
        // Composición del Json de logout
        
            /*  IMPORTANTISIMO EN LOS PARSINGS DE JSON  */
        /* toString() == "blabla" <-- ojo comillas!!    */
        /* asString() == blabla   <-- no hay comilas!!  */
        
        this.msg = Json.object().add( "command","logout" );
        this.msg.add( "key", this.key.get( "result" ).asString() );
        
        // Creación del ACL
        this.out = new ACLMessage();
        this.out.setSender( this.getAid() );
        this.out.setReceiver( new AgentID( "Furud" ) );
        
        this.out.setContent( this.msg.toString() );
        
        this.send( out );
    }
    
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
        
        String act;
        
        if (npos.first == 1 && npos.second == 1){
            act = "moveNW";
            this.world[this.gps.first - 1][this.gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 2){
            act = "moveN";
            this.world[this.gps.first][this.gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 3){
            act = "moveNE";
            this.world[this.gps.first + 1][this.gps.second - 1]++;
        }
        else if (npos.first == 2 && npos.second == 1){
            act = "moveW";
            this.world[this.gps.first - 1][this.gps.second]++;
        }
        else if (npos.first == 2 && npos.second == 3){
            act = "moveE";
            this.world[this.gps.first + 1][this.gps.second]++;
        }
        else if (npos.first == 3 && npos.second == 1){
            act = "moveSW";
            this.world[this.gps.first - 1][this.gps.second + 1]++;
        }
        else if (npos.first == 3 && npos.second == 2){
            act = "moveS";
            this.world[this.gps.first][this.gps.second + 1]++;
        }
        else{
            act = "moveSE";
            this.world[this.gps.first + 1][this.gps.second + 1]++;
        }
        
        return act;
    }
    
    /**
     * Pintado en interfaz.
     * 
     * ROJO = Vacio
     * NEGRO = Pared
     * VERDE = Objetivo
     * BLUE = Bot
     * 
     * ( POR IMPLEMENTAR : EL BOT )
     * 
     * @author Alberto Meana
     */
    public void paint(){
        
        for( int i = 0; i < 5; i++ ){
            for( int j = 0; j < 5; j++ ){
            
                if( this.gps.first + ( i-2 ) >= 0 && this.gps.second + ( j-2 ) >= 0 && this.gps.first + ( i-2 ) < 500 && this.gps.second + ( j-2 ) < 500 ){
                    switch( this.radar[i][j] ){

                        case 0:
                            this.gui.paint( this.gps.first + ( i-2 ), this.gps.second + (j-2), Color.RED );
                            break;
                        case 1:
                            this.gui.paint( this.gps.first + ( i-2 ), this.gps.second + (j-2), Color.BLACK );
                            break;
                        case 2:    
                            if( i == 2 && j == 2 ){

                                this.gui.paint( this.gps.first, this.gps.second, Color.ORANGE );

                            }else{

                                this.gui.paint( this.gps.first + ( i-2 ), this.gps.second + (j-2), Color.GREEN );

                            }
                            break;
                    }
                }
            }
        }
        // El bot
        if( this.radar[2][2] != 2 )
            this.gui.paint( this.gps.first, this.gps.second, Color.BLUE );
        
    }
    
    /**
     * Función encargada de elegir la mejor acción posible a partir de un 
     * algorítmo Greedy.
     * @return 
     * @author Vicente Martínez
     */
    public String heuristic(){
        
        this.paint();
        
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        
        if(this.battery < 2){
            System.out.println("CONTROLLER: REFUEL");
            return "refuel";
        }
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2){
            System.out.println("CONTROLLER: FOUND");
            return "found";
        }
        else{
            for(int i=1; i<4; i++){
                for(int j=1; j<4; j++){
                    if((i!=2 || j!=2) && this.radar[i][j] != 1){ 
                        benefit= getBenefit(i, j);
                        if(this.minValue > benefit){
                            this.minValue= benefit;
                            newpos.first= i;
                            newpos.second= j;
                        }
                    }
                    else if (i!=2 || j!=2){
                        System.out.println("CONTROLLER: OBSTACULO en ["+i+"]["+j+"]");
                        this.world[i][j]= -1;
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
     * @param action
     */
    public void sendAction(String action)
    {
        
        // Composición del Json de sendLocalization

        this.msg = Json.object().add( "command", action );
        this.msg.add( "key", this.key.get( "result" ).asString() );

        // Creación del ACL
        this.out = new ACLMessage();
        this.out.setSender( this.getAid() );
        this.out.setReceiver( new AgentID( "Furud" ) );

        this.out.setContent( this.msg.toString() );

        this.send( out );

        try {
            this.out = receiveACLMessage();
        } catch (InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println( "CONTROLLER: " + out.getContent() );
        
    }
    
    /**
     * Espera a la recepción de un objeto JSON del listener
     * 
     * @author Andrés Ortiz
     * @throws java.lang.InterruptedException
     * 
     */
    public void receiveMessage() throws InterruptedException{
      
       JsonObject message = Json.parse( this.receiveACLMessage().getContent() ).asObject();
       
       this.battery= (int) message.getFloat("battery", -1); //in case of problem, battery is one

       JsonObject gpsObject=message.get("gps").asObject();
       this.gps.first= gpsObject.getInt("x",gps.first);
       this.gps.second= gpsObject.getInt("y",gps.second);
       
       JsonArray rad=message.get("radar").asArray();
       int j=0,i2=0;
       for(int i=0;i<rad.size();i++){
           this.radar[j][i2] = rad.get(i).asInt();
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
           this.scanner[j][i2]=scan.get(i).asFloat();
           i2++;
           if(i2==5){
               j++;
               i2=0;
           }
       }
    }
    
    /**
     * Función de ejecución del flujo del controller.
     * Se logea, resuelve la lógica, escucha al listener y se desloguea
     * 
     * @author Alberto Meana
     */
    @Override
    public void execute(){
        
        ////////////////////////////////////////////////////////////////////////
        // LOGIN Y KEY
        this.login();
        
        // Recibir result
        this.result = null;
        
        System.out.println( "\n CONTROLLER: Mensaje de login enviado" );
        try {
            
            this.result = this.receiveACLMessage();
        
        } catch (InterruptedException ex) {
        
            Logger.getLogger( Controller.class.getName( ) ).log( Level.SEVERE, null, ex );
        
        }
        
        System.out.println( "CONTROLLER: Mensaje de respuesta de login recibido" );
        
        // Guardar la key que me envia en el result
        this.key = Json.parse( this.result.getContent() ).asObject();
        System.out.println( "CONTROLLER: " + this.key.get( "result" ) );
        
        ////////////////////////////////////////////////////////////////////////
        // ACCIONES PARA RESOLVER MAPA
        
        while(true){ 
            try {
                
                this.receiveMessage();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
         
            String aux = this.heuristic();
            
            System.out.println("CONTROLLER: ACTION: " + aux);
            
            if( aux.equals( "found" ) ){
                JsonObject response=new JsonObject();
                
                response= Json.object().add( "action","FOUND" );
                
                ACLMessage out = new ACLMessage();
                out.setSender(this.getAid());
                out.setReceiver(new AgentID(listenerName));
                out.setContent(response.toString());
        
                this.send(out);
                break;
            
            }else{
            
                this.sendAction(aux);
                
            }
        }
        ////////////////////////////////////////////////////////////////////////
        // LOGOUT CONTROLLER
        this.logout();
        System.out.println( "CONTROLLER: Mensaje de logout enviado" );

        // Recepción del mensaje de deslogeo
        this.result = null;
        try {

            this.result = this.receiveACLMessage();

        } catch ( InterruptedException ex ) {

            Logger.getLogger( Controller.class.getName() ).log( Level.SEVERE, null, ex );

        }

        System.out.println( "CONTROLLER: Mensaje de confirmación de logout recibido" );

        this.answer = Json.parse( this.result.getContent() ).asObject();
        System.out.println( "CONTROLLER: " + answer.get( "result" ) );

        ////////////////////////////////////////////////////////////////////////
        // MATAR AL CONTROLLER Y ENVIAR MUERTE AL LISTENER
        
    }
}
