package main.java.util;

import java.util.ArrayList;
import java.util.Random;

public class MathUtil {

    public ArrayList<ArrayList<Integer>> createMatrix(int n, int foodCount, int foodMark){
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<n; i++){
            ArrayList<Integer> row = new ArrayList<Integer>();
            for (int j=0; j<n; j++){
                row.add(0);
            }

            matrix.add(row);
        }

        Random random = new Random();
        for(int i=0; i<foodCount; i++){
            int x = random.nextInt(n);
            int y = random.nextInt(n);

            while (matrix.get(x).get(y) == 1){
                x = random.nextInt(n);
                y = random.nextInt(n);
            }

            matrix.get(x).set(y, foodMark);
        }

        return matrix;
    }

    public ArrayList<Integer> createRandomIndividual(int size, ArrayList<Integer> possibilities){
        ArrayList<Integer> individual = new ArrayList<Integer>();

        Random random = new Random();
        for(int i=0; i<size; i++){
            individual.add(possibilities.get(random.nextInt(possibilities.size())));
        }

        return individual;
    }

}
