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
    private boolean iniHeu2;
    private boolean izq, dec;
    private double []minObstacle;
    private double []finalPoint;
    private String antMov;
    private String action;
    private int cont;
    
    public HeuristicaBasica(){
        minObstacle= new double[2];
        finalPoint= new double[2];
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Integer[500][500];
        this.cont = 0;
        
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
    
    public boolean ahogado(){   
        for(int i=1; i<4; i++)
            for(int j=1; j<4; j++)
                if(radar[i][j] != 1){
                    return false;
                }
        return true;
    }
    
    public boolean isMovS(){
        return radar[2][3]!=1;  //2 3
    }
    public boolean isMovSW(){
        return radar[1][3]!=1;  // 1 3
    }
    public boolean isMovSE(){
        return radar[3][3]!=1;
    }
    public boolean isMovE(){
        return radar[3][2]!=1;  //3 2
    }
    public boolean isMovNE(){
        return radar[3][1]!=1;  //3 1
    }
    public boolean isMovN(){
        return radar[2][1]!=1;  //2 1
    }
    public boolean isMovNW(){
        return radar[1][1]!=1;  //1 1
    }
    public boolean isMovW(){
        return radar[1][2]!=1;  //1 2
    }
    
    public void IzqOrDec(){
        if(antMov=="moveN" || antMov=="moveNE" || antMov=="moveNW"){
            if((radar[1][1] + radar[2][1] + radar[3][1]) > (radar[1][3] + radar[2][3] + radar[3][3])){
                action= "dec";
            }
            else
                action= "izq";
        }
        else if(antMov=="moveS" || antMov=="moveSW" || antMov=="moveSE"){
            if((radar[1][1] + radar[2][1] + radar[3][1]) > (radar[1][3] + radar[2][3] + radar[3][3])){
                action= "izq";
            }
            else
                action= "dec";
        }
        else if(antMov=="moveW"){
            if((radar[1][1] + radar[1][2] + radar[1][3]) > (radar[3][1] + radar[3][2] + radar[3][3])){
                action= "dec";
            }
            else
                action= "izq";
        }
        else{
            if((radar[1][1] + radar[1][2] + radar[1][3]) > (radar[3][1] + radar[3][2] + radar[3][3])){
                action= "izq";
            }
            else
                action= "dec";
        }
    }
    
    public boolean sinSolucion(){
        return (gps.first==finalPoint[0]) && (gps.second==finalPoint[1]);
    }
    
    public boolean initHeuristic2(){
        double minObst= Double.POSITIVE_INFINITY;
        double minVoid= Double.POSITIVE_INFINITY;
        for(int i=1; i<4; i++){
            for(int j=1; j<4; j++){
                if((i!=2 || j!=2) && (radar[i][j] == 1)){
                    if(radar[i][j] < minObst){
                        minObst= scanner[i][j];
                        minObstacle[0]= i;
                        minObstacle[1]= j;
                    }
                }
                else if((i!=2 || j!=2) && (radar[i][j] != 1)){
                    if(radar[i][j] < minVoid){
                        minVoid= scanner[i][j];
                    }
                }
            }
        }
        
         
        
        if(minObst < minVoid){
            System.out.println("MINIMO OBSTACULO: " + minObst);
            System.out.println("MINIMO VACIO: " + minVoid);
            if(!iniHeu2){
                finalPoint[0]=gps.first;
                finalPoint[1]=gps.second;
                cont++;
                iniHeu2= true;
                IzqOrDec();
            }
            
            return true;
        }
        else{
            
            System.out.println("MINIMO VACIO: " + minVoid);
            System.out.println("MINIMO OBSTACULO: " + minObst);
            finalPoint[0]=0;
            finalPoint[1]=0;
            return false;
        }
    }
    
    public String heuristic2(){
        
        if(sinSolucion() && cont==0)
            return "found";
        
        if(action=="izq"){
            if(antMov=="moveN" || antMov=="moveNE" || antMov=="moveNW"){
                if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else{
                    //antMov= "MoveNW";
                    return "moveNW";
                }
            }
            else if(antMov=="moveS" || antMov=="moveSW" || antMov=="moveSE"){
                if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else{
                    //antMov= "MoveSE";
                    return "moveSE";
                }
            }
            else if(antMov=="moveW"){
                if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else{
                    //antMov= "MoveSW";
                    return "moveSW";
                }
            }
            else{   //MoveE
                if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else{
                    //antMov= "MoveNE";
                    return "moveNE";
                }
            }
        }
        else{
            if(antMov=="moveN" || antMov=="moveNE" || antMov=="moveNW"){
                if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else{
                    //antMov= "MoveNE";
                    return "moveNE";
                }
            }
            else if(antMov=="moveS" || antMov=="moveSW" || antMov=="moveSE"){
                if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else{
                    //antMov= "MoveSW";
                    return "moveSW";
                }
            }
            else if(antMov=="moveW"){
                if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else if(isMovSE()){
                    //antMov= "MoveSE";
                    return "moveSE";
                }
                else if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovN()){
                    //antMov= "MoveN";
                    return "moveN";
                }
                else{
                    //antMov= "MoveNW";
                    return "moveNW";
                }
            }
            else{   //MoveE
                if(isMovE()){
                    //antMov= "MoveE";
                    return "moveE";
                }
                else if(isMovNE()){
                    //antMov= "MoveNE";
                    return "moveNE";
                }
                else if(isMovN()){
                   // antMov= "MoveN";
                    return "moveN";
                }
                else if(isMovNW()){
                    //antMov= "MoveNW";
                    return "moveNW";
                }
                else if(isMovW()){
                    //antMov= "MoveW";
                    return "moveW";
                }
                else if(isMovSW()){
                    //antMov= "MoveSW";
                    return "moveSW";
                }
                else if(isMovS()){
                    //antMov= "MoveS";
                    return "moveS";
                }
                else{
                    //antMov= "MoveSE";
                    return "moveSE";
                }
            }
            
        }
       
        
    }
    
    
    public String heuristic(){
        
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
        else if(ahogado()){
            System.out.println("AHOGADO");
            return "found";
        }
        else if(initHeuristic2()){
            System.out.println("HEURISTICA 2");
            return heuristic2();
        }
        else{
            System.out.println("HEURISTICA 1");
            cont=0;
            iniHeu2= false;
            
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
            antMov= nextAction(newpos);
            return nextAction(newpos);
        }
    }
}
