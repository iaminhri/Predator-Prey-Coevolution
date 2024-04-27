package functions;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import predator_prey_main.PredatorPrey;

@SuppressWarnings("serial")
public class PreyRight extends GPNode implements EvalPrint{

	@Override
	public void evalPrint(final EvolutionState state, 
			final int thread, 
			final GPData input, 
			final ADFStack stack, 
			final GPIndividual individual, 
			final  Problem problem, 
			final int[][] map2) {
		// TODO Auto-generated method stub
		eval(state,thread,input,stack,individual,problem);
	}

	@Override
	public void eval(final EvolutionState state, 
			final int thread, 
			final GPData input, 
			final ADFStack stack, 
			final GPIndividual individual, 
			final  Problem problem) {
		// TODO Auto-generated method stub
		
		PredatorPrey prey = (PredatorPrey)problem;
		// changes orientation to right based on current orientation.
		switch (prey.preyOrientation) {
			case PredatorPrey.orientation_prey_UP:
				prey.preyOrientation = PredatorPrey.orientation_prey_RIGHT;
				break;
			case PredatorPrey.orientation_LEFT:
				prey.preyOrientation = PredatorPrey.orientation_prey_UP;
				break;
			case PredatorPrey.orientation_DOWN:
				prey.preyOrientation = PredatorPrey.orientation_prey_LEFT;
				break;
			case PredatorPrey.orientation_RIGHT:
				prey.preyOrientation = PredatorPrey.orientation_prey_DOWN;
				break;
			default:
				state.output.fatal("Bad Orientation of Predator Detected. ( " + prey.preyOrientation + " )");
		}
		
		prey.preyMoves++;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "prey_right";
	}
	
	public int expectedChildren() {
		return 0;
	}

}
