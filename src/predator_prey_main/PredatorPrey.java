package predator_prey_main;

import java.util.ArrayList;

import ec.*;
import ec.coevolve.*;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import functions.EvalPrint;


@SuppressWarnings("serial")
public class PredatorPrey extends GPProblem implements GroupedProblemForm{

	/**
	 * Create one predator, and multiple instances of prey.
	 * fitness goal is predator eating maximum amount of preys.
	 * predator: evolves with GP
	 * prey: dumb and runs from predator by randomy movement.
	 * fitness: number of prey eaten.
	 * 
	 * GP Lang: 
	 * 	Functions: IfPreyAhead, Behind, Right, Left, PROGN2 A B, PRGON3 A B C
	 *  Terminals: Move, Left, Right 
	 *  
	 *  Simulation: update and record all entities - location, current predator's direction, 
	 *  time count, ...
	 *    
	 *  Predator Prey Application
	 *  	one predator eats multiple preys.
	 *  	simulation: 
	 *  		update and record the state of all entities. location, current predator direction,
	 *  		time count and others.
	 *  		Predators state changes according to the functions and terminals.
	 *  		Output: Predator movements, locations where prey eaten, location of live prey
	 *  
	 *  
	 */
	
	// these are defined in the predator.params file.
	public static final String P_MOVES = "moves";
	public static final String P_Map_lenght_X = "map-x";
	public static final String P_Map_lenght_Y = "map-y";
	public static final String P_Prey_Amount = "prey_amount";
	
	
	// PredatorPrey simulation.
	
	public static final int PREY = -1; // Prey's position mark
	public static final int PREDATOR = 1; // predator itself 
	public static final int EMPTY = 0; // empty cell mark
	public static final int ATE = 3; // prey eaten mark
	
	public static final int PREDATOR_X = 0;
	public static final int PREDATOR_Y = 0;
	
	
    public int map[][]; // map
    
    public int preyX[]; //preys X position in map
    public int preyY[]; // preys Y position in map
    
    public int posX, posY; // current pred's position
    
    public int maxX, maxY; // map max length and width
    
    public int moves; // moves counting
    
    public int maxMoves; // maximum moves
    
    public int preyMoves;
    
    public int prey; // predator's food
        
    public int preyAmount;
    
    public int preyEaten;
    
    public int pmod;
	
	
	// Predator orientations
    public static final int orientation_UP = 0;
    public static final int orientation_LEFT = 1;
    public static final int orientation_DOWN = 2;
    public static final int orientation_RIGHT = 3;
    
    public static final int orientation_prey_UP = 0;
    public static final int orientation_prey_LEFT = 1;
    public static final int orientation_prey_DOWN = 2;
    public static final int orientation_prey_RIGHT = 3;
	
    public int predOrientation;
    public int preyOrientation;
    
    public Object clone() {
    	PredatorPrey myobj = (PredatorPrey) (super.clone()); // deep clone of the class
    	myobj.map = new int[map.length][];
    	
    	for(int i = 0; i < map.length; i++) {
    		myobj.map[i] = (int[]) (map[i].clone()); // shallow clone of the rows
    	}
    	
    	return myobj;    	
    }

	public void setup(final EvolutionState state, final Parameter base) {
		// always initialize this.
		super.setup(state, base); 
		
		// reading maxMoves and maxX and maxY from predator.params file.
		// ____________________________________________________________________________
		maxMoves = state.parameters.getInt(base.push(P_MOVES), null, 1);
		maxX = state.parameters.getInt(base.push("max-x"), null, 10);
		maxY = state.parameters.getInt(base.push("max-y"), null, 10);
		preyAmount = state.parameters.getInt(base.push(P_Prey_Amount), null, 5);
				
		if(maxMoves == 0 || maxX == 0 || maxY == 0) {
			state.output.error("The number of moves of a predator or max length has to be > 0");
		}
		// ____________________________________________________________________________
		
		// Map Initialization
		// ____________________________________________________________________________
		map = new int[maxX][maxY];
		preyX = new int[preyAmount];
		preyY = new int[preyAmount];
		
		for(int i = 0; i < maxX; i++) {
			for(int j = 0; j < maxY; j++) {
				map[i][j] = EMPTY;
			}
		}
		
		// ____________________________________________________________________________

		// fixed position for predator's starting point.
		map[PREDATOR_X][PREDATOR_Y] = PREDATOR;
		
		// ____________________________________________________________________________
		
		// Random movement of prey
		for(int i = 0; i < preyAmount; i++) {
			int x, y;
			
			do {
				x = state.random[0].nextInt(maxX-1);
				y = state.random[0].nextInt(maxY-1);
			}while(map[x][y] != EMPTY && x < maxX && y < maxY);		
						
			preyX[i] = x;
			preyY[i] = y; 
			
			map[x][y] = PREY;
		}      
		// ____________________________________________________________________________

		moves = 0;
		preyEaten = 0;
	}	
	
