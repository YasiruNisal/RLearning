package part2;

import java.text.DecimalFormat;
import java.util.Random;

public class QLearning {

	static int height = 4;                                                                                                 
    static int width = 4;                                                                                                  
    static int[][] grid;                                                                                               

    static int[][] actions = new int[height*width][];;                                                                                        

    static double[][] rewards;                                                                                         
    static int goal;                                                                                                   

    static int StartX = 0;
    static int StartY = 0;
    static int EndX = 3;
    static int EndY = 1;
    
    static int UZ1X = 0;
    static int UZ1Y = 1;
    static int UZ2X = 2;
    static int UZ2Y = 1;
    static int UZ3X = 3;
    static int UZ3Y = 3;
    static int unsafeZ1;
    static int unsafeZ2;
    static int unsafeZ3;
    
    static double reward = 10;
    static double nonReward = -1;
    static double unsafeZones = -10;
    
    static double[][] qScores = new double[height*width][height*width];                                                                                         
    static double learningRate = 0.1;                                                                                         
    static double discountRate = 0.85;                                                                                        
    static int episodes = 20;                                                                                                

    static int[] policy;   
    
    public static void main(String[] args){
    	//First build a grid with numbers going from 1-16 (4x4 grid)
    	 grid = new int[height][width];                                                                                  
         int counter = 0;
         for (int i = 0; i < height; i++) {
             for (int j = 0; j < width; j++) {
                 grid[i][j] = counter;
                 counter++;
             }
         }
         
         
         //next we want to set the actions that can be taken at each step
         //it will be up, down, left, right
         //(-1 means you cannot move in that direction)
         //others show the grid you can move to in all four directions
         for (int i = 0; i < height; i++){
        	 for (int j = 0; j < width; j++){
        		 int[] action = new int[4];
        		 if(i != 0){//up (-1 means you cannot move in that direction)
        			 action[0] = (grid[i-1][j]);
        		 }else{
        			 action[0] = -1;
        		 }
        		 if(j != 0){//left
        			 action[1] = (grid[i][j-1]);
        		 }else{
        			 action[1] = -1;
        		 }if(i != height-1){//down
        			 action[2] = grid[i+1][j];
        		 }else{
        			 action[2] = -1;
        		 }if(j != width -1){//right
        			 action[3] = grid[i][j+1];
        		 }else{
        			 action[3] = -1;
        		 }
        		 actions[grid[i][j]] = action;
        	 }
         }
         
         rewards = new double[height*width][height*width];
         goal = grid[EndY][EndX]; //set the goal state 
         unsafeZ1 = grid[UZ1X][UZ1Y];
         unsafeZ2 = grid[UZ2X][UZ2Y];
         unsafeZ3 = grid[UZ3X][UZ3Y];
         // Sets the goal state from the Grid.
         //sets the reward to -1 in all other locations
         //needs to add the functionality where the black boxes have a higher - value
         if (EndX != 0) {
        	 rewards[goal - 1][goal] = reward;
         }                                                                 
         if (EndY != 0) {
        	 rewards[goal - width][goal] = reward;
         }
         if (EndX != width-1) {
        	 rewards[goal + 1][goal] = reward;
         }
         if (EndY != height-1) {
        	 rewards[goal + width][goal] = reward;
         }
         
       //setting the rewards for the first black box
         if (UZ1X != 0) {
        	 rewards[unsafeZ1 - 1][unsafeZ1] = unsafeZones;
         }                                                               
         if (UZ1Y != 0) {
        	 rewards[0][1] = unsafeZones;
         }
         if (UZ1X != width-1) {
        	 rewards[unsafeZ1 + 1][unsafeZ1] = unsafeZones;
         }
         if (UZ1Y != height-1) {
        	 rewards[unsafeZ1 + width][unsafeZ1] = unsafeZones;
         }
         //System.out.println(rewards[unsafeZ1 + 1][unsafeZ1] + "- " +rewards[unsafeZ1 + 1][unsafeZ1] +"- " + rewards[unsafeZ1 + width][unsafeZ1] +" 1" );
       //setting the rewards for the second black box
         if (UZ2X != 0) {
        	 rewards[unsafeZ2 - 1][unsafeZ2] = unsafeZones;
         }                                                                 
         if (UZ2Y != 0) {
        	 rewards[unsafeZ2 - width][unsafeZ2] = unsafeZones;
         }
         if (UZ2X != width-1) {
        	 rewards[unsafeZ2 + 1][unsafeZ2] = unsafeZones;
         }
         if (UZ2Y != height-1) {
        	 rewards[unsafeZ2 + width][unsafeZ2] = unsafeZones;
         }
         //System.out.println(rewards[unsafeZ2 - 1][unsafeZ2] + "- " +rewards[unsafeZ2 - width][unsafeZ2] +"- " + rewards[unsafeZ2 + 1][unsafeZ2] +"-" + rewards[unsafeZ2 + width][unsafeZ2] + " 2");
         //setting the rewards for the third black box
         if (UZ3X != 0) {
        	 rewards[unsafeZ3 - 1][unsafeZ3] = unsafeZones;
         }                                                                
         if (UZ3Y != 0) {
        	 rewards[unsafeZ3 - width][unsafeZ3] = unsafeZones;
         }
         if (UZ3X != width-1) {
        	 rewards[unsafeZ3 + 1][unsafeZ3] = unsafeZones;
         }
         if (UZ3Y != height-1) {
        	 rewards[unsafeZ3 + width][unsafeZ3] = unsafeZones;
         }
         
         if (nonReward == 0) {                                      
             for (int i = 0; i < rewards.length; i++) {
                 for (int j = 0; j < rewards[i].length; j++) {
                     if (rewards[i][j] == 0)
                         rewards[i][j] = nonReward;
                 }
             }
         }

         actions[goal] = new int[] {goal};  
         
         
         QLalgorithm();
         makePolicy();  
         run(); 
         //THis for loops prints out the policy
         for (int i = 0; i < policy.length; i++) {                                                                   
             if (i == goal){
                 System.out.print((i) + " to " + " Goal!");
             }else if(i == unsafeZ1 || i == unsafeZ2 || i == unsafeZ3){
            	 System.out.print((i) + " to " + " Unsafe!");
         	 }else{
            	 System.out.print((i) + " to " + (policy[i]));
             }
             System.out.println();
         }
         
         //This for loop prints out the q values associated with transitions
         DecimalFormat decimal = new DecimalFormat("0.00");
         System.out.print("\t\t\t");
         for (int i = 0; i < qScores.length; i++) {  
        	 System.out.print((i) + "\t");
         }
         System.out.println();
         for (int i = 0; i < qScores.length; i++) {                                                                  
        	 System.out.print("Current State " + (i) + ":\t");
             if (i != goal) {
                 for (int j = 0; j < qScores[i].length; j++) {                                                      
                	 //System.out.println(qScores.length + " - " + i + " - " + qScores[i].length);
                	 System.out.print(Double.parseDouble(decimal.format(qScores[i][j])) + "\t");                           
                 }
                 System.out.println();
             }
             else {
            	 System.out.print("END!");
            	 System.out.println();
             }
         }
    }
    
