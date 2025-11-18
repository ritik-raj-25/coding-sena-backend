package com.codingsena.codingsena_backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.codingsena.codingsena_backend.utils.StatusType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode
public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(name = "profile_pic_name")
	private String profilePicName;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	private String location;
	
	private String college;
	
	@Column(nullable = false)
	private LocalDate dob;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, name = "nick_name")
	private String nickName;
	
	@Column(nullable = false, name = "is_deleted")
	private Boolean isDeleted;
	
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	@Column(name = "last_updated_at")
	private LocalDateTime lastUpdatedAt;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING) // saves enum as string in db
	private StatusType status;
	
	@Column(nullable = false, name = "is_verified")
	private Boolean isVerified;
	
	@Builder.Default
	private Long tokenVersion = 0L; // Access Token (JWT)
	
	@EqualsAndHashCode.Exclude
	@Builder.Default
	@ManyToMany
	@JoinTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
			)
	private Set<Role> roles = new HashSet<>();
	
	@OneToMany(mappedBy = "user")
	@Builder.Default
	private Set<Enrollment> enrollments = new HashSet<>();
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private VerificationToken verificationToken;
	
	@EqualsAndHashCode.Exclude
	@ManyToMany
	@Builder.Default
	@JoinTable(
			name = "user_skills",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "skill_id")
			)
	private Set<Skill> skills = new HashSet<>(); 
	
	@OneToMany(mappedBy = "user")
	private Set<TestAttempt> testAttempts;
	
	@Column(name = "last_test_attempt_id")
	private Long lastTestAttemptId; // To prevent simultaneous attempts
	
	public void addTestAttempt(TestAttempt testAttempt) {
		this.testAttempts.add(testAttempt);
		testAttempt.setUser(this);
	}
	
	public void removeTestAttempt(TestAttempt testAttempt) {
		this.testAttempts.remove(testAttempt);
		testAttempt.setUser(null);
	}
	
	public void addRole(Role role) {
		this.roles.add(role); // add role to user
		role.getUsers().add(this); // add user to role
	}
	
	public void removeRole(Role role) {
		this.roles.remove(role); // remove role from user
		role.getUsers().remove(this); // remove user from role
	}
	
	public void addSkill(Skill skill) {
		this.skills.add(skill); // add skill to user
		skill.getUsers().add(this); // add user to skill
		
	}
	
	public void removeSkill(Skill skill) {
		this.skills.remove(skill); // remove skill from user
		skill.getUsers().remove(this); // remove user from skill
	}
	
	public void addVerificationToken(VerificationToken verificationToken) {
		this.verificationToken = verificationToken;
		verificationToken.setUser(this);
	}
	
	public void removeVerificationToken() {
		if (this.verificationToken != null) {
	        this.verificationToken.setUser(null);
	        this.verificationToken = null;
	    }
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
        .collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return this.email;
	}
	
	@Override
	public boolean isEnabled() { // isVerified()
		return this.isVerified;
	}
	
	@Override
	public boolean isAccountNonLocked() { // isAccountNonBlocked()
		return status == StatusType.ACTIVE;
	}
	
	@Override
	public boolean isAccountNonExpired() { // isDeleted()
		return !this.isDeleted;
	}
}
