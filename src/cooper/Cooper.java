package cooper;

import Controller.Controller;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import Listener.*;
import es.upv.dsic.gti_ia.core.AgentID;
import mapproject.MapProject;

/**
 *
 * @author Alberto Meana
 */
public class Cooper {

    /**
     * @param args the command line arguments
     *
     * @author Alberto Meana
     */
    public static void main(String[] args) throws Exception {
        
        String nameListener = "Listener";
        String nameController = "Controller";
        
        MapProject map = new MapProject();
        
        AgentsConnection.connect("isg2.ugr.es", 6000, "Furud", "Canmaior", "Ishiguro", false);
        Listener listener = new Listener( new AgentID( nameListener ),nameListener,nameController );
        Controller controller = new Controller( new AgentID( nameController ),nameListener,nameController,map );
        
        //map.startInterface();
        listener.start();
        controller.start();
        
        map.startInterface();
    }
    
}
