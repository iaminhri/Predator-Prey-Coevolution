package functions;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import predator_prey_main.PredatorPrey;

@SuppressWarnings("serial")
public class PreyMoves extends GPNode implements EvalPrint{
	
	@Override
	public void evalPrint(final EvolutionState state, 
			final int thread, 
			final GPData input, 
			final ADFStack stack, 
			final GPIndividual individual, 
			final  Problem problem,
			final int[][] map2) {
		// TODO Auto-generated method stub
		
		PredatorPrey prey = (PredatorPrey) problem;
		
		switch (prey.preyOrientation) {
			case PredatorPrey.orientation_prey_UP:
				prey.posY--;
				if(prey.posY < 0) {
					prey.posY = prey.maxY - 1;
				}
				break;
			case PredatorPrey.orientation_prey_LEFT:
				prey.posX--;
				if(prey.posX < 0) {
					prey.posX = prey.maxX - 1;
				}
				break;
			case PredatorPrey.orientation_prey_DOWN:
				prey.posY++;
				if(prey.posY >= prey.maxY) {
					prey.posY = 0;
				}
				break;
			case PredatorPrey.orientation_prey_RIGHT:
				prey.posX++;
				
				if(prey.posX >= prey.maxX) {
					prey.posX = 0;
				}
				break;
			default:
				state.output.fatal("Bad Orientation of Predator Detected. ( " + prey.preyOrientation + " )");
		}
		
		prey.preyMoves++;
		
		if(prey.map[prey.posX][prey.posY] == PredatorPrey.PREY && prey.moves < prey.maxMoves) {
			prey.preyEaten++;
			prey.map[prey.posX][prey.posY] = PredatorPrey.ATE;
		}
		
		if(prey.moves < prey.maxMoves) {
			// implement prey's movement here. 
		}	
}

	@Override
	public void eval(final EvolutionState state, 
			final int thread, 
			final GPData input, 
			final ADFStack stack, 
			final GPIndividual individual, 
			final  Problem problem) {
		// TODO Auto-generated method stub
		PredatorPrey prey = (PredatorPrey) problem;
		
		switch (prey.preyOrientation) {
			case PredatorPrey.orientation_prey_UP:
				prey.posY--;
				if(prey.posY < 0) {
					prey.posY = prey.maxY - 1;
				}
				break;
			case PredatorPrey.orientation_prey_LEFT:
				prey.posX--;
				if(prey.posX < 0) {
					prey.posX = prey.maxX - 1;
				}
				break;
			case PredatorPrey.orientation_prey_DOWN:
				prey.posY++;
				if(prey.posY >= prey.maxY) {
					prey.posY = 0;
				}
				break;
			case PredatorPrey.orientation_prey_RIGHT:
				prey.posX++;
				
				if(prey.posX >= prey.maxX) {
					prey.posX = 0;
				}
				break;
			default:
				state.output.fatal("Bad Orientation of Predator Detected. ( " + prey.preyOrientation + " )");
		}
		
		prey.preyMoves++;
		
//		if(prey.map[prey.posX][prey.posY] == PredatorPrey.PREY && prey.moves < prey.maxMoves) {
//			prey.preyEaten++;
//			prey.map[prey.posX][prey.posY] = PredatorPrey.ATE;
//		}		
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "prey_move";
	}
	
	public int expectedChildren() {
		return 0;
	}

}
