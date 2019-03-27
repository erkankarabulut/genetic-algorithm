package main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import main.java.algorithm.GeneticAlgorithm;
import main.java.util.MathUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML Spinner matrixSize;
    @FXML Spinner individualSize;
    @FXML Spinner individualCount;
    @FXML Spinner foodCount;

    @FXML Button simulateButton;

    private int matrixSizeValue;
    private int individualSizeValue;
    private int individualCountValue;
    private int foodCountValue;
    private int centerX;
    private int centerY;

    private static final ArrayList<Integer> possibilities = new ArrayList<Integer>(){{add(1); add(2); add(3); add(4);}};
    private static final int foodMark = 1;

    private MathUtil mathUtil;
    private GeneticAlgorithm geneticAlgorithm;

    public void initialize(URL location, ResourceBundle resources) {

        mathUtil = new MathUtil();
        geneticAlgorithm = new GeneticAlgorithm();

        setChangeListeners();

        matrixSizeValue         = 10;
        individualSizeValue     = 10;
        individualCountValue    = 4;
        foodCountValue          = 3;

        simulateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                ArrayList<ArrayList<Integer>> individuals = new ArrayList<ArrayList<Integer>>();
                ArrayList<ArrayList<Integer>> matrix      = mathUtil.createMatrix(matrixSizeValue, foodCountValue, foodMark);
                centerX = matrixSizeValue / 2;
                centerY = matrixSizeValue / 2;

                for(int i=0; i<individualCountValue; i++){
                    individuals.add(mathUtil.createRandomIndividual(individualSizeValue, possibilities));
                }

                ArrayList<Integer> bestIndividual = geneticAlgorithm.applyGeneticAlgorithm(matrix, individuals, centerX, centerY, foodCountValue);

                if(bestIndividual == null){
                    System.out.println("Could not find the solution");
                }else {
                    System.out.println(matrix.toString() + "\n" + individuals.toString() + "\n" + centerX + " - " + centerY + "\n" + bestIndividual.toString());
                }

            }
        });

    }

    public void setChangeListeners(){

        matrixSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));
        matrixSize.getValueFactory().setValue(new Integer(10));
        matrixSize.setEditable(true);

        individualSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));
        individualSize.getValueFactory().setValue(new Integer(10));
        individualSize.setEditable(true);

        individualCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));
        individualCount.getValueFactory().setValue(new Integer(4));
        individualCount.setEditable(true);

        foodCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100));
        foodCount.getValueFactory().setValue(new Integer(3));
        foodCount.setEditable(true);

        matrixSize.getValueFactory().valueProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                matrixSizeValue = Integer.parseInt(matrixSize.getValueFactory().getValue().toString());
            }
        });

        individualSize.getValueFactory().valueProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                individualSizeValue = Integer.parseInt(individualSize.getValueFactory().getValue().toString());
            }
        });

        individualCount.getValueFactory().valueProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                individualCountValue = Integer.parseInt(individualCount.getValueFactory().getValue().toString());
            }
        });

        foodCount.getValueFactory().valueProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                foodCountValue = Integer.parseInt(foodCount.getValueFactory().getValue().toString());
            }
        });

    }

}
