package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Integer> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		txtResult.clear();
		txtResult.appendText(model.trovaSequenza(boxMese.getValue()));
		txtResult.appendText("\nIl costo della soluzione è: "+ model.getCostoMigliorPercorso());
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		txtResult.clear();
		txtResult.appendText(model.getUmiditaMedia(boxMese.getValue()));
	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
		for(int i=1; i<13; i++) {
			boxMese.getItems().add(i);
		}
	}

	public void setModel(Model model) {
		this.model=model;
	}

}
