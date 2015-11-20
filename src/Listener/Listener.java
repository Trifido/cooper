package Listener;

import com.eclipsesource.json.*;
import es.upv.dsic.gti_ia.core.*;
import java.util.ArrayList;

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

    private final String listenerName;
    private final String controllerName;
    private ACLMessage result, in, out;
    JsonObject key, answer, msg, messageReceived;
    ArrayList <JsonObject> messages;
    int cont;
    boolean received;
    boolean endConnection;
    
    /**
     * Constructor del agente Listener
     * 
     * @param aid ID del agente para Magentix
     * @param listenerName Nombre del Listener en string definido en el main.
     * @param controllerName Nombre del Controller en string definido en el main.
     * @throws Exception Error de creación
     * @author Alberto Meana
     */
    public Listener( AgentID aid, String listenerName, String controllerName ) throws Exception {
        
        super(aid);
        this.cont = 0;
        this.result = new ACLMessage();
        this.in = new ACLMessage();
        this.out = new ACLMessage();
        this.key = new JsonObject();
        this.answer = new JsonObject();
        this.msg = new JsonObject();
        this.messageReceived = new JsonObject();
        this.messages = new ArrayList();
        this.listenerName = listenerName;
        this.controllerName = controllerName;
        this.received = false;
        this.endConnection = false;
        
    }
    
    /**
     * Función de ejecución de la hebra.
     * Realiza el test de login y logout.
     * 
     * @author Alberto Meana
     */
    @Override
    public void execute(){
        
        ////////////////////////////////////////////////////////////////////////
        // INIT EXECUTION LISTENER
        
        while( !this.endConnection ){
            
            System.out.println( "LISTENER: Escuchando...");
            this.hearMessages();
        
        }
        
        // MATAR AL LISTENER!!!
        System.out.println( "LISTENER: HE MUERTO!");
        ////////////////////////////////////////////////////////////////////////
        // END EXECUTION LISTENER
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
    private void hearMessages(){

        try {
            while( ( this.cont < 4) && !endConnection )
            {
                in = this.receiveACLMessage();
                
                if((in.getContent().contains("CRASHED")) || (in.getContent().contains("FOUND")))
                {
                    endConnection=true;
                    this.cont = 0;
                }
                else{
                    this.messageReceived = Json.parse( this.in.getContent() ).asObject();
                    this.messages.add(this.messageReceived);
                    this.cont++;
                }
            }
            if(!endConnection)
            { 
                this.received = true;
                this.redirectResponses(this.messages);
                this.cont = 0;
            }
            
        } catch (InterruptedException ex) {
            System.out.println("lISTENER: Fallo en la recepción de mensajes.");

        }
        
    }
}
