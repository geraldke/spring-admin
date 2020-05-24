package com.example.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier {

	private final UserRepository repository;

	/**
	 * The currently edited user
	 */
	private User user;

	/* Fields to edit properties in User entity */
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");

	/* Action buttons */
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<User> binder = new Binder<>(User.class);
	private ChangeHandler changeHandler;

	@Autowired
	public UserEditor(UserRepository repository) {
		this.repository = repository;

		add(firstName, lastName, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editUser(user));
		setVisible(false);
	}

	void delete() {
		repository.delete(user);
		changeHandler.onChange();
	}

	void save() {
		repository.save(user);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}

	public final void editUser(User u) {
		if (u == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = u.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			user = repository.findById(u.getId()).get();
		}
		else {
			user = u;
		}
		cancel.setVisible(persisted);

		// Bind user properties to similarly named fields
		binder.setBean(user);

		setVisible(true);

		// Focus first name initially
		firstName.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}

}
