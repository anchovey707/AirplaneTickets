package project.object;

public class User {
	
	//this class should be done
	//two instanciation methods one for creation, the other for using the id to find booked flights with the user
	String id, firstName, lastName, email, password;
	
	
	public User(String firstName, String lastName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}
	
	public User(String id, String firstName, String lastName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.id = id;
	}
	public String id() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	
}