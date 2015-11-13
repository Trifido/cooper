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
    //Listener (this) agent name
    private String listenerName;
    //Controller name
    private String controllerName;
    private ACLMessage result, in, out;
    JsonObject key, answer, msg, mensaRecibido;
    ArrayList <JsonObject> mensajes;
    int contador;
    boolean recibidos;
    boolean endConnection;
    
    /**
     * Constructor del agente Listener
     * 
     * @param aid ID del agente para Magentix
     * @throws Exception Error de creación
     * @author Alberto Meana
     */
    public Listener( AgentID aid, String listenerName, String controllerName ) throws Exception {
        
        super(aid);
        this.contador = 0;
        this.result = new ACLMessage();
        this.in = new ACLMessage();
        this.out = new ACLMessage();
        this.key = new JsonObject();
        this.answer = new JsonObject();
        this.msg = new JsonObject();
        this.mensajes = new ArrayList();
        this.listenerName = listenerName;
        this.controllerName = controllerName;
        this.recibidos = false;
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
            escucharMensajes();
        }
        
        // MATAR AL LISTENER!!!
        
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
     * Metodo para enviar comprobaciones ante choques y finalización.
     * @author Vicente Martínez
     */
    private void sendCheck(String var){
        JsonObject check=new JsonObject();
        
        check.add("check", var);
        
        ACLMessage out = new ACLMessage();
        out.setSender(this.getAid());
        out.setReceiver(new AgentID(controllerName));
        out.setContent(check.toString());
        
        this.send(out);
    }
    
    /**
     * Función para escuchar los mensajes y guardarlos en un Array de JsonObject 
     * 
     * @author Nikolai González
     */
    private void escucharMensajes(){

        try {
            while( (contador < 4) && !endConnection )
            {
                in = this.receiveACLMessage();
                
                if((in.getContent().contains("CRASHED")) || (in.getContent().contains("FOUND")))
                {
                    endConnection=true;
                    contador = 0;
                }
                else{
                    mensaRecibido = Json.parse( this.in.getContent() ).asObject();
                    mensajes.add(mensaRecibido);
                    contador++;
                }
            }
            if(!endConnection)
            { 
                recibidos = true;
                redirectResponses(mensajes);
                contador = 0;
            }
            else{
                //sendCheck("finish");
            }
            
        } catch (InterruptedException ex) {
            System.out.println("lISTENER: Fallo en la recepción de mensajes.");
            //si da error se desloguea???
            //logout( this.key.get( "result" ).asString() );
        }
        
    }
}
