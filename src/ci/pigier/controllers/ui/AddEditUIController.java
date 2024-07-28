package ci.pigier.controllers.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;
import ci.pigier.DatabaseConnection;
import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class AddEditUIController extends BaseController implements Initializable {

    @FXML
    private TextArea descriptionTxtArea;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField titleTxtFld;

    @FXML
    void doBack(ActionEvent event) throws IOException {
    	navigate(event, FXMLPage.LIST.getPage());
    }

    @FXML
    void doClear(ActionEvent event) {
    	titleTxtFld.clear();
        descriptionTxtArea.clear();
    }

    @FXML
    void doSave(ActionEvent event) throws IOException {
    	
    	String title = titleTxtFld.getText();
        String description = descriptionTxtArea.getText();
    	
        if (titleTxtFld.getText().trim().equals("")
                || descriptionTxtArea.getText().trim().equals("")) {
        	alert = new Alert(AlertType.WARNING);
            alert.setTitle("Attention !");
            alert.setHeaderText("Mauvaise donnée ");
            alert.setContentText("Le champ titre ou description ne doit pas être vide!");
            alert.showAndWait();
            return;
        }

        
        //requête sql d'enregistrement ou de modification en fonction de l'etat des champs
        //si les champs sont vides => enregistrement sinon modification
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (Objects.nonNull(editNote)) {
                // Mettre à jour la note existante
                String sql = "UPDATE NOTE SET titre_note = ?, description = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, title);
                    statement.setString(2, description);
                    statement.setInt(3, editNote.getId());
                    statement.executeUpdate();

                    // Maj la note dans la liste de donnée
                    editNote.setTitle(title);
                    editNote.setDescription(description);
                }
            } else {
                //Ajouter une nouvelle note 
                String sql = "INSERT INTO NOTE (titre_note, description) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, title);
                    statement.setString(2, description);
                    statement.executeUpdate();

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newId = generatedKeys.getInt(1);
                            Note newNote = new Note(newId, title, description);
                            data.add(newNote);
                        } else {
                            throw new SQLException("Creating note failed, no ID obtained.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //nettoyage après ajout ou modification des champs titre et description
        editNote = null;
        
        navigate(event, FXMLPage.LIST.getPage());
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	    if (Objects.nonNull(editNote)) {
	        titleTxtFld.setText(editNote.getTitle());
	        descriptionTxtArea.setText(editNote.getDescription());
	        saveBtn.setText("Update");
	    }else {
	    	saveBtn.setText("Save");
	    }
	}

}