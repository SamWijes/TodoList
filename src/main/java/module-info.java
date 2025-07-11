module org.example.todolist {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.SamWij.todolist to javafx.fxml;
	exports com.SamWij.todolist;

}