	@Override
	public void evaluate(final EvolutionState state, 
	        final Individual ind, 
	        final int subpopulation,
	        final int threadnum) {
		// TODO Auto-generated method stub
		if (!ind.evaluated)  // don't bother reevaluating
        {
			preyEaten = 0;
	        posX = 0;
	        posY = 0;
	        pmod = 97;
	        map[posX][posY] = PREDATOR; 
	        predOrientation = orientation_RIGHT;
	        preyOrientation = orientation_prey_RIGHT;
	        
	  
	        // GP tree running
	        for(moves = 0; moves < maxMoves && preyEaten < preyAmount; )
	            ((GPIndividual)ind).trees[0].child.eval(
	                state,threadnum,input,stack,((GPIndividual)ind),this);
	        
	        // the fitness better be KozaFitness!
	        KozaFitness f = ((KozaFitness)ind.fitness);
	        f.setStandardizedFitness(state,(preyAmount - preyEaten));
	        f.hits = preyEaten;
	        ind.evaluated = true;
	        
	        for (int i = 0; i < preyAmount; i++) {
		        map[preyX[i]][preyY[i]] = PREY;
		    }
        }
	}
	
	public void describe(
	        final EvolutionState state, 
	        final Individual ind, 
	        final int subpopulation, 
	        final int threadnum,
	        final int log) {
		
        state.output.println("\n\nBest Individual's Map\n=====================", log);

        preyEaten = 0;
        posX = 0;
        posY = 0;
        predOrientation = orientation_RIGHT;
        preyOrientation = orientation_prey_RIGHT;
                
        int[][] map2 = new int[map.length][];
        for(int x=0;x<map.length;x++)
            map2[x] = (int[])(map[x].clone());      
 
        
        map2[posX][posY] = PREDATOR;
        
        for(moves = 0; moves < maxMoves && preyEaten < preyAmount; )
            ((EvalPrint)(((GPIndividual)ind).trees[0].child)).evalPrint(
                state,threadnum,input,stack,((GPIndividual)ind),this,
                map2);
        
     // Print simulation map
        state.output.println("Simulation Map:", log); //
               
        for (int y = 0; y < map2.length; y++) {
            for (int x = 0; x < map2.length; x++) {
            	
            	switch(map2[y][x]) {
            		case PREY:
            			state.output.print("p", log);
            			break;
            		case PREDATOR:
            			state.output.print("#", log);
            			break;
            		case ATE:
            			state.output.print("X", log);
            			break;
            		case EMPTY:
            			state.output.print(".", log);
            			break;
            		default:
            			state.output.print(""+((char)map2[x][y]), log);
            			break;
            	}
            }
            state.output.println("", log);
        }
        
    }	
	
	 public int getPreyEaten() {
        return preyEaten;
     }

    public int getPreyAlive() {
        int preyAlive = 0;
        for (int i = 0; i < preyAmount; i++) {
            if (map[preyX[i]][preyY[i]] == PREY) {
                preyAlive++;
            }
         }
	     return preyAlive;
     }

