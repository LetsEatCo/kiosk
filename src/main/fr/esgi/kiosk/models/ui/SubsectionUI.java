package main.fr.esgi.kiosk.models.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.fr.esgi.kiosk.controllers.ProductCompositionController;
import main.fr.esgi.kiosk.helpers.UIHelper;
import main.fr.esgi.kiosk.models.MealSubsection;

import java.util.ArrayList;

public class SubsectionUI extends VBox {
    private long minSelections;
    private long maxSelections;
    private long currentSelections=0;
    private ProductCompositionController productCompositionController;
    private MealSubsection mealSubsection;

    public SubsectionUI(MealSubsection mealSubsection, ProductCompositionController productCompositionController) {

        this.productCompositionController = productCompositionController;
        this.mealSubsection = mealSubsection;
        this.minSelections = mealSubsection.getMinSelectionsPermitted();
        this.maxSelections = mealSubsection.getMaxSelectionsPermitted();

        initUI();

    }

    private void initUI() {

        Label subsectionName = new Label(mealSubsection.getName() + " (UP TO " + mealSubsection.getMaxSelectionsPermitted()+")");
        subsectionName.getStyleClass().add("option-subsection-name");

        HBox titleContainer = new HBox(subsectionName);
        titleContainer.setPadding(new Insets(0,0,50,0));

        this.getChildren().add(titleContainer);

        ArrayList<OptionMealUI> optionProductsUIArrayList = UIHelper.createProductsElementsOptionsUI(mealSubsection.getProducts(), productCompositionController, this);
        ArrayList<OptionMealUI> optionIngredientsUIArrayList = UIHelper.createProductsElementsOptionsUI(mealSubsection.getIngredients(), productCompositionController, this);

        this.getChildren().addAll(optionIngredientsUIArrayList);
        this.getChildren().addAll(optionProductsUIArrayList);
    }

    public long getMinSelections() {
        return minSelections;
    }

    public void setMinSelections(long minSelections) {
        this.minSelections = minSelections;
    }

    public long getMaxSelections() {
        return maxSelections;
    }

    public void setMaxSelections(long maxSelections) {
        this.maxSelections = maxSelections;
    }

    public long getCurrentSelections() {
        return currentSelections;
    }

    public void setCurrentSelections(long currentSelections) {
        this.currentSelections = currentSelections;
    }
}
