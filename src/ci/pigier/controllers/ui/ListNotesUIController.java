package ci.pigier.controllers.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import ci.pigier.DatabaseConnection;
import ci.pigier.NoteTakingApp;
import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ListNotesUIController extends BaseController implements Initializable {

    @FXML
    private TableColumn<?, ?> descriptionTc;

    @FXML
    private Label notesCount;
    
    
    @FXML
    private TableView<Note> notesListTable;
    

    @FXML
    private TextField searchNotes;

    @FXML
    private TableColumn<?, ?> titleTc;

    @FXML
    void doTranslate(ActionEvent event) {
        
        
        
    }
    
    @FXML
    void doDelete(ActionEvent event) {
        Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        
        if (selectedNote != null) {
        	Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("You've selected a note to delete.");
            alert.setContentText("Are you sure to delete this note?");
            
            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType noBtn = new ButtonType("No", ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(yesBtn, noBtn);
            
            alert.showAndWait().ifPresent(buttonType -> {
            	
                if (buttonType == yesBtn) {
                	data.remove(selectedNote);
                	notesCounter();
                	int noteId = selectedNote.getId();
                    System.out.println("Selected Note ID: " + noteId);
                    
                    try (Connection connection = DatabaseConnection.getConnection()) {
                        String deleteSQL = "DELETE FROM NOTE WHERE id = ?";
                        
                        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
                        preparedStatement.setInt(1, selectedNote.getId());
                        preparedStatement.executeUpdate();
                        
                        int affectedRows = preparedStatement.executeUpdate();
                        
                        if (affectedRows > 0) {
                            data.remove(selectedNote);
                            System.out.println("Note deleted from database and removed from UI");
                            System.out.println("Selected Note ID: " + selectedNote.getId());

                        } else {
                            System.out.println("No note found with the specified ID " + selectedNote.getId());
                        }
                    } catch (SQLException e) {
                        System.out.println("Error while deleting the note: " + e.getMessage());
                        System.out.println("Attempting to delete note with ID: ");

                    }
                }
            });
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("No Note Selected");
            alert.setContentText("Please select a note to delete.");
            alert.showAndWait();
        }
    }
    
    @FXML
    void doEdit(ActionEvent event) {
    	
    	Note getSelectedNote = notesListTable.getSelectionModel().getSelectedItem();
    	if(getSelectedNote != null) {
    		editNote = getSelectedNote;
    		try {
				navigate(event, FXMLPage.ADD.getPage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Any note selected");
            alert.setContentText("Select a note to Edit please.");
            alert.showAndWait();
        }
    	
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
    	editNote = null;
    	navigate(event, FXMLPage.ADD.getPage());
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		FilteredList<Note> filteredData = new FilteredList<>(data, n -> true);
		notesListTable.setItems(filteredData);
		notesCounter();
		titleTc.setCellValueFactory(new PropertyValueFactory<>("title"));
		descriptionTc.setCellValueFactory(new PropertyValueFactory<>("description"));
		searchNotes.setOnKeyReleased(e -> {
			filteredData.setPredicate(n -> {
				if (searchNotes.getText() == null || searchNotes.getText().isEmpty())
					return true;
				return n.getTitle().contains(searchNotes.getText())
						|| n.getDescription().contains(searchNotes.getText());
			});
		});
	}
	
	public void notesCounter() {
		notesCount.setText(data.size() + " Notes");
	}
}