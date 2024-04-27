package functions;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import predator_prey_main.PredatorPrey;

@SuppressWarnings("serial")
public class Right extends GPNode implements EvalPrint{

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
		
		PredatorPrey pred = (PredatorPrey)problem;
		// changes orientation to right based on current orientation.
		switch (pred.predOrientation) {
			case PredatorPrey.orientation_UP:
				pred.predOrientation = PredatorPrey.orientation_RIGHT;
				break;
			case PredatorPrey.orientation_LEFT:
				pred.predOrientation = PredatorPrey.orientation_UP;
				break;
			case PredatorPrey.orientation_DOWN:
				pred.predOrientation = PredatorPrey.orientation_LEFT;
				break;
			case PredatorPrey.orientation_RIGHT:
				pred.predOrientation = PredatorPrey.orientation_DOWN;
				break;
			default:
				state.output.fatal("Bad Orientation of Predator Detected. ( " + pred.predOrientation + " )");
		}
		
		pred.moves++;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "right";
	}
	
	public int expectedChildren() {
		return 0;
	}

}