	@SuppressWarnings("unchecked")
	@Override
	public void evaluate(EvolutionState state, 
			Individual[] ind, 
			boolean[] updateFitness, 
			boolean countVictoriesOnly, 
			int[] subpopulation, 
			int threadnum) {
		
		// TODO Auto-generated method stub
		
		if( ind.length != 2 || updateFitness.length != 2 )
             state.output.fatal( "The InternalSumProblem evaluates only two individuals at a time." );

        if( ! ( ind[0] instanceof GPIndividual ) )
             state.output.fatal( "The individuals should be GPIndividuals." );

        if( ! ( ind[1] instanceof GPIndividual ) )
             state.output.fatal( "The individuals should be GPIndividuals." );
		
		preyEaten = 0;
        posX = 0;
        posY = 0;
        pmod = 97;
        map[posX][posY] = PREDATOR; 
        predOrientation = orientation_RIGHT;
        preyOrientation = orientation_prey_RIGHT;
                
        // GP tree running
        for(moves = 0; moves < maxMoves && preyEaten < preyAmount; )
            ((GPIndividual)ind[0]).trees[0].child.eval(
                state,threadnum,input,stack,((GPIndividual)ind[0]),this);
        
        for(moves = 0; moves < maxMoves && preyEaten < preyAmount; )
            ((GPIndividual)ind[1]).trees[0].child.eval(
                state,threadnum,input,stack,((GPIndividual)ind[1]),this);
        
        if(updateFitness[0]) {
        	// the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind[0].fitness);
            f.trials.add(new Double(preyAmount - preyEaten));
            f.setFitness(state,(preyAmount - preyEaten));
            f.hits = preyEaten;
            ind[0].evaluated = true;
        }
        
        if(updateFitness[1]) {
        	KozaFitness f = ((KozaFitness)ind[1].fitness);
        	f.trials.add(new Double(preyEaten));
            f.setFitness(state,(preyEaten));
            f.hits = preyEaten;
            ind[1].evaluated = true;
        }
        
        
        for (int i = 0; i < preyAmount; i++) {
	        map[preyX[i]][preyY[i]] = PREY;
	    }
	}

	@Override
	public void postprocessPopulation(EvolutionState state, Population pop, boolean[] assessFitness, boolean countVictoriesOnly) {
		// TODO Auto-generated method stub
		int total = 0;
		
		for(int i = 0; i < pop.subpops.length; i++) {
			if(assessFitness[i]) {
				for(int j = 0; j < pop.subpops[i].individuals.length; j++) {
					KozaFitness fit = ((KozaFitness) (pop.subpops[i].individuals[j].fitness));
					
					int len = fit.trials.size();
					
					double sum = 0;
					
					for(int l = 0; l < len; l++) {
						sum += ((Double)(fit.trials.get(l))).doubleValue();
					}
					sum /= len;
					
					fit.setStandardizedFitness(state, sum);
					pop.subpops[i].individuals[j].evaluated = true;
					total++;
				}
			}
		}
	}

	@Override
	public void preprocessPopulation(EvolutionState state, 
			Population pop, 
			boolean[] prepareForAssessment, 
			boolean countVictoriesOnly) {
		// TODO Auto-generated method stub
		for(int i = 0; i < pop.subpops.length; i++ )
          if (prepareForAssessment[i])
              for(int j = 0; j < pop.subpops[i].individuals.length ; j++ )
                  ((KozaFitness)(pop.subpops[i].individuals[j].fitness)).trials = new ArrayList();
	}
}


