package employees;

import employees.dto.DTOEmployees;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import subsidiary.classes.AlertWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static employees.ODBC_PubsBD.updateImagesURL;

public class InfoEmployeesController {

    public ImageView imageView;

    private final FileChooser fileChooser = new FileChooser();
    public GridPane photosButtonsGridPane;
    public Pane photosPane;
    private DTOEmployees dtoEmployees;

    public void initWindow(){
        setPhotoHoverAction();
        if (dtoEmployees.getImagesURL() != null) {
            loadPhoto();
        } else {
            photosButtonsGridPane.add(getAddingPhotoButton(), 0, 0);
        }
    }

    private void setPhotoHoverAction(){
        photosPane.setOnMouseEntered(event -> {
            photosButtonsGridPane.setVisible(true);
            photosPane.setStyle("-fx-background-color: rgb(0, 0, 0, .2)");
        });

        photosPane.setOnMouseExited(event -> {
            photosButtonsGridPane.setVisible(false);
            photosPane.setStyle("-fx-background-color: transparent");
        });
    }

    private void addPhoto() {
        File file;
        if ((file = fileChooser.showOpenDialog(imageView.getScene().getWindow())) != null) {
            String extension = FilenameUtils.getExtension(file.getName());
            if (! extension.equals("png") && ! extension.equals("jpg")) {
                alterAddingError();
                return;
            }
            dtoEmployees.setImagesURL(dtoEmployees.getId().toString()+".png");
            try {
                java.nio.file.Files.copy(file.toPath(), Paths.get(getPhotosPath()+"\\"+dtoEmployees.getImagesURL()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateImagesURL(dtoEmployees.getId(), dtoEmployees.getImagesURL());
            loadPhoto();
        }
    }

    private void loadPhoto(){
        File file;
        if ((file = new File(getPhotosPath()+"\\"+dtoEmployees.getImagesURL())) != null ) {
            imageView.setImage(new Image(file.toURI().toString()));

            photosButtonsGridPane.getChildren().clear();
            Button changingPhotoButton = getChangingPhotoButton();
            photosButtonsGridPane.add(changingPhotoButton, 0, 0);
            photosButtonsGridPane.setMargin(changingPhotoButton, new Insets(0, 0, 5, 0));
            Button removingPhotoButton = getRemovingPhotoButton();
            photosButtonsGridPane.add(removingPhotoButton, 0, 1);
            photosButtonsGridPane.setMargin(removingPhotoButton, new Insets(5, 0, 0, 0));
        }
    }

    private void changePhoto(){
        File newFile;
        if ((newFile = fileChooser.showOpenDialog(imageView.getScene().getWindow())) != null) {
            dtoEmployees.setImagesURL(dtoEmployees.getId().toString()+".png");
            new File(getPhotosPath()+"\\"+dtoEmployees.getImagesURL()).delete();
            try {
                java.nio.file.Files.copy(newFile.toPath(), Paths.get(getPhotosPath()+"\\"+dtoEmployees.getImagesURL()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateImagesURL(dtoEmployees.getId(), dtoEmployees.getImagesURL());
            imageView.setImage(new Image(newFile.toURI().toString()));
        }
    }

    private void removeButton(){
        File file = new File(getPhotosPath()+"\\"+dtoEmployees.getImagesURL());
        dtoEmployees.setImagesURL(null);
        updateImagesURL(dtoEmployees.getId(), dtoEmployees.getImagesURL());
        imageView.setImage(new Image(getClass().getResourceAsStream("/icons/no-photo.png")));
        photosButtonsGridPane.getChildren().clear();
        photosButtonsGridPane.add(getAddingPhotoButton(), 0, 0);
        file.delete();
    }

    private Button getAddingPhotoButton(){
        Button button = new Button("Додати фото");
        button.setTooltip(new Tooltip("Завантажити фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> addPhoto());
        return button;
    }

    private Button getChangingPhotoButton(){
        Button button = new Button("Змінити");
        button.setTooltip(new Tooltip("Завантажити нове фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> changePhoto());
        return button;
    }

    private Button getRemovingPhotoButton(){
        Button button = new Button("Видалити");
        button.setTooltip(new Tooltip("Видалити фото"));
        button.getStylesheets().add(getClass().getResource("/employees/PhotosButtonStyle.css").toExternalForm());
        button.setPrefWidth(140);
        button.setOnAction(event -> removeButton());
        return button;
    }

    private String getPhotosPath(){
        String path = "C:\\Users\\"+ System.getProperty("user.name") +"\\Documents\\CTracking";
        File directory = new File(path);
        if (! directory.exists()) {
            directory.mkdir();
        }
        path = path + "\\photos";
        directory = new File(path);
        if (! directory.exists()) {
            directory.mkdir();
        }
        return path;
    }

    public void setDtoEmployees(DTOEmployees dtoEmployees) {
        this.dtoEmployees = dtoEmployees;
    }

    private void alterAddingError(){
        String contentText = "Зображення повинне відповідати формату *png, або *jpg.";
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.ERROR, null, contentText);
        alertWindow.showError();
    }

}
