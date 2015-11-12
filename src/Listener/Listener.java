package Listener;

import com.eclipsesource.json.*;
import es.upv.dsic.gti_ia.core.*;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * 
 * Clase listener, de momento implementa el login y el logout
 * Convenio:
 *  -Nombres de variables en ¡INGLES!
 *  -Variables propias de la clase con ¡THIS.!
 * 
 * @author Alberto Meana, Andrés Ortiz, Nikolai González
 */
public class Listener extends SingleAgent{
    //Listener (this) agent name
    private String listenerName="Listenerr";
    //Controller name
    private String controllerName="Controllerrr";
    private ACLMessage result, in, out;
    JsonObject key, answer, msg, mensaRecibido;
    ArrayList <JsonObject> mensajes;
    int contador = 0;
    boolean recibidos = false;
    boolean endConnection = false;
    
    /**
     * Constructor del agente Listener
     * 
     * @param aid ID del agente para Magentix
     * @throws Exception Error de creación
     * @author Alberto Meana
     */
    public Listener(AgentID aid) throws Exception {
        
        super(aid);
        this.result = new ACLMessage();
        this.in = new ACLMessage();
        this.out = new ACLMessage();
        this.key = new JsonObject();
        this.answer = new JsonObject();
        this.msg = new JsonObject();
        this.mensajes = new ArrayList();
        
    }
    
    /**
     * Función de ejecución de la hebra.
     * Realiza el test de login y logout.
     * 
     * @author Alberto Meana
     */
    @Override
    public void execute(){
        
        // INIT TEST EXECUTION
        ////////////////////////////////////////////////////////////////////////
        
        // Logear
        this.login();
        
        // Recibir result
        this.result = null;
        
        System.out.println( "\nMensaje de login enviado" );
        try {
            
            this.result = this.receiveACLMessage();
        
        } catch (InterruptedException ex) {
        
            Logger.getLogger( Listener.class.getName( ) ).log( Level.SEVERE, null, ex );
        
        }
        
        System.out.println( "Mensaje de respuesta de login recibido" );
        
        // Imprimir result por consola para ver la key o el BAD_*
        this.key = Json.parse( this.result.getContent() ).asObject();
        System.out.println( this.key.get( "result" ) );
        
        // Mandar key al controller.
        sendKey(this.key);
        
        
        // Escuchar sensores y enviar resultados
        //while( !this.endConnection )
            escucharMensajes();
        
        
        

        // Deslogear
            /*  IMPORTANTISIMO EN LOS PARSINGS DE JSON  */
        /* toString() == "blabla" <-- ojo comillas!!    */
        /* asString() == blabla   <-- no hay comilas!!  */
        
        logout( this.key.get( "result" ).asString() );
        System.out.println( "Mensaje de logout enviado" );
        
        this.result = null;
        try {
            
            this.result = this.receiveACLMessage();
        
        } catch ( InterruptedException ex ) {
        
            Logger.getLogger( Listener.class.getName() ).log( Level.SEVERE, null, ex );
        
        }
        
        System.out.println( "Mensaje de confirmación de logout recibido" );
        
        this.answer = Json.parse( this.result.getContent() ).asObject();
        System.out.println( answer.get( "result" ) );
        
        // END TEST EXECUTION
        ////////////////////////////////////////////////////////////////////////
        
    }
    
    
    /**
     * Función para logearse en el sistema con el controlador
     * @author Alberto Meana
     */
    private void login(){
    
        //Composición de Json de logeo.
        JsonObject msg = Json.object().add( "command","login" );
        msg.add( "world","map1" );
        msg.add( "radar", this.listenerName );
        msg.add( "scanner", this.listenerName );
        msg.add( "battery", this.listenerName );
        msg.add( "gps", this.listenerName );
        /* ... Poner sensores y tal ... */
        
        // Creación del ACL
        ACLMessage out = new ACLMessage();
        out.setSender( this.getAid() );
        out.setReceiver( new AgentID( "Furud" ) );
        
        out.setContent( msg.toString() );
        
        this.send( out );
        
    }
    
    /**
     * Función que desloguea al bot del sistema
     * 
     * @author Alberto Meana
     * @param key String con la key k usa el controlador para autenticar.
     */
    private void logout( String key ){
    
        // Composición del Json de logout
        this.msg = Json.object().add( "command","logout" );
        this.msg.add( "key", key );
        
        // Creación del ACL
        this.out = new ACLMessage();
        this.out.setSender( this.getAid() );
        this.out.setReceiver( new AgentID( "Furud" ) );
        
        this.out.setContent( this.msg.toString() );
        
        this.send( out );
    }
    
    /**
     * Une y Redirecciona las respuestas de los sensores al agente controler
     * 
     * @author Andrés Ortiz
     * @param sensorResponse Arraylist de respuestas de los sensores
     */
    private void redirectResponses(ArrayList<JsonObject> sensorResponse){
        JsonObject response=new JsonObject();
        for(JsonObject obj : sensorResponse){
            response.merge(obj);
        }
        
        ACLMessage out = new ACLMessage();
        out.setSender(this.getAid());
        out.setReceiver(new AgentID(controllerName));
        out.setContent(response.toString());
        
        this.send(out);
    }
    /**
     * Función para escuchar los mensajes y guardarlos en un Array de JsonObject 
     * 
     * @author Nikolai González
     */
    private void escucharMensajes(){
        String entero, separador;
        try {
            while( (contador < 4) && !endConnection )
            {
                in = this.receiveACLMessage();
                System.out.println("\nRecibido mensaje <"+in.getContent());
                if(in.getContent().contains("CRASHED"))
                {
                    endConnection=true;
                }
                mensaRecibido = Json.parse( this.in.getContent() ).asObject();
                mensajes.add(mensaRecibido);
                contador++;
            }
            if(!endConnection)
            { 
                // mensajes.add(key);
                recibidos = true;
                redirectResponses(mensajes);
                contador = 0;
            }
            
        } catch (InterruptedException ex) {
            System.out.println("Fallo en la recepción de mensajes.");
            //si da error se desloguea???
            //logout( this.key.get( "result" ).asString() );
        }
        
    }
    
     /**
     * Envia la key a controller
     * 
     * @author Andrés Ortiz
     * @param key clave de conexión
     */
    private void sendKey(JsonObject key){
        
        ACLMessage out = new ACLMessage();
        
        out.setSender(this.getAid());
        out.setReceiver(new AgentID(controllerName));
        out.setContent(key.toString());
        
        this.send(out);
    }
    
}
