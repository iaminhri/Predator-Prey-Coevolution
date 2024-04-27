package predator_prey_main;

import java.util.ArrayList;

import ec.*;
import ec.coevolve.*;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import functions.EvalPrint;

@SuppressWarnings("serial")
public class PredatorPrey extends PredatorPreyGPProblem implements GroupedProblemForm {

	@SuppressWarnings({ "deprecation", "unchecked", "null" })
	@Override
	public void evaluate(EvolutionState state, 
			Individual[] ind, 
			boolean[] updateFitness, 
			boolean countVictoriesOnly, 
			int[] subpopulation, 
			int threadnum) {
		
//		state.output.println("Evaluating individuals...", threadnum);
    
		// TODO Auto-generated method stub
		if( ind.length != 2 || updateFitness.length != 2 )
            state.output.fatal( "The InternalSumProblem evaluates only two individuals at a time." );
		
		if( ! (ind[0] instanceof GPIndividual )) {
            state.output.fatal( "The individuals in the PredatorPrey should be GPIndividual." );
		}
		
		if( ! (ind[1] instanceof GPIndividual )) {
            state.output.fatal( "The individuals in the PredatorPrey should be GPIndividual." );
		}
		
//		int size = 0;
//		for(int i = 0; i < ind.length; i++) {
//			if(! (ind[i] instanceof GPIndividual)) {
//				state.output.error("Individual " + i + "in coevolution is not a GPIndividual.");
//			}
//			else {
//				GPIndividual t = (GPIndividual)(ind[i]);
//				size += t.trees.length;
//			}
//		}
		
		int value1 = 0;
		int value2 = 0;
		
		GPIndividual temp;
		
		temp = (GPIndividual)ind[0];
		
		for(int i = 0; i < temp.trees.length; i++) {
			if(temp.trees[i] != null) {
				value1++;
			}
		}
		
		temp = (GPIndividual)ind[1];
		for(int i = 0; i < temp.trees.length; i++) {
			if(temp.trees[i] != null) {
				value2++;
			}
		}
		
		double score = value1 - value2;
//				
		KozaFitness fit1 = (KozaFitness)(ind[0].fitness);
		KozaFitness fit2 = (KozaFitness)(ind[1].fitness);
		
		if(updateFitness[0]) {
			fit1.trials.add(new Double(score));
			fit1.setStandardizedFitness(state, score);
		}
		
		if(updateFitness[1]) {
			fit2.trials.add(new Double(score));
			fit2.setStandardizedFitness(state, score);
		}
	}
	
	@Override
	public void preprocessPopulation(EvolutionState state, 
			Population pop, 
			boolean[] prepareForAssessment, 
			boolean countVictoriesOnly) {
		// TODO Auto-generated method stub
		// evaluate population 1's individual with population 2's individual
		// and vice versa
		// evaluate the fitness of both population here, and update the fitness of both population. 
//		System.out.println("Hello hridoy");
//		for(int i = 0; i < pop.subpops.length; i++) {
//			Subpopulation subpop = pop.subpops[i];
//			for(int j = 0; j < subpop.individuals.length; j++) {
//				KozaFitness fit = (KozaFitness) (subpop.individuals[j].fitness);
//				state.output.fatal( "Individual[ " + j + "] = " + subpop.individuals[j].fitness + "Fitness" );
//				fit.trials = new ArrayList<KozaFitness>();
//			}
//		}
		
		for(int i = 0; i < pop.subpops.length; i++ )
            if (prepareForAssessment[i])
                for(int j = 0; j < pop.subpops[i].individuals.length ; j++ )
                    ((KozaFitness)(pop.subpops[i].individuals[j].fitness)).trials = new ArrayList();
	}	
	
	@Override
	public void postprocessPopulation(EvolutionState state, Population pop, boolean[] assessFitness, boolean countVictoriesOnly) {
		// TODO Auto-generated method stub
		// evaluate the fitness in terms of how one individual performed in the opposition's population
//		Finish processing the population (such as fitness information) after evaluation. Although this method is not static, you should not use it to write to any instance variables in the GroupedProblem instance; this is because it's possible that the instance used is in fact the prototype, and you will have no guarantees that your instance variables will remain valid during the evaluate(...) process. Do not assume that pop will be the same as state.pop -- it may not. state is only provided to give you access to EvolutionState features.
//		countVictoriesOnly will be set if Individuals' fitness is to be based on whether they're the winner of a test, instead of based on the specifics of the scores in the tests. This really only happens for Single-Elimination Tournament one-population competitive coevolution. If this is set, probably would leave the Fitnesses as they are here (they've been set and incremented in evaluate(...)), but if it's not set, you may want to set the Fitnesses to the maximum or average or the various trials performed.
//
//		assessFitness will indicate which subpopulations should have their final fitness values assessed. You should not clear the trials of individuals for which assessFitness[] is false. Instead allow trials to accumulate and ultimately update the fitnesses later when the flag is set. assessFitness[] may not be the same as updateFitness[] in evaluate(...).
//
//		Should return the number of individuals evaluated (not tested: but actually had their fitnesses modified -- or would have if the evaluated flag wasn't set).
		
//		System.out.println("Hello Iamin");
		
		
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
}
