package main.java.algorithm;

import java.util.*;

public class GeneticAlgorithm {

    public ArrayList<ArrayList<ArrayList<Integer>>> applyGeneticAlgorithm(ArrayList<ArrayList<Integer>> matrix, ArrayList<ArrayList<Integer>> population, int centerX, int centerY, int foodCount, int iterationCount){

        ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<Integer>> bestIndividuals = null;
        ArrayList<ArrayList<Integer>> bestOfAllPopulations = new ArrayList<ArrayList<Integer>>();

        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
        temp.addAll(population);
        bestOfAllPopulations.add(findMostAte(temp, matrix, centerX, centerY));

        ArrayList<Integer> x;
        ArrayList<Integer> y;
        ArrayList<Integer> newChild;

        int counter = 0;

        while ((bestIndividuals = checkStopCondition(population, matrix, centerX, centerY, foodCount)).size() == 0 && counter++ < iterationCount){

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

            ArrayList<ArrayList<Integer>> tempList = new ArrayList<ArrayList<Integer>>();
            tempList.addAll(population);
            bestOfAllPopulations.add(findMostAte(tempList, matrix, centerX, centerY));
        }

        result.add(bestIndividuals);
        result.add(bestOfAllPopulations);

        return result;
    }

    public ArrayList<Integer> findMostAte(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        ArrayList<Integer> result = null;

        int max = 0;
        int foodCount = 0;
        for(int i=0; i<population.size(); i++){
            int tempFoodCount = fitnessFunction(population.get(i), matrix, centerX, centerY);
            if(foodCount < tempFoodCount){
                foodCount = tempFoodCount;
                max = i;
            }
        }

        if(!(population.size() == 0)){
            result = population.get(max);
        }

        return result;
    }

    public ArrayList<ArrayList<Integer>> checkStopCondition(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY, int foodCount){
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<population.size(); i++){
            if(foodCount == fitnessFunction(population.get(i), matrix, centerX, centerY)){
                result.add(population.get(i));
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

    public boolean checkIfEatenBefore(ArrayList<Integer> xList, ArrayList<Integer> yList, int x, int y){
        boolean result = false;

        for(int i=0; i<xList.size(); i++){
            if(xList.get(i) == x && yList.get(i) == y){
                result = true;
                break;
            }
        }

        return result;
    }

    public int fitnessFunction(ArrayList<Integer> individual, ArrayList<ArrayList<Integer>> matrix, int centerX, int centerY){
        int result = 0;
        ArrayList<Integer> eatenFoodsX = new ArrayList<Integer>();
        ArrayList<Integer> eatenFoodsY = new ArrayList<Integer>();

        if(matrix.get(centerX).get(centerY) == 1){
            if(!checkIfEatenBefore(eatenFoodsX, eatenFoodsY, centerX, centerY)){
                result++;
                eatenFoodsX.add(centerX);
                eatenFoodsY.add(centerY);
            }
        }

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

            if(matrix.get(centerX).get(centerY) == 1){
                if(!checkIfEatenBefore(eatenFoodsX, eatenFoodsY, centerX, centerY)){
                    result++;
                    eatenFoodsX.add(centerX);
                    eatenFoodsY.add(centerY);
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
