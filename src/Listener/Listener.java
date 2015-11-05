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
 * @author Alberto Meana, Andrés Ortiz
 */
public class Listener extends SingleAgent{
    //Listener (this) agent name
    private String listenerName="Listener";
    //Controller name
    private String controllerName="Controller";
    private ACLMessage result, in, out;
    JsonObject key, answer, msg;
    
    
    public Listener(AgentID aid) throws Exception {
        
        super(aid);
        this.result = new ACLMessage();
        this.in = new ACLMessage();
        this.out = new ACLMessage();
        this.key = new JsonObject();
        this.answer = new JsonObject();
        this.msg = new JsonObject();
        
    }
    
    /**
     * Función de ejecución de la hebra.
     * Realiza el test de login y logout.
     * 
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
}
