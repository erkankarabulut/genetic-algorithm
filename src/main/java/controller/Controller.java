package main.java.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.nashorn.internal.ir.Labels;
import main.java.algorithm.GeneticAlgorithm;
import main.java.util.MathUtil;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML Spinner matrixSize;
    @FXML Spinner individualSize;
    @FXML Spinner individualCount;
    @FXML Spinner foodCount;
    @FXML Spinner maxIteration;

    @FXML CheckBox visualizeAll;

    @FXML Button simulateButton;

    private int matrixSizeValue;
    private int individualSizeValue;
    private int individualCountValue;
    private int foodCountValue;
    private int maxIterationValue;
    private int centerX;
    private int centerY;

    private boolean visualizeAllValue;

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
        maxIterationValue       = 10000;
        visualizeAllValue       = false;

        simulateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                ArrayList<ArrayList<Integer>> individuals = new ArrayList<ArrayList<Integer>>();
                ArrayList<ArrayList<Integer>> matrix      = mathUtil.createMatrix(matrixSizeValue, foodCountValue, foodMark);
                centerX = matrixSizeValue / 2;
                centerY = matrixSizeValue / 2;

                for(int i=0; i<individualCountValue; i++){
                    individuals.add(mathUtil.createRandomIndividual(individualSizeValue, possibilities));
                }

                ArrayList<ArrayList<ArrayList<Integer>>> geneticAlgorithmResults = geneticAlgorithm.applyGeneticAlgorithm(matrix, individuals, centerX, centerY, foodCountValue, maxIterationValue);
                final ArrayList<ArrayList<Integer>> bestOfAllPopulations = geneticAlgorithmResults.get(1);
                ArrayList<ArrayList<Integer>> finalPopulation = geneticAlgorithmResults.get(0);

                if(finalPopulation.size() == 0){
                    System.out.println("Could not find the solution");
                }else {

                    if(visualizeAllValue){

                        VisualizeAllThread visualizationThread = new VisualizeAllThread(matrix, bestOfAllPopulations, centerX, centerY);

                        Timeline drawPath = new Timeline(new KeyFrame(Duration.millis(100 * bestOfAllPopulations.get(0).size()), visualizationThread));
                        drawPath.setCycleCount(bestOfAllPopulations.size());
                        drawPath.play();

                    }else {
                        ArrayList<Integer> bestOfLastPopulation = geneticAlgorithm.findMostAte(finalPopulation, matrix, centerX, centerY);
                        VisualizationThread visualizationThread = new VisualizationThread(matrix, bestOfLastPopulation, centerX, centerY, true, 0);

                        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(100), visualizationThread));
                        fiveSecondsWonder.setCycleCount(bestOfLastPopulation.size());
                        fiveSecondsWonder.play();
                    }

                }

            }
        });

    }

    public class VisualizeAllThread implements EventHandler<ActionEvent> {

        private ArrayList<ArrayList<Integer>> matrix;
        private ArrayList<ArrayList<Integer>> allPopulations;
        private int currentX;
        private int currentY;
        private int populationIndex;

        public VisualizeAllThread(ArrayList<ArrayList<Integer>> matrix, ArrayList<ArrayList<Integer>> allPopulations, int currentX, int currentY){
            this.matrix   = matrix;
            this.allPopulations = allPopulations;
            this.currentY = currentY;
            this.currentX = currentX;
            this.populationIndex = 0;
        }

        public void run() {

            VisualizationThread visualizationThread = new VisualizationThread(matrix, allPopulations.get(populationIndex), centerX, centerY,
                    populationIndex != allPopulations.size()-1 ? false : true, populationIndex);

            Timeline drawPath = new Timeline(new KeyFrame(Duration.millis(100), visualizationThread));
            drawPath.setCycleCount(allPopulations.get(populationIndex).size());
            drawPath.play();

            populationIndex++;

        }

        public int getCurrentX() {
            return currentX;
        }

        public void setCurrentX(int currentX) {
            this.currentX = currentX;
        }

        public int getCurrentY() {
            return currentY;
        }

        public void setCurrentY(int currentY) {
            this.currentY = currentY;
        }

        public void handle(ActionEvent event) {
            run();
        }
    }

    public class VisualizationThread implements EventHandler<ActionEvent> {

        private ArrayList<ArrayList<Integer>> matrix;
        private ArrayList<Integer> solution;
        private int currentX;
        private int currentY;
        private int index;
        private int attempt;
        private boolean isSuccessful;
        private Stage primaryStage;

        public VisualizationThread(ArrayList<ArrayList<Integer>> matrix, ArrayList<Integer> solution, int currentX, int currentY, boolean isSuccessful, int attempt){
            this.matrix   = matrix;
            this.solution = solution;
            this.currentY = currentY;
            this.currentX = currentX;
            this.isSuccessful = isSuccessful;
            this.attempt = attempt;
            this.primaryStage = new Stage();
            this.index = 0;
        }

        public void run() {

            BorderPane root = new BorderPane();

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.setVgap(10);

            StackPane[][] screen_buttons = new StackPane[matrix.size()][matrix.size()];

            for (int y=0;y<matrix.size();y++) {
                for (int x=0;x<matrix.get(y).size();x++) {
                    screen_buttons[y][x] = new StackPane();
                    Rectangle rec = new Rectangle(60,60);
                    if(x == currentX && y == currentY){
                        if(currentX>=matrix.size() || currentY>=matrix.size()){
                            break;
                        }
                        rec.setFill(Color.GREEN);
                    }else {
                        rec.setFill(matrix.get(x).get(y) == 0 ? Color.YELLOW : Color.RED);
                    }
                    rec.setStyle("-fx-arc-height: 20; -fx-arc-width: 20;");
                    screen_buttons[y][x].getChildren().addAll(rec);
                    grid.add(screen_buttons[y][x], x, y);
                }

                if(currentY>=matrix.size() || currentX>=matrix.size()){
                    break;
                }
            }

            //container for controls
            GridPane controls = new GridPane();

            root.setCenter(grid);
            root.setBottom(controls);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(isSuccessful ? "Correct Path" : "Unsuccessful Attempt: " + attempt);
            primaryStage.show();

            switch (solution.get(index)){
                case 1:
                    setCurrentY(getCurrentY()-1);
                    break;
                case 2:
                    setCurrentX(getCurrentX()-1);
                    break;
                case 3:
                    setCurrentY(getCurrentY()+1);
                    break;
                case 4:
                    setCurrentX(getCurrentX()+1);
                    break;
            }

            index++;

        }

        public int getCurrentX() {
            return currentX;
        }

        public void setCurrentX(int currentX) {
            this.currentX = currentX;
        }

        public int getCurrentY() {
            return currentY;
        }

        public void setCurrentY(int currentY) {
            this.currentY = currentY;
        }

        public void handle(ActionEvent event) {
            run();
        }
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

        maxIteration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE));
        maxIteration.getValueFactory().setValue(new Integer(10000));
        maxIteration.setEditable(true);

        visualizeAll.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                visualizeAllValue = visualizeAll.isSelected();
            }
        });

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

        maxIteration.getValueFactory().valueProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                maxIterationValue = Integer.parseInt(maxIteration.getValueFactory().getValue().toString());
            }
        });

        visualizeAll.setSelected(false);

    }

}