//@SuppressWarnings("serial")
//public class PredatorPrey extends PredatorPreyGPProblem implements GroupedProblemForm {
//
//	@SuppressWarnings({ "deprecation", "unchecked", "null" })
//	@Override
//	public void evaluate(EvolutionState state, 
//			Individual[] ind, 
//			boolean[] updateFitness, 
//			boolean countVictoriesOnly, 
//			int[] subpopulation, 
//			int threadnum) {
//		
////		state.output.println("Evaluating individuals...", threadnum);
//    
//		// TODO Auto-generated method stub
//		if( ind.length != 2 || updateFitness.length != 2 )
//            state.output.fatal( "The InternalSumProblem evaluates only two individuals at a time." );
//		
//		if( ! (ind[0] instanceof GPIndividual )) {
//            state.output.fatal( "The individuals in the PredatorPrey should be GPIndividual." );
//		}
//		
//		if( ! (ind[1] instanceof GPIndividual )) {
//            state.output.fatal( "The individuals in the PredatorPrey should be GPIndividual." );
//		}
//		
////		int size = 0;
////		for(int i = 0; i < ind.length; i++) {
////			if(! (ind[i] instanceof GPIndividual)) {
////				state.output.error("Individual " + i + "in coevolution is not a GPIndividual.");
////			}
////			else {
////				GPIndividual t = (GPIndividual)(ind[i]);
////				size += t.trees.length;
////			}
////		}
//		
//		int value1 = 0;
//		int value2 = 0;
//		
//		GPIndividual temp;
//		
//		temp = (GPIndividual)ind[0];
//		
//		for(int i = 0; i < temp.trees.length; i++) {
//			if(temp.trees[i] != null) {
//				value1++;
//			}
//		}
//		
//		temp = (GPIndividual)ind[1];
//		for(int i = 0; i < temp.trees.length; i++) {
//			if(temp.trees[i] != null) {
//				value2++;
//			}
//		}
//		
//		double score = value1 - value2;
////				
//		KozaFitness fit1 = (KozaFitness)(ind[0].fitness);
//		KozaFitness fit2 = (KozaFitness)(ind[1].fitness);
//		
//		if(updateFitness[0]) {
//			fit1.trials.add(new Double(score));
//			fit1.setStandardizedFitness(state, score);
//		}
//		
//		if(updateFitness[1]) {
//			fit2.trials.add(new Double(score));
//			fit2.setStandardizedFitness(state, score);
//		}
//	}
//	
//	@Override
//	public void preprocessPopulation(EvolutionState state, 
//			Population pop, 
//			boolean[] prepareForAssessment, 
//			boolean countVictoriesOnly) {
//		// TODO Auto-generated method stub
//		// evaluate population 1's individual with population 2's individual
//		// and vice versa
//		// evaluate the fitness of both population here, and update the fitness of both population. 
////		System.out.println("Hello hridoy");
////		for(int i = 0; i < pop.subpops.length; i++) {
////			Subpopulation subpop = pop.subpops[i];
////			for(int j = 0; j < subpop.individuals.length; j++) {
////				KozaFitness fit = (KozaFitness) (subpop.individuals[j].fitness);
////				state.output.fatal( "Individual[ " + j + "] = " + subpop.individuals[j].fitness + "Fitness" );
////				fit.trials = new ArrayList<KozaFitness>();
////			}
////		}
//		
//		for(int i = 0; i < pop.subpops.length; i++ )
//            if (prepareForAssessment[i])
//                for(int j = 0; j < pop.subpops[i].individuals.length ; j++ )
//                    ((KozaFitness)(pop.subpops[i].individuals[j].fitness)).trials = new ArrayList();
//	}	
//	
//	@Override
//	public void postprocessPopulation(EvolutionState state, Population pop, boolean[] assessFitness, boolean countVictoriesOnly) {
//		// TODO Auto-generated method stub
//		// evaluate the fitness in terms of how one individual performed in the opposition's population
////		Finish processing the population (such as fitness information) after evaluation. Although this method is not static, you should not use it to write to any instance variables in the GroupedProblem instance; this is because it's possible that the instance used is in fact the prototype, and you will have no guarantees that your instance variables will remain valid during the evaluate(...) process. Do not assume that pop will be the same as state.pop -- it may not. state is only provided to give you access to EvolutionState features.
////		countVictoriesOnly will be set if Individuals' fitness is to be based on whether they're the winner of a test, instead of based on the specifics of the scores in the tests. This really only happens for Single-Elimination Tournament one-population competitive coevolution. If this is set, probably would leave the Fitnesses as they are here (they've been set and incremented in evaluate(...)), but if it's not set, you may want to set the Fitnesses to the maximum or average or the various trials performed.
////
////		assessFitness will indicate which subpopulations should have their final fitness values assessed. You should not clear the trials of individuals for which assessFitness[] is false. Instead allow trials to accumulate and ultimately update the fitnesses later when the flag is set. assessFitness[] may not be the same as updateFitness[] in evaluate(...).
////
////		Should return the number of individuals evaluated (not tested: but actually had their fitnesses modified -- or would have if the evaluated flag wasn't set).
//		
////		System.out.println("Hello Iamin");
//		
//		
//		int total = 0;
//		
//		for(int i = 0; i < pop.subpops.length; i++) {
//			if(assessFitness[i]) {
//				for(int j = 0; j < pop.subpops[i].individuals.length; j++) {
//					KozaFitness fit = ((KozaFitness) (pop.subpops[i].individuals[j].fitness));
//					
//					int len = fit.trials.size();
//					
//					double sum = 0;
//					
//					for(int l = 0; l < len; l++) {
//						sum += ((Double)(fit.trials.get(l))).doubleValue();
//					}
//					sum /= len;
//					
//					fit.setStandardizedFitness(state, sum);
//					pop.subpops[i].individuals[j].evaluated = true;
//					total++;
//				}
//			}
//		}
//
//	}
//}
