package com.codingsena.codingsena_backend.entities;

import java.util.HashSet;
import java.util.Set;

import com.codingsena.codingsena_backend.utils.RoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "roles")
@Entity
@EqualsAndHashCode(exclude = {"users"})
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING) // saves enum as string in db
	@Column(name = "role_name", nullable = false)
	private RoleType roleName;
	
	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();
}
