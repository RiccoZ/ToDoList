package com.ricco.todolist;

import com.ricco.todolist.datamodel.TodoData;
import com.ricco.todolist.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea todoDetailTextArea;
    @FXML
    private Label deadlineLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private HBox hb;

    public void initialize() {

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if (newValue != null) {
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    todoDetailTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d MMMM yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                    hb.setVisible(true);
                }
            }
        });

        todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        if(todoListView.getSelectionModel().getSelectedItem() == null) {
            hb.setVisible(false);
        }
    }

    @FXML
    public void deleteItem() {
        TodoData.getInstance().getTodoItems().remove(todoListView.getSelectionModel().getSelectedItem());
        todoListView.getItems().remove(todoListView.getSelectionModel().getSelectedItem());


        if(todoListView.getSelectionModel().getSelectedItem() == null) {
            todoDetailTextArea.setText(null);
            deadlineLabel.setText(null);
            hb.setVisible(false);
        }
    }

    @FXML
    public void showNewItemDialog() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());

        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource("todoitemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlloader.load());

        } catch(IOException e) {
            System.out.println("Konnte Dialogfeld nicht laden!");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlloader.getController();
            controller.processResults();
            todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
            System.out.println("Okay pressed");
        } else {
            System.out.println("Cancel pressed");
        }

    }

    public void close() {
        System.exit(0);
    }
}
