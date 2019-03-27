package main.java.algorithm;

import java.util.*;

public class GeneticAlgorithm {

    public ArrayList<Integer> applyGeneticAlgorithm(ArrayList<ArrayList<Integer>> matrix, ArrayList<ArrayList<Integer>> population, int centerX, int centerY, int foodCount){

        ArrayList<Integer> bestIndividual = null;

        ArrayList<Integer> x;
        ArrayList<Integer> y;
        ArrayList<Integer> newChild;

        int iterationCount = 1000;
        int counter = 0;

        while ((bestIndividual = checkStopCondition(population, matrix, centerX, centerY, foodCount)) == null && counter++ < iterationCount){

            ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<ArrayList<Integer>>();
            for(int i=0; i<population.size(); i++){
                x = selectRandomIndividual(population, matrix, centerX, centerY);
                y = selectRandomIndividual(population, matrix, centerX, centerY);

                newChild = reproduce(x, y);
                if(checkIfMutationRequired(population, matrix, centerX, centerY)){
                    newChild = mutate(newChild);
                }

                newPopulation.add(newChild);
            }

            population.clear();
            population.addAll(newPopulation);
        }

        if(counter >= iterationCount){
            bestIndividual = null;
        }

        return bestIndividual;
    }

    public ArrayList<Integer> checkStopCondition(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY, int foodCount){
        ArrayList<Integer> result = null;

        for(int i=0; i<population.size(); i++){
            if(foodCount == fitnessFunction(population.get(i), matrix, centerX, centerY)){
                result = population.get(i);
                break;
            }
        }

        return result;
    }

    public boolean checkIfMutationRequired(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        boolean result = false;

        // copy population and remove duplicates in order to calculate possibilities for individuals.
        ArrayList<ArrayList<Integer>> copyOfPopulation = copyMatrixWithoutDuplicates(population);

        ArrayList<Integer> eatenFoods = new ArrayList<Integer>();
        float totalEatenFoods = 0;
        float max = 0;
        for(int i=0; i<copyOfPopulation.size(); i++){
            int eatenFood = fitnessFunction(copyOfPopulation.get(i), matrix, centerX, centerY);
            eatenFoods.add(eatenFood);
            totalEatenFoods += eatenFood;
            if(eatenFood > max){
                max = eatenFood;
            }
        }

        if(max / totalEatenFoods >= 0.7){
            result = true;
        }

        return result;
    }

    public int fitnessFunction(ArrayList<Integer> individual, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        int result = 0;
        HashMap<Integer, Integer> eatenFoodsIndexes = new HashMap<Integer, Integer>();

        if(matrix.get(centerX).get(centerY) == 1){
            if(eatenFoodsIndexes.get(centerX) != null){
                if(!(eatenFoodsIndexes.get(centerX) == centerY)){
                    result++;
                    eatenFoodsIndexes.put(centerX, centerY);
                }
            }else{
                result++;
                eatenFoodsIndexes.put(centerX, centerY);
            }
        }

        for(int direction : individual){
            switch (direction){
                case 1:
                    centerX--;
                    break;
                case 2:
                    centerY--;
                    break;
                case 3:
                    centerX++;
                    break;
                case 4:
                    centerY++;
                    break;
            }

            if(centerX >= matrix.size() || centerY >= matrix.size() || centerX < 0 || centerY < 0){
                break;
            }

            if(matrix.get(centerX).get(centerY) == 1){
                if(eatenFoodsIndexes.get(centerX) != null){
                    if(!(eatenFoodsIndexes.get(centerX) == centerY)){
                        result++;
                        eatenFoodsIndexes.put(centerX, centerY);
                    }
                }else{
                    result++;
                    eatenFoodsIndexes.put(centerX, centerY);
                }
            }
        }

        return result;
    }

    public ArrayList<Integer> reproduce(ArrayList<Integer> x, ArrayList<Integer> y){
        ArrayList<Integer> newchild = new ArrayList<Integer>();
        int splitPoint = new Random().nextInt(x.size()) + 1;

        for(int i=0; i<splitPoint; i++){
            newchild.add(x.get(i));
        }

        for(int i=splitPoint; i<y.size(); i++){
            newchild.add(y.get(i));
        }

        return newchild;
    }

    public ArrayList<Integer> mutate(ArrayList<Integer> individual){
        ArrayList<Integer> mutant = new ArrayList<Integer>();
        int index = new Random().nextInt(individual.size());
        int value = new Random().nextInt(4) + 1;

        for(int i=0; i<individual.size(); i++){
            if(i != index){
                mutant.add(individual.get(i));
            }else {
                mutant.add(value);
            }
        }

        return mutant;
    }

    public ArrayList<Integer> selectRandomIndividual(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){

        ArrayList<Integer> eatenFoods = new ArrayList<Integer>();
        ArrayList<Integer> selectedIndividuals = new ArrayList<Integer>();
        int totalDist = 0;

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

        for(int i=0; i<eatenFoods.size(); i++){
            if((eatenFoods.get(i) + temp) >= randomSelection){
                selectedIndividuals = population.get(i);
                break;
            }

            temp += eatenFoods.get(i);
        }

        return selectedIndividuals;
    }

    public ArrayList<ArrayList<Integer>> copyMatrixWithoutDuplicates(ArrayList<ArrayList<Integer>> matrix){
        ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<matrix.size(); i++){
            copy.add(new ArrayList<Integer>());
            for(int j=0; j<matrix.get(i).size(); j++){
                copy.get(i).add((matrix.get(i).get(j)));
            }
        }

        Set<ArrayList<Integer>> tempSet = new HashSet<ArrayList<Integer>>(copy);
        copy.clear();
        copy.addAll(tempSet);

        return copy;
    }

}
