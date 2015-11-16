package mapproject;

import static helpers.Artist.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/**
 * Para que corra en Netbeans con Windows:
 * ==>Right click on the Project
 * ==>Properties
 * ==>Click on RUN
 * ==>VM Options : -Djava.library.path="C:\Your Directory where Dll is present"
 * ==>Ok
 * También están añadidos los natives de Linux
 * 
 * @author Alba Ríos
 */
public class MapProject {
    public static int ZOOM = 1;
    public static final int ZOOMSPEED = 2;
    public static final int MINZOOM = 1, MAXZOOM = 16;
    
    /**
     * Constructor que crea el mapa y la interfaz
     * 
     * @author Alba Ríos
     */
    public MapProject(){
        
        BeginSession();
        
        int[][] prueba = {
            {2,0,0,0,2},
            {0,1,0,1,0},
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,0,0,0,0}
        };
        
        TileGrid grid = new TileGrid(prueba);

        //Prueba
        /*.setTile(100, 100, TileType.Dirt);
        grid.setTile(101, 100, TileType.Dirt);
        grid.setTile(102, 100, TileType.Dirt);
        grid.setTile(103, 100, TileType.Dirt);
        grid.setTile(104, 100, TileType.Dirt);
        grid.setTile(105, 100, TileType.Dirt);
        grid.setTile(106, 100, TileType.Dirt);
        grid.setTile(107, 100, TileType.Dirt);
        grid.setTile(108, 100, TileType.Dirt);
        grid.setTile(109, 100, TileType.Dirt);
        grid.setTile(110, 100, TileType.Dirt);*/
        
        while(!Display.isCloseRequested()){
            grid.Draw();
            Display.update();
            Display.sync(60);
              
            //Interacción con el MOUSE
            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) zoomOut();
            else if (dWheel > 0) zoomIn();
        }
        
        Display.destroy(); //Cerrar la interfaz
    }
    
    /**
     * Método que aplica la ampliación de zoom
     * 
     * @author Alba Ríos
     */
    public final void zoomIn(){
        if (ZOOM < MAXZOOM){
            ZOOM *= ZOOMSPEED;
            applyZoom(ZOOM);
        }
    }
    
    /**
     * Método que aplica la reducción de zoom
     * 
     * @author Alba Ríos
     */
    public final void zoomOut(){
        if (ZOOM > MINZOOM){
            ZOOM /= ZOOMSPEED;
            applyZoom(ZOOM);
        }
    }
    
    public static void main(String[] args) {
        new MapProject();
    }
    
}
