package com.example.taskmanager.task_manager.model;

import java.sql.Date; // java.sql.Date is deprecated.
						// It’s better to use LocalDate or LocalDateTime (from Java.time)
// Import annotations to work with JPA(Java Persistence API), like @Entity, @Id, ...
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity //Indicates that the class corresponds to a database table.
@Table(name = "users") //Table named users.
public class User {

    @Id //the id field is the primary key.
    //the id is generated automatically, 
    //with the IDENTITY strategy, typical for relational databases.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    public Date getConfirmationExpiryDate() {
		return confirmationExpiryDate;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public void setConfirmationExpiryDate(Date confirmationExpiryDate) {
		this.confirmationExpiryDate = confirmationExpiryDate;
	}

	private String role;
    
    @Column(nullable = true) //Specifies that the field can be null.
    private String confirmationToken;

    @Column(nullable = true)
    private Date confirmationExpiryDate;

    @Column(nullable = false)
    private boolean enabled = false; // The user is not active until confirmed.

    // Empty constructor required by JPA.
    public User() {} 

    public User(String username, String password, String role, String email) {
        this.username = username;
        this.setEmail(email);
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}

