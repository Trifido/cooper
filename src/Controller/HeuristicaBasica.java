package Controller;

/**
 * 
 * Clase Heurística Básica
 * 
 * Encargada de establecer la acción elegida
 * 
 * @author Vicente Martínez and Rafael Ruiz
 * 
 */
public class HeuristicaBasica {

    private int battery;
    private double minValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Integer[][] world;
    private double []minObstacle;
    private double []finalPoint;
    private String actionAnterior;
    private double minValueFind;
    private boolean upMVF;
    private boolean initHeu2;
    
    public HeuristicaBasica(){
        minObstacle= new double[2];
        finalPoint= new double[2];
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Integer[500][500];
        this.minValueFind= Double.POSITIVE_INFINITY;
        this.upMVF= true;
        this.initHeu2= true;
        actionAnterior="";
        
        for(int i=0; i<500; i++)
            for(int j=0; j<500; j++)
                this.world[i][j]= 1;
    }
    
    
    public void actualizarSensores(int bat, Pair<Integer, Integer> gps, int[][] radar, double[][] scanner){
        this.battery= bat;
        this.gps= gps;
        this.radar= radar;
        this.scanner= scanner;
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
    
    public void changeWorld(String act){
                
        if(act == "moveNW")
            this.world[this.gps.first - 1][this.gps.second - 1]++;
        else if (act == "moveN")
            this.world[this.gps.first][this.gps.second - 1]++;
        else if (act == "moveNE")
            this.world[this.gps.first + 1][this.gps.second - 1]++;
        else if ( act == "moveW")
            this.world[this.gps.first - 1][this.gps.second]++;
        else if (act == "moveE")
            this.world[this.gps.first + 1][this.gps.second]++;
        else if (act == "moveSW")
            this.world[this.gps.first - 1][this.gps.second + 1]++;
        else if ( act == "moveS")
            this.world[this.gps.first][this.gps.second + 1]++;
        else
            this.world[this.gps.first + 1][this.gps.second + 1]++;
    }
    
    public boolean checkWorld(String act){
        if(act == "moveNW")
            return this.world[this.gps.first - 1][this.gps.second - 1]!=2;
        else if (act == "moveN")
            return this.world[this.gps.first][this.gps.second - 1]!=2;
        else if (act == "moveNE")
            return this.world[this.gps.first + 1][this.gps.second - 1]!=2;
        else if ( act == "moveW")
            return this.world[this.gps.first - 1][this.gps.second]!=2;
        else if (act == "moveE")
            return this.world[this.gps.first + 1][this.gps.second]!=2;
        else if (act == "moveSW")
            return this.world[this.gps.first - 1][this.gps.second + 1]!=2;
        else if ( act == "moveS")
            return this.world[this.gps.first][this.gps.second + 1]!=2;
        else
            return this.world[this.gps.first + 1][this.gps.second + 1]!=2;
    }
    
    public boolean ahogado(){   
        for(int i=1; i<4; i++)
            for(int j=1; j<4; j++)
                if(radar[i][j] != 1){
                    return false;
                }
        return true;
    }
    
    public boolean isMovS(){
        return radar[3][2]!=1;  //2 3
    }
    public boolean isMovSW(){
        return radar[3][1]!=1;  // 1 3
    }
    public boolean isMovSE(){
        return radar[3][3]!=1;
    }
    public boolean isMovE(){
        return radar[2][3]!=1;  //3 2
    }
    public boolean isMovNE(){
        return radar[1][3]!=1;  //3 1
    }
    public boolean isMovN(){
        return radar[1][2]!=1;  //2 1
    }
    public boolean isMovNW(){
        return radar[1][1]!=1;  //1 1
    }
    public boolean isMovW(){
        return radar[2][1]!=1;  //1 2
    }
    
    
    public boolean sinSolucion(String act){
                
        if(act == "moveNW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if (act == "moveN")
            return ((this.gps.first)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if (act == "moveNE")
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if ( act == "moveW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second)==finalPoint[1]);
        else if (act == "moveE")
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second)==finalPoint[1]);
        else if (act == "moveSW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
        else if ( act == "moveS")
            return ((this.gps.first)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
        else
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
    }
        
    public boolean initHeuristic2(){
        double minObst= Double.POSITIVE_INFINITY;
        double minVoid= Double.POSITIVE_INFINITY;
        
        for(int i=1; i<4; i++){
            for(int j=1; j<4; j++){
                if((i!=2 || j!=2) && (radar[i][j] == 1)){
                    if(scanner[i][j]<minObst)
                        minObst= scanner[i][j];
                }
                else if((i!=2 || j!=2) && (radar[i][j] != 1)){
                    if(scanner[i][j]<minVoid)
                        minVoid= scanner[i][j];
                }
            }
        }
        
        if(minObst<minVoid && minVoid != Double.POSITIVE_INFINITY){
            if(this.minValueFind>scanner[2][2]){
                this.minValueFind= scanner[2][2];
            }
        }
        
        if(minObst==Double.POSITIVE_INFINITY || this.minValueFind>minVoid)
            return false;
        else if(this.minValueFind<minVoid)
            return true;
        else
            return true;

    }
    
    public String heuristic2(){
        String act= new String();
        boolean accionElegida=false;
        
        if(!isMovE() && !isMovW() && !accionElegida){
             System.out.println("CHICANE: W-E"); 
             
             if(actionAnterior=="moveSE" || actionAnterior=="moveS" || actionAnterior=="moveSW"){
                if(isMovSW()){
                   act= "moveSW";
                   accionElegida=true;
                }
                else if(isMovS()){
                   act= "moveS";
                   accionElegida=true;
                }
             }
             else if(actionAnterior=="moveNE" || actionAnterior=="moveN" || actionAnterior=="moveNW"){
                 if(isMovNE()){
                   act= "moveNE";
                   accionElegida=true;
                }
                else if(isMovN()){
                   act= "moveN";
                   accionElegida=true;
                }
             }
        }
        
        if(!isMovN() && !isMovS() && !accionElegida){
             System.out.println("CHICANE: N-S"); 
             
             if(actionAnterior=="moveSE" || actionAnterior=="moveE" || actionAnterior=="moveNE"){
                if(isMovSE()){
                   act= "moveSE";
                   accionElegida=true;
                }
                else if(isMovE()){
                   act= "moveE";
                   accionElegida=true;
                }
             }
             else if(actionAnterior=="moveSW" || actionAnterior=="moveW" || actionAnterior=="moveNW"){
                 if(isMovSW()){
                   act= "moveSW";
                   accionElegida=true;
                }
                else if(isMovW()){
                   act= "moveW";
                   accionElegida=true;
                }
             }
        }
        
        if(!isMovE() && !accionElegida){
            System.out.println("NO SE PUEDE A: E");
            if(isMovNE()){
                act= "moveNE";
                accionElegida=true;
            }
            else if(isMovN()){
                act= "moveN";
                accionElegida=true;
            }
        }
        if(!isMovN() && !accionElegida){
            System.out.println("NO SE PUEDE A: N");
            if(isMovNW()){
                act= "moveNW";
                accionElegida=true;
            }
            else if(isMovW()){
                act= "moveW";
                accionElegida=true;
            }
        }
        if(!isMovW() && !accionElegida){
            System.out.println("NO SE PUEDE A: W");
            if(isMovSW()){
                act= "moveSW";
                accionElegida=true;
            }
            else if(isMovS()){
                act= "moveS";
                accionElegida=true;
            }
        }
        if(!isMovS() && !accionElegida){
            System.out.println("NO SE PUEDE A: S");
            if(isMovSE()){
                act= "moveSE";
                accionElegida=true;
            }
            else if(isMovE()){
                act= "moveE";
                accionElegida=true;
            }
        }
        if(!isMovSE() && !accionElegida){
            System.out.println("NO SE PUEDE A: SE");
            if(isMovE()){
                act= "moveE";
                accionElegida=true;
            }
            else if(isMovNE()){
                act= "moveNE";
                accionElegida=true;
            }
        }
        if(!isMovSW() && !accionElegida){
            System.out.println("NO SE PUEDE A: SW");
            if(isMovS()){
                act= "moveS";
                accionElegida=true;
            }
            else if(isMovSE()){
                act= "moveSE";
                accionElegida=true;
            }
        }
        if(!isMovNW() && !accionElegida){
            System.out.println("NO SE PUEDE A: NW");
            if(isMovW()){
                act= "moveW";
                accionElegida=true;
            }
            else if(isMovSW()){
                act= "moveSW";
                accionElegida=true;
            }
        }
        if(!accionElegida){
            System.out.println("NO SE PUEDE A: NE");
            if(isMovN())
                act= "moveN";
            else if(isMovNW())
                act= "moveNW";
        }
        
        if(sinSolucion(act)){
            System.out.println("-------------------------- No tiene solución. --------------------------");
            return "found";
        }
        
        changeWorld(act);
        System.out.println("ACCION ELEGIDA: "+act);
        return act;
    }
    
    public String heuristic1(){
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        finalPoint[0]= 0;
        finalPoint[1]= 0;

        System.out.println("HEURISTICA 1");

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
                    //System.out.println("CONTROLLER: OBSTACULO en ["+i+"]["+j+"]");
                    this.world[i][j]= -1;
                }
            }
        }
        actionAnterior= nextAction(newpos);
        return actionAnterior;
    }
    
    
    public String heuristic(){
        
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        String badPosition;
        
        if(this.battery < 2){
            System.out.println("CONTROLLER: REFUEL");
            return "refuel";
        }
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2){
            System.out.println("CONTROLLER: FOUND");
            return "found";
        }
        else if(ahogado()){
            System.out.println("AHOGADO");
            return "found";
        }
        else if(initHeuristic2()){
            System.out.println("HEURISTICA 2");
            
            if(initHeu2){
                finalPoint[0]= gps.first;
                finalPoint[1]= gps.second;
                initHeu2= false;
            }
            actionAnterior= heuristic2();
            return actionAnterior;
        }
        else{
            initHeu2=true;
            return heuristic1();
        }
    }
}
