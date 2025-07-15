package com.SamWij.todolist;

import com.SamWij.todolist.datamodel.TodoData;
import com.SamWij.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
	@FXML
	private ContextMenu listContextMenu;
	@FXML
	private ToggleButton filterToggleButton;
	private Predicate<TodoItem> wantAllItems;
	private Predicate<TodoItem> wantTodayItems;

	private FilteredList<TodoItem> filteredList;

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

		listContextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				TodoItem item = todoListView.getSelectionModel().getSelectedItem();
				deleteItem(item);
			}
		});
		listContextMenu.getItems().addAll(deleteMenuItem);
		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
			@Override
			public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem oldVal, TodoItem newVal) {
				if (newVal != null) {
					TodoItem item = todoListView.getSelectionModel().getSelectedItem();
					itemDetailsTextArea.setText(item.getDetail());
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
					deadlineLabel.setText(dtf.format(item.getDeadline()));
				}
			}
		});
/*
		instead of using observable arraylist to populate the list view
		wrap it in s sortedList instance
		then use the sortedlist to pupulate list view
*/
		wantAllItems=new Predicate<TodoItem>() {
			@Override
			public boolean test(TodoItem item) {
				return true;
			}
		};

		wantTodayItems=item -> item.getDeadline().equals(LocalDate.now());

		filteredList=new FilteredList<>(TodoData.getInstance().getTodoItems(),wantAllItems);
		SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList,
				new Comparator<TodoItem>() {
					@Override
					public int compare(TodoItem o1, TodoItem o2) {
						return o1.getDeadline().compareTo(o2.getDeadline());
					}
				});
//		todoListView.setItems(TodoData.getInstance().getTodoItems());
		todoListView.setItems(sortedList);
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();

		todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
			@Override
			public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
				ListCell<TodoItem> cell = new ListCell<TodoItem>() {  //anonymous class implements callback interface
					@Override
					protected void updateItem(TodoItem todoItem, boolean empty) {
						super.updateItem(todoItem, empty);
						if (empty) {
							setText(null);
						} else {
							setText(todoItem.getShortDescription());
							if (todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
								setTextFill(Color.RED);
							} else if (todoItem.getDeadline().equals(LocalDate.now().plusDays(1))) {
								setTextFill(Color.BROWN);

							}
						}
					}
				};
				cell.emptyProperty().addListener(
						(obs, wasEmpty, isNowEmpty) -> {
							if (isNowEmpty) {
								cell.setContextMenu(null);
							} else {
								cell.setContextMenu(listContextMenu);
							}
						});

				return cell;
			}
		});
	}

	@FXML
	public void showNewItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Add new Todo Item");
		dialog.setHeaderText("Use this dialog to create new Item");
		FXMLLoader fxmlLoader = new FXMLLoader();//get a instanace to access fxml controller
		fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
		try {
//			Parent root=FXMLLoader.load(getClass().getResource("todoitemDialog.fxml"));   //this is a static method no control
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			DialogController controller = fxmlLoader.getController();
			TodoItem newItem = controller.processResults();
//			todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
			todoListView.getSelectionModel().select(newItem);//select the new value
//			System.out.println("OK Pressed");
		}

	}

	@FXML
	public void handleClickListView() {
		TodoItem item = todoListView.getSelectionModel().getSelectedItem();
		itemDetailsTextArea.setText(item.getDetail());
		deadlineLabel.setText(item.getDeadline().toString());

//		System.out.println("The selected Item is "+item);
//		StringBuilder sb =new StringBuilder(item.getDetail());
//		sb.append("\n\n\n\n");
//		sb.append("Due :");
//		sb.append(item.getDeadline().toString());
//		itemDetailsTextArea.setText(sb.toString());
	}

	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (keyEvent.getCode().equals(KeyCode.DELETE))
				deleteItem(selectedItem);

		}
	}

	@FXML
	public void deleteButton() {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		deleteItem(selectedItem);
	}

	public void deleteItem(TodoItem item) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete todo item");
		alert.setHeaderText("Delete Item: " + item.getShortDescription());
		alert.setContentText("Are you sure ?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get().equals(ButtonType.OK)) {
			TodoData.getInstance().deleteTodoItem(item);
		}
	}

	@FXML
	public void handleFilterButton() {
		TodoItem selectedItem=todoListView.getSelectionModel().getSelectedItem();
		if (filterToggleButton.isSelected()) {
			filteredList.setPredicate(wantTodayItems);
			if (filteredList.isEmpty()) {
				itemDetailsTextArea.clear();
				deadlineLabel.setText("");
			} else if (filteredList.contains(selectedItem)) {
				todoListView.getSelectionModel().select(selectedItem);
			} else {
				todoListView.getSelectionModel().selectFirst();
			}
		} else {
			filteredList.setPredicate(wantAllItems);
			todoListView.getSelectionModel().select(selectedItem);
		}

	}

	public void handleExit() {
		Platform.exit();
	}
}