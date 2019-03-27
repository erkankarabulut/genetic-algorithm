package main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jdk.nashorn.internal.ir.Labels;
import main.java.algorithm.GeneticAlgorithm;
import main.java.util.MathUtil;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML Spinner matrixSize;
    @FXML Spinner individualSize;
    @FXML Spinner individualCount;
    @FXML Spinner foodCount;
    @FXML Spinner maxIteration;

    @FXML Button simulateButton;

    private int matrixSizeValue;
    private int individualSizeValue;
    private int individualCountValue;
    private int foodCountValue;
    private int maxIterationValue;
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
        maxIterationValue       = 10000;

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
                    visualize(matrix, bestIndividual);
                }

            }
        });

    }

    public void visualize(ArrayList<ArrayList<Integer>> matrix, ArrayList<Integer> solution){
        BorderPane root = new BorderPane();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        StackPane[][] screen_buttons = new StackPane[matrix.size()][matrix.size()];

        for (int y=0;y<matrix.size();y++) {
            for (int x=0;x<matrix.get(y).size();x++) {
                screen_buttons[y][x] = new StackPane();
                Rectangle rec = new Rectangle(30,30);
                rec.setFill(matrix.get(x).get(y) == 0 ? Color.YELLOW : Color.RED);
                rec.setStyle("-fx-arc-height: 10; -fx-arc-width: 10;");
                screen_buttons[y][x].getChildren().addAll(rec);
                grid.add(screen_buttons[y][x], x, y);
            }
        }

        //container for controls
        GridPane controls = new GridPane();

        root.setCenter(grid);
        root.setBottom(controls);
        Scene scene = new Scene(root);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.show();
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

        maxIteration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100000));
        maxIteration.getValueFactory().setValue(new Integer(10000));
        maxIteration.setEditable(true);

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

    }

}
