package ci.pigier.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import ci.pigier.DatabaseConnection;
import ci.pigier.model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class BaseController {
	protected Alert alert;
	protected static Note editNote = null;
	
	//implementer un programme permettant d'afficher les notes de la Table Note
	protected static ObservableList<Note> data = FXCollections.<Note>observableArrayList();
	
	// Méthode pour compter les notes
	
	
	//Method to add note in db
	/*public void addNote(Note note) throws SQLException {
        String insertQuery = "INSERT INTO NOTE (title, description) VALUES (?, ?)";
        //String generatedColumns[] = { "ID" };
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, note.getTitle());
            statement.setString(2, note.getDescription());
            statement.executeUpdate();
            
            /*try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            	if(generatedKeys.next()) {
            		note.setId(generatedKeys.getInt(1));
            	}
            }
        }
    }*/
	
	 //Methode affichage de toutes les notes de la BD
	public void selectNotes() {
		String listNotes = "SELECT id, titre_note, description FROM NOTE";
		
				try(Connection cnx = DatabaseConnection.getConnection();
						Statement stmt = cnx.createStatement();
						ResultSet rs = stmt.executeQuery(listNotes);) {
					while (rs.next()) {
						int id = rs.getInt("id");
		                String title = rs.getString("titre_note");
		                String description = rs.getString("description");
		                data.add(new Note(id, title, description));
		            }
					
				}catch(SQLException e) {
					e.printStackTrace();
				}
	}
	
	//delete method's
    /*public void deleteNoteById(int noteId) {
        String dltQry = "DELETE FROM NOTE WHERE id = ?";
        try (Connection cnx = DatabaseConnection.getConnection();
             PreparedStatement pstmt = cnx.prepareStatement(dltQry)) {

	            pstmt.setInt(1, noteId);
	            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
			 
	protected void navigate(Event event, URL fxmlDocName) throws IOException {
		// Chargement du nouveau document FXML de l'interface utilisateur
		Parent pageParent = FXMLLoader.load(fxmlDocName);
		// Création d'une nouvelle scène
		Scene scene = new Scene(pageParent);
		// Obtention de la scène actuelle
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		// Masquage de l'ancienne scène (facultatif)
		appStage.hide(); // facultatif
		// Définition de la nouvelle scène pour la scène
		appStage.setScene(scene);
		// Affichage de la scène
		appStage.show();
	}
}