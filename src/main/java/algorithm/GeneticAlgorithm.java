package main.java.algorithm;

import java.util.*;

public class GeneticAlgorithm {

    // This method applies genetic algorithm
    // @param matrix: The matrix consists of 0s for blanks and 1s for foods
    // @param population: Initial generation
    // @param centerX: x coordinate of center of matrix
    // @param centerY: y coordinate of center of matrix
    // @param foodCount: total food count
    // @param iterationCount: total generation count to try to find the result
    // @param mutationRate: mutation rate
    public ArrayList<ArrayList<ArrayList<Integer>>> applyGeneticAlgorithm(ArrayList<ArrayList<Integer>> matrix, ArrayList<ArrayList<Integer>> population, int centerX, int centerY,
                                                                          int foodCount, int iterationCount, double mutationRate){

        ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<Integer>> bestIndividuals = null;
        ArrayList<ArrayList<Integer>> bestOfAllPopulations = new ArrayList<ArrayList<Integer>>();

        // For current generation, find best in order to visualize later (Program can visualize most successful individuals of every generation as well as only the last one which is the result of algorithm)
        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
        temp.addAll(population);
        bestOfAllPopulations.add(findMostAte(temp, matrix, centerX, centerY));

        ArrayList<Integer> x;
        ArrayList<Integer> y;
        ArrayList<Integer> newChild;

        int counter = 0;

        // Stop after finding all the foods or when generation exceeds the limit value
        while ((bestIndividuals = checkStopCondition(population, matrix, centerX, centerY, foodCount)).size() == 0 && counter++ < iterationCount){

            ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<ArrayList<Integer>>();
            // Create new generation
            for(int i=0; i<population.size(); i++){
                x = selectRandomIndividual(population, matrix, centerX, centerY);
                y = selectRandomIndividual(population, matrix, centerX, centerY);

                newChild = reproduce(x, y);
                newPopulation.add(newChild);
            }

            // Mutate new generation according to mutation rate
            newPopulation = mutate(newPopulation, mutationRate);

            // Save new generation
            population.clear();
            population.addAll(newPopulation);

            ArrayList<ArrayList<Integer>> tempList = new ArrayList<ArrayList<Integer>>();
            tempList.addAll(population);

            // save best of this generation as well in a separate list in order to visualize it later
            bestOfAllPopulations.add(findMostAte(tempList, matrix, centerX, centerY));
        }

        // add last generation to lists
        bestOfAllPopulations.add(findMostAte(bestIndividuals, matrix, centerX, centerY));
        result.add(bestIndividuals);
        result.add(bestOfAllPopulations);

        return result;
    }

    // This method finds the individuals which ate most food in a generation
    // @param population: current generation
    // @param matrix: the matrix consists of 0s for blanks and 1s for foods
    // @param centerX: x coordinate of center of matrix
    // @param centerY: y coordinate of center of matrix
    public ArrayList<Integer> findMostAte(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        ArrayList<Integer> result = null;

        int max = 0;
        int foodCount = 0;

        // find food count for every individual in a generation and then find max of them
        for(int i=0; i<population.size(); i++){
            int tempFoodCount = fitnessFunction(population.get(i), matrix, centerX, centerY);
            if(foodCount < tempFoodCount){
                foodCount = tempFoodCount;
                max = i;
            }
        }

        if(!(population.size() == 0)){
            result = new ArrayList<Integer>();
            result.addAll(population.get(max));
        }

        return result;
    }

    // This method checks if stop condition for genetic algorithm is took place or not
    // @param population: current generation
    // @param matrix: The matrix consists of 0s for blanks and 1s for foods
    // @param centerX: x coordinate of center of matrix
    // @param centerY: y coordinate of center of matrix
    // @param foodCount: total food count
    public ArrayList<ArrayList<Integer>> checkStopCondition(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY, int foodCount){
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<population.size(); i++){
            if(foodCount == fitnessFunction(population.get(i), matrix, centerX, centerY)){
                result.add(population.get(i));
            }
        }

        return result;
    }

