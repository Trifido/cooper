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
import javax.swing.JFrame;
import mapproject.MapProject;
import mapproject.TileType;

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
    private final String listenerName;
    private final String controllerName;
    private final String worldToSolve;
    private HeuristicaBasica heuristica;
    
    private ACLMessage out;
    private ACLMessage result;
    private JsonObject key, answer, msg;
    
    public MapProject gui;
    public WorldDialog ask;
    
    /**
     * Constructor del Agente Controller.
     * 
     * @param aid ID del agente para Magentix.
     * @param nameListener Nombre del Listener en string definido en el main.
     * @param nameController Nombre del Controller en string definido en el main.
     * @param map Referncia a la interfaz creada en el main para modificación a datos.
     * @throws Exception Error en la creación.
     * @author Alba Rios, Alberto Meana
     */
    public Controller(AgentID aid, String nameListener, String nameController,MapProject map) throws Exception {
        
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
        this.controllerName = nameController;
        this.out = new ACLMessage();
        this.result = new ACLMessage();
        this.heuristica= new HeuristicaBasica();
        
        for(int i=0; i<500; i++)
            for(int j=0; j<500; j++)
                this.world[i][j]= 1;

       // Inicializacion de la interfaz:
        this.worldToSolve = (new WorldDialog( new JFrame(), true) ).getWordl();
        System.out.println(this.worldToSolve);
        this.gui = map;
        
        this.heuristica.actualizarSensores(battery, gps, radar, scanner);
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
     * Pintado en interfaz.
     * 
     * 
     * ( POR IMPLEMENTAR : EL BOT )
     * 
     * @author Alberto Meana
     */
    public void paint(){
        
        for( int i = 0; i < 5; i++ ){
            for( int j = 0; j < 5; j++ ){
                
                // Interfaz nueva
                if( this.gps.first + ( i-2 ) >= 0 && this.gps.second + ( j-2 ) >= 0 && this.gps.first + ( i-2 ) < 500 && this.gps.second + ( j-2 ) < 500 ){
                    
                    switch( this.radar[j][i] ){

                    case 0:
                        this.gui.grid.setTile( this.gps.first + ( i-2 ), this.gps.second + (j-2), TileType.Grass );
                        break;
                    case 1:
                        this.gui.grid.setTile( this.gps.first + ( i-2 ), this.gps.second + (j-2), TileType.Rock );
                        break;
                    case 2:    
                        if( i == 2 && j == 2 ){

                            //this.gui.grid.setTile( this.gps.first, this.gps.second, TileType.Goal );

                        }else{

                            this.gui.grid.setTile( this.gps.first + ( i-2 ), this.gps.second + (j-2), TileType.Goal );

                        }
                        break;
                    }
                }
            }
        }
        // El bot
        this.gui.grid.setTile( this.gps.first, this.gps.second, TileType.Bot );
        
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
       
       this.heuristica.actualizarSensores(battery, gps, radar, scanner);  
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
         
            this.paint();
            String aux= this.heuristica.heuristic();
            
            
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
        System.out.println( "CONTROLLER: HE MUERTO!");
        
    }
}
