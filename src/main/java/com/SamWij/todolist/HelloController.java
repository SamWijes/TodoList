package com.SamWij.todolist;

import com.SamWij.todolist.datamodel.TodoData;
import com.SamWij.todolist.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HelloController {
	@FXML
	private List<TodoItem> todoItems;

	@FXML
	private ListView<TodoItem> todoListView;

	@FXML
	private TextArea itemDetailsTextArea;

	@FXML
	private Label deadlineLabel;

	@FXML
	private BorderPane mainBorderPane;

	public void initialize() {
//		TodoItem item1=new TodoItem("Mail BirthDay Card","buy a card",
//				LocalDate.of(2025, Month.APRIL,23));
//		TodoItem item2=new TodoItem("Doctors appointment","Book appointment on 25th june ",
//				LocalDate.of(2020, Month.AUGUST,13));
//		TodoItem item3=new TodoItem("Pick cleaning","dry cleaners closed on 26h call",
//				LocalDate.of(2015, Month.DECEMBER,17));
//
//		todoItems=new ArrayList<>();
//		todoItems.add(item1);
//		todoItems.add(item2);
//		todoItems.add(item3);
//
//		TodoData.getInstance().setTodoItems(todoItems);



		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
			@Override
			public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem oldVal, TodoItem newVal) {
				if (newVal != null) {
					TodoItem item=todoListView.getSelectionModel().getSelectedItem();
					itemDetailsTextArea.setText(item.getDetail());
					DateTimeFormatter dtf=DateTimeFormatter.ofPattern("MMMM d, yyyy");
					deadlineLabel.setText(dtf.format(item.getDeadline()));
				}
			}
		});

		todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();
	}

	@FXML
	public void showNewItemDialog(){
		Dialog<ButtonType> dialog=new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Add new Todo Iem");
		dialog.setHeaderText("Use this dialog to create new item");
		FXMLLoader fxmlLoader=new FXMLLoader();//get a instanace to access fxml controller
		fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
		try {
//			Parent root=FXMLLoader.load(getClass().getResource("todoitemDialog.fxml"));   //this is a static method no control
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			System.out.println("Couldnt load the dialog");
			e.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		Optional<ButtonType> result=dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			DialogController controller=fxmlLoader.getController();
			TodoItem newItem= controller.processResults();
			todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
			todoListView.getSelectionModel().select(newItem);//select the new value
			System.out.println("OK Pressed");
		}else {
			System.out.println("Cancel Pressed");
		}

	}
	@FXML
	public void handleClickListView() {
		TodoItem item=  todoListView.getSelectionModel().getSelectedItem();
		itemDetailsTextArea.setText(item.getDetail());
		deadlineLabel.setText(item.getDeadline().toString());

//		System.out.println("The selected Item is "+item);
//		StringBuilder sb =new StringBuilder(item.getDetail());
//		sb.append("\n\n\n\n");
//		sb.append("Due :");
//		sb.append(item.getDeadline().toString());
//		itemDetailsTextArea.setText(sb.toString());
	}



}