/*    public boolean checkIfMutationRequired(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY, double mutationRate){
        boolean result = false;

        // copy population and remove duplicates in order to calculate possibilities for individuals.
        ArrayList<ArrayList<Integer>> copyOfPopulation = copyMatrix(population);

        int x = new Random().nextInt(100);
        if(x < (mutationRate * 100)){
            result = true;
        }

        return result;
    }*/


    // This method finds if a certain coordinate is visited before or not
    // @param xList: list of x coordinates
    // @param yList: list of y coordinates
    // @param x: x coordinate
    // @param y: y coordinate
    public boolean checkIfEatenBefore(ArrayList<Integer> xList, ArrayList<Integer> yList, int x, int y){
        boolean result = false;

        // check if x and y visited before
        for(int i=0; i<xList.size(); i++){
            if(xList.get(i) == x && yList.get(i) == y){
                result = true;
                break;
            }
        }

        return result;
    }

    // This method returns the total food count eaten by a certain individual
    // @param matrix: The matrix consists of 0s for blanks and 1s for foods
    // @param individual: Current generation
    // @param centerX: x coordinate of center of matrix
    // @param centerY: y coordinate of center of matrix
    public int fitnessFunction(ArrayList<Integer> individual, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        int result = 0;
        ArrayList<Integer> eatenFoodsX = new ArrayList<Integer>();
        ArrayList<Integer> eatenFoodsY = new ArrayList<Integer>();

        // Check first coordinates
        if(matrix.get(centerY).get(centerX) == 1){
            if(!checkIfEatenBefore(eatenFoodsX, eatenFoodsY, centerX, centerY)){
                result++;
                eatenFoodsX.add(centerX);
                eatenFoodsY.add(centerY);
            }
        }

        // check for every coordinate according to individual that if that point contains a food or not
        for(int i=0; i<individual.size(); i++){
            switch (individual.get(i)){
                case 1:
                    centerY--;
                    break;
                case 2:
                    centerX--;
                    break;
                case 3:
                    centerY++;
                    break;
                case 4:
                    centerX++;
                    break;
            }

            if(centerX >= matrix.size() || centerY >= matrix.size() || centerX < 0 || centerY < 0){
                break;
            }

            // Check if that certain coordinate contains food
            if(matrix.get(centerY).get(centerX) == 1){
                if(!checkIfEatenBefore(eatenFoodsX, eatenFoodsY, centerX, centerY)){
                    result++;
                    eatenFoodsX.add(centerX);
                    eatenFoodsY.add(centerY);
                }
            }
        }

        return result;
    }

    // This method produces an individual from 2 of other individuals by applying a crossover (roulette wheel)
    // @param x: first individual
    // @param y: second individual
    public ArrayList<Integer> reproduce(ArrayList<Integer> x, ArrayList<Integer> y){
        ArrayList<Integer> newchild = new ArrayList<Integer>();

        // Find split point randomly (roulette wheel)
        int splitPoint = new Random().nextInt(x.size()) + 1;

        // split the individuals to create a new one
        for(int i=0; i<splitPoint; i++){
            newchild.add(x.get(i));
        }

        for(int i=splitPoint; i<y.size(); i++){
            newchild.add(y.get(i));
        }

        return newchild;
    }

    // This method does a mutation on a generation
    // @param generation: Current generation
    // @param mutationRate: mutation rate
    public ArrayList<ArrayList<Integer>> mutate(ArrayList<ArrayList<Integer>> generation, double mutationRate){
        ArrayList<ArrayList<Integer>> mutants = copyMatrix(generation);

        if(generation.size() != 0){

            // Find gene count to mutate according to mutation rate
            int geneCountToMutate = ((Double) (mutationRate * generation.size() * generation.get(0).size())).intValue();

            // mutate genes
            for(int i=0; i<geneCountToMutate; i++){
                // find index and value of the gene which is about to be mutated
                int value = new Random().nextInt(4) + 1;
                int index = new Random().nextInt(generation.size() * generation.get(0).size());

                int temp = generation.get(0).size();
                int generationIndex = 0;

                // find next gene to mutate
                while (temp < index){
                    temp += generation.get(0).size();
                    generationIndex++;
                }

                // save mutated version
                mutants.get(generationIndex)
                        .set((index % mutants.get(0).size()), value);
            }
        }

        return mutants;
    }

    // select random individual according to fitness function randomly
    // @param matrix: The matrix consists of 0s for blanks and 1s for foods
    // @param population: Current generation
    // @param centerX: x coordinate of center of matrix
    // @param centerY: y coordinate of center of matrix
    public ArrayList<Integer> selectRandomIndividual(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){

        ArrayList<Integer> eatenFoods = new ArrayList<Integer>();
        ArrayList<Integer> selectedIndividuals = new ArrayList<Integer>();
        int totalDist = 0;

        // find food counts in order to use them while choosing one individual randomly
        for(int i=0; i<population.size(); i++){
            int foodCount = fitnessFunction(population.get(i), matrix, centerX, centerY);
            totalDist += foodCount;
            eatenFoods.add(foodCount);
        }

        int temp = 0;
        int randomSelection = 0;

        if(totalDist > 0){
            randomSelection = new Random().nextInt(totalDist);
        }

        // find individual according to random selection
        for(int i=0; i<eatenFoods.size(); i++){
            if((eatenFoods.get(i) + temp) >= randomSelection){
                selectedIndividuals = population.get(i);
                break;
            }

            temp += eatenFoods.get(i);
        }

        return selectedIndividuals;
    }

    // This method copies a matrix
    // @param matrix: The matrix consists of 0s for blanks and 1s for foods
    public ArrayList<ArrayList<Integer>> copyMatrix(ArrayList<ArrayList<Integer>> matrix){
        ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();

        // copy the matrix
        for(int i=0; i<matrix.size(); i++){
            copy.add(new ArrayList<Integer>());
            for(int j=0; j<matrix.get(i).size(); j++){
                copy.get(i).add((matrix.get(i).get(j)));
            }
        }

        return copy;
    }

}
