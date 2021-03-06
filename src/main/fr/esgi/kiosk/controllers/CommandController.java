package main.fr.esgi.kiosk.controllers;



import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.fr.esgi.kiosk.helpers.StageManagerHelper;
import main.fr.esgi.kiosk.helpers.UIHelper;
import main.fr.esgi.kiosk.models.*;
import main.fr.esgi.kiosk.models.ui.CartElementUI;
import main.fr.esgi.kiosk.models.ui.ElementUI;
import main.fr.esgi.kiosk.models.ui.SectionUI;
import main.fr.esgi.kiosk.plugin.PluginLoader;
import main.fr.esgi.kiosk.routes.StoreRouter;
import main.fr.esgi.kiosk.views.FxmlView;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class CommandController <T extends RessourceElementProduct>  implements FxmlController {

    @FXML
    private VBox sectionsContainer;
    private int adminCounter = 0;
    private Store store;
    private Order order;

    @FXML
    private HBox root;

    @FXML
    private Pane mainContent;

    @FXML
    private VBox cartPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private HBox adminRoot;

    @FXML
    private JFXTextField emailInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXTextField voucherField;

    private final StageManagerHelper stageManagerHelper;
    private ProductCompositionController<T> accompanimentController;
    PluginLoader pluginLoader = new PluginLoader();
    private Cart<T> cart;

    @Autowired @Lazy
    public CommandController(StageManagerHelper stageManagerHelper, Store store, Order order, ProductCompositionController<T> accompanimentController, Cart<T> cart) {
        this.stageManagerHelper = stageManagerHelper;
        this.store = store;
        this.order = order;
        this.accompanimentController = accompanimentController;
        this.cart = cart;
    }

    @Override
    public void initialize() {

        root.setOpacity(0);
        UIHelper.makeFadeInTransition(root);
        lazyLoadProducts();
        loadCartElement(cart);

        // We fill the first section to display
        if(store.getSections().size()>0)createUIElements(store.getSections().get(0));
    }

    public HBox getRoot() {
        return root;
    }

    @FXML
    void adminRegistration() {

        adminCounter +=1;

        if(adminCounter == 10) {

            adminCounter=0;
            UIHelper.makeFadeOutTransition(root,stageManagerHelper,FxmlView.ADMIN_LOGIN);
        }
    }

    @FXML
    void loadPreviousPage() {

        UIHelper.makeFadeOutTransition(root,stageManagerHelper,FxmlView.LOCATION);
    }

    public void focusProductElement(T productElement) {

        accompanimentController.setSelectedProductElement(productElement);
        stageManagerHelper.switchScene(FxmlView.ACCOMPANIMENT);

    }

    @FXML
    void order()  {

        if (cart.size() > 0 ){


            StoreRouter storeRouter = new StoreRouter();

            double reduction = 0;
            try {
                reduction = storeRouter.getVoucher(voucherField.getText());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Make Order");
            alert.setHeaderText(null);
            alert.setContentText("Are You Sure ?");

            Optional<ButtonType> action = alert.showAndWait();

            if(action.get() == ButtonType.OK){

                order.convertCart(cart, reduction);

                UIHelper.makeFadeOutTransition(root, stageManagerHelper, FxmlView.PAYMENT_SCREEN);
            }


        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Order");
            alert.setHeaderText(null);
            alert.setContentText("Empty Cart");
            alert.show();

        }


    }

    private void loadCartElement(Cart<T> cart){

        for(T productElement : cart){
            CartElementUI<T> cartElementUI = new CartElementUI<>(productElement, this);
            cartPane.getChildren().add(cartElementUI);

        }

    }

    public void createUIElements(Section section){


        ArrayList<ElementUI> elementUIS = new ArrayList<>();
        ArrayList<ElementUI> mealsElementUI  = UIHelper.createProductsElementsUI(section.getMeals());
        ArrayList<ElementUI> productsElementUI  = UIHelper.createProductsElementsUI(section.getProducts());

        elementUIS.addAll(mealsElementUI);
        elementUIS.addAll(productsElementUI);

        loadUIContent(elementUIS, mainContent);


    }

    public void addSections(SectionUI sectionUI){
        sectionsContainer.getChildren().add(sectionUI);

    }

    public void removeProductElementToCart(T productElement, CartElementUI cartElementUI){

        cart.remove(productElement);
        cartPane.getChildren().remove(cartElementUI);

    }

    @FXML
    void switchTheme() {

        String css = "assets/css/dark-theme.css";

        processPlugin(css);
    }

    private void processPlugin(String cssPath) {
        try {


            if(pluginLoader.getJarFile()==null){

                File jar = PluginLoader.importJar(stageManagerHelper.getPrimaryStage());
                if(jar!=null) pluginLoader.processSkinChange(this, jar, cssPath);
            }else pluginLoader.processSkinChange(this, pluginLoader.getJarFile(), cssPath);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void defaultTheme() {
        String css = "assets/css/app.css";
        processPlugin(css);
    }

    private <T> void loadUIContent(ArrayList<T> elementUI, Pane content) {

        VBox vBox = new VBox();

        int size = elementUI.size();
        int productsIndexJourney = 0;



            for(int i =0; i<size; i++){

                HBox hBox = new HBox();

                for(int j=productsIndexJourney;j<elementUI.size();j++){

                    hBox.getChildren().add((Node) elementUI.get(productsIndexJourney));
                    productsIndexJourney +=1;
                }

                vBox.getChildren().add(hBox);

            }


        content.getChildren().removeAll();
        content.getChildren().setAll(vBox);
    }

    private void lazyLoadProducts() {

        Sections sections = store.getSections();

        for (Section section : sections) {

            if(section.getMeals().size() > 0 || section.getProducts().size()>0){

                new SectionUI(section, this);
            }

        }

    }

    @FXML
    void login() throws IOException, ParseException {
        String email = emailInput.getText();
        String password = passwordInput.getText();
        StoreRouter storeRouter = new StoreRouter();
        storeRouter.login(email, password);
    }

    @FXML
    void home(ActionEvent event){

        UIHelper.makeFadeOutTransition(adminRoot, stageManagerHelper, FxmlView.COMMAND_HOME);
    }




}