    //This is the q-learning algorithm that calculates 
    //the q values which determine the transitions from different states
    public static void QLalgorithm() {
        Random random = new Random();

        for (int i = 0; i < episodes; i++) {                                                                            
            int state = random.nextInt(height*width - 1);                                                              
            while (state != goal) {
                int[] stateActions = actions[state];                                                                    
                int action = stateActions[random.nextInt(stateActions.length)];                                         
                while (action == -1) {
                    action = stateActions[random.nextInt(stateActions.length)];
                }
                //get the previous score
                double q = qScores[state][action];  
                //get the reward
                double reward = rewards[state][action];                                                                 

                double maxQ = maxQ(action);                                                                             
                double score = q + learningRate * (reward + discountRate * maxQ - q);                                   
                qScores[state][action] = score;                                                                         

                state = action;//go to the new state
                
            }
        }
        
        
    }
    
 // Creates the policy based on Q scores.
    public static void makePolicy() {
        policy = new int[height*width];
        for (int i = 0; i < qScores.length; i++) {                                                                      
            double max = Double.MIN_VALUE;
            int index = -1;
            for (int j = 0; j < qScores[i].length; j++) {                                                               
                if (qScores[i][j] > max) {                                                                             
                    index = j;                                                                                          
                    max = qScores[i][j];                                                                                
                }
            }
            policy[i] = index;                     
        }
    }
    
    //This prints out the path from the starting point to the end goal
    public static void run() {

        int state = grid[StartY][StartX];                                                                                         
        while (state != goal) {
        	if(state != -1){
	            System.out.print("From: " + (state));
	            state = policy[state];     
	            
	            System.out.print(" To: " + (state) + "\n");
        	}else{
        		System.out.print(" Please Re-Run the Progam");
        	}
        }
    }
    
    //use this function to find the maximum Q value
    //helper function used in the QL algorithm
    public static double maxQ(int state) {
        double max = Double.MIN_VALUE;                                                                                 
        int[] futureActions = actions[state];                                                                           
        for (int i = 0; i < futureActions.length; i++) {                                                                
            if (futureActions[i] != -1) {
                double value = qScores[state][futureActions[i]];                                                        
                if (value > max)                                                                                        
                    max = value;
            }
        }
        return max;
    }
    
    public static void printintArray(int[][] arr){
    	for (int i = 0; i < height; i++){
    		for(int j = 0; j < width; j++){
    			System.out.print(arr[j][i]+ "\t");
    		}
    		System.out.println("");
    		
    	}
    }
    
    public static void printDblArray(double[][] arr){
    	for (int i = 0; i < height; i++){
    		for(int j = 0; j < width; j++){
    			System.out.print(arr[j][i]+ "\t");
    		}
    		System.out.println("");
    		
    	}
    }
}
