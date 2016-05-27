package employees;

import employees.dto.DTOEmployees;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static employees.ODBC_PubsBD.updateImagesURL;

public class InfoEmployeesController {

    public ImageView imageView;

    private final FileChooser fileChooser = new FileChooser();
    private DTOEmployees dtoEmployees;

    public void initWindow(){
        if (dtoEmployees.getImagesURL() != null) {
            File file = new File(getPhotosPath()+"\\"+dtoEmployees.getImagesURL());
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    public void addPhoto(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        String newFilesName = dtoEmployees.getId().toString()+".png";

        try {
            java.nio.file.Files.copy(file.toPath(), Paths.get(getPhotosPath()+"\\"+newFilesName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        dtoEmployees.setImagesURL(newFilesName);

        updateImagesURL(dtoEmployees.getId(), newFilesName);

        file = new File(getPhotosPath()+"\\"+newFilesName);

        imageView.setImage(new Image(file.toURI().toString()));
    }

    public void setDtoEmployees(DTOEmployees dtoEmployees) {
        this.dtoEmployees = dtoEmployees;
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
}
