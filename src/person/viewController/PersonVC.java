package person.viewController;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import person.model.Geschlecht;
import person.model.Person;
import person.model.PersonException;
import serial.Catalog;

import java.text.NumberFormat;
import java.util.Optional;


public class PersonVC {
  private final VBox root = new VBox();

 private final GridPane fixedfields = new GridPane();
  private final GridPane togglefields = new GridPane();
  private final Label lbSvnr = new Label();
  private final TextField tfSvnr = new TextField();
  private final Label lbNname = new Label();
  private final TextField tfNname = new TextField();
  private final Label lbVname = new Label();
  private final TextField tfVname = new TextField();
  private final Label lbGebDat = new Label();
  private final DatePicker dpGebDat = new DatePicker();
  private final Label lbGroesse = new Label();
  private final TextField tfGroesse = new TextField();
  private final Label lbGeschlecht = new Label();
  private final ChoiceBox cbGeschlecht = new ChoiceBox();
  
  private final HBox buttons = new HBox();
  private final Button btCancel = new Button();
  private final Button btSave = new Button();
  
  private final TextField tfMsg = new TextField();
  
  // Model
  private Person model;
  
  // Helper
  private static final NumberFormat df;


  
  static {
    df = NumberFormat.getNumberInstance();
    df.setMaximumFractionDigits(2);
    df.setMinimumFractionDigits(2);

  }
  
  
  private PersonVC() {
    this.model = new Person();
  }
  
  
  public static void show(Stage stage, Catalog catalog) {
    Person.setCatalog(catalog);
    
    PersonVC personVC = new PersonVC();
    
    // View aufbauen, Handeler hinzufügen, ...
    personVC.init();
    
    // View anzeigen
    Scene scene = new Scene(personVC.root);
    stage.setTitle("Personenwartung");
    stage.setScene(scene);
    stage.show();
  }
  
  
  private void init() {


    // Root
    root.setSpacing(10);
    root.setPadding(new Insets(5, 5, 5, 5));
    


    //fixedFields
    root.getChildren().add(fixedfields);
    fixedfields.setHgap(5);
    fixedfields.setVgap(5);

    // ToggelFields
    root.getChildren().add(togglefields);
    togglefields.setHgap(5);
    togglefields.setVgap(5);

    
    int row = -1;
    
    // - SVNR
    row++;
    lbSvnr.setText("SVNR:");
    fixedfields.add(lbSvnr, 0, row);



    tfSvnr.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if(oldValue){
        changePerson();
      }
    });

    fixedfields.add(tfSvnr, 1, row);

    row = -1;


    // - Nachname
    row++;
    lbNname.setText("Nachname:");

    togglefields.add(lbNname, 0, row);


    togglefields.add(tfNname, 1, row);
    
    // - Vorname
    row++;
    lbVname.setText("Vorname:");

    togglefields.add(lbVname, 0, row);


    togglefields.add(tfVname, 1, row);
    
    // - Geburtsdatum
    row++;

    lbGebDat.setText("Geburtsdatum:");
    togglefields.add(lbGebDat, 0, row);


    togglefields.add(dpGebDat, 1, row);
    
    // - Größe
    row++;

    lbGroesse.setText("Größe:");
    togglefields.add(lbGroesse, 0, row);


    togglefields.add(tfGroesse, 1, row);
    tfGroesse.setAlignment(Pos.CENTER_RIGHT);
    
    // - Geschlecht
    row++;

    lbGeschlecht.setText("Geschlecht:");
    togglefields.add(lbGeschlecht, 0, row);


    togglefields.add(cbGeschlecht, 1, row);
    /*
    List<String> geschlechter = new ArrayList<>();
    geschlechter.add("W");
    geschlechter.add("M");
    ObservableList<String> cbGeschlechtItems = FXCollections.observableList(geschlechter);
    cbGeschlecht.setItems(cbGeschlechtItems);
    */
    cbGeschlecht.setItems(Geschlecht.valuesAsObservableList());
    //cbGeschlecht.setValue(cbGeschlecht.getItems().get(0));
    
    // Buttons
    root.getChildren().add(buttons);

    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.setSpacing(5);
    
    // - Button Cancel
    btCancel.setText("Cancel");
    buttons.getChildren().add(btCancel);
    btCancel.setOnAction(e -> cancel());
    
    // - Button Calc
    btSave.setText("Save");
    buttons.getChildren().add(btSave);
    btSave.setDefaultButton(true);
    btSave.setOnAction(e -> save());
    
    // Message-Bereich
    root.getChildren().add(tfMsg);
    tfMsg.setEditable(false);
    tfMsg.setFocusTraversable(false);

  //Mode
    toggleMode(false);
  }
  
  
  private void save() {
    try {
      // View-Felder in Model speichern
      model.setSvnr(tfSvnr.getText());
      model.setNname(tfNname.getText());
      model.setVname(tfVname.getText());
      model.setGroesse(df.parse(tfGroesse.getText()).doubleValue());
      model.setGebDat(dpGebDat.getValue());
      model.setGeschlecht((Geschlecht) cbGeschlecht.getValue());
      
      // Model speichern
      model.save();
      
      // View zurücksetzen
      clear();

      //Mode zurücksetzen
      toggleMode(false);

      // Nachricht
      tfMsg.setText("Ok, gesichert!");
      tfMsg.setStyle("-fx-text-inner-color: green;");
    }
    catch (Exception ex) {
      // Fehlermeldung
      tfMsg.setText(ex.getMessage());
      tfMsg.setStyle("-fx-text-inner-color: red;");
    }
  }
  
  
  private void cancel() {
    Alert alConfirm = new Alert(Alert.AlertType.CONFIRMATION);
    alConfirm.setHeaderText("Sicher?");
    alConfirm.setContentText("Wirklich Canceln?");
    Optional<ButtonType> result = alConfirm.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      // View zurücksetzen
      clear();

      //Model zurücksetzen
      toggleMode(false);

      // Nachricht
      tfMsg.setText("Ok, Wartung abgebrochen!");
      tfMsg.setStyle("-fx-text-inner-color: green;");
    }
    else {
      // Nachricht
      tfMsg.setText("Ok, kein Abbruch der Wartung vorgenommen!");
      tfMsg.setStyle("-fx-text-inner-color: green;");
    }
  }
  
  
  private void clear() {
    // Nächstes Model
    model = new Person();
    
    // View für nächste Person herrichten
    tfSvnr.setText(null);
    tfNname.setText(null);
    tfVname.setText(null);
    dpGebDat.setValue(null);
    cbGeschlecht.setValue(null);
    tfGroesse.setText(null);
  }

  private void changePerson(){
    System.out.println("Es funktioniert");
     model = model.selectBySvnr(tfSvnr.getText());

     if(model==null){
       model= new Person();
       try {
           model.setSvnr(tfSvnr.getText());
            toggleMode(true);

           tfMsg.setText("Person erfolgreich erstellt");
           tfMsg.setStyle("-fx-text-inner-color: green;");


       } catch (PersonException ex) {
         tfMsg.setText(ex.getMessage());
         tfMsg.setStyle("-fx-text-inner-color: red;");
       }
     }

     else {
       tfGroesse.setText(df.format( model.getGroesse()));
       tfNname.setText(model.getNname());
       tfVname.setText(model.getVname());
       cbGeschlecht.setValue(model.getGeschlecht());
       dpGebDat.setValue(model.getGebDat());
      toggleMode(true);

        tfMsg.setText("Person erfolgreich geladen");
         tfMsg.setStyle("-fx-text-inner-color: green;");
     }
  }

  private void toggleMode(boolean visibel){
      tfSvnr.setEditable(!visibel);
      togglefields.setVisible(visibel);
      buttons.setVisible(visibel);
  }
}
