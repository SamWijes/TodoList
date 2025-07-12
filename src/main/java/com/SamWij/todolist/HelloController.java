package com.SamWij.todolist;

import com.SamWij.todolist.datamodel.TodoData;
import com.SamWij.todolist.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
	@FXML
	private List<TodoItem> todoItems;

	@FXML
	private ListView<TodoItem> todoListView;

	@FXML
	private TextArea itemDetailsTextArea;

	@FXML
	private Label deadlineLabel;

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