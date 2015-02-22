package com.hunnymustard.ssbl.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="events")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Event {

	private Integer _id;
	private String _title, _description;
	private Location _location;
	private Long _startTime, _endTime;
	private User _host;
	private Boolean _public;
	private List<Game> _games;
	private List<User> _users;
	
	public Event() {}
	
	public Event(Integer id, String title, String description, Location location, Long startTime,
			Long endTime, User host, Boolean publc, List<Game> games, List<User> users) {	
		
		_id = id;
		_title = title;
		_description = description;
		_location = location;
		_startTime = startTime;
		_endTime = endTime;
		_host = host;
		_public = publc;
		_games = games;
		_users = users;
	}
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="event_id", unique=true, nullable=false)
	public Integer getEventId() {
		return _id;
	}
	
	public void setEventId(Integer id) {
		_id = id;
	}
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name="host_id", nullable=false)
	public User getHost() {
		return _host;
	}
	
	public void setHost(User host) {
		_host = host;
	}
	
	@Column(name="title", nullable=false)
	public String getTitle() {
		return _title;
	}
	
	public void setTitle(String title) {
		_title = title;
	}
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name="location_id", nullable=false)
	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}
	
	@Column(name="start_time", nullable=false)
	public Long getStartTime() {
		return _startTime;
	}
	
	public void setStartTime(Long startTime) {
		_startTime = startTime;
	}
	
	@Column(name="end_time", nullable=false)
	public Long getEndTime() {
		return _endTime;
	}
	
	public void setEndTime(Long endTime) {
		_endTime = endTime;
	}
	
	@Column(name="description")
	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String description) {
		_description = description;
	}
	
	@Column(name="public", nullable=false)
	public Boolean isPublic() {
		return _public;
	}
	
	public void setPublic(Boolean publc) {
		_public = publc;
	}
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(name="event_users",
			joinColumns = { @JoinColumn(name="event_id") },
			inverseJoinColumns = { @JoinColumn(name="user_id") })
	public List<User> getUsers() {
		return _users;
	}
	
	public void setUsers(List<User> users) {
		_users = users;
	}
	
	public void addUser(User user) {
		if(_users == null) _users = new ArrayList<User>();
		_users.add(user);
	}
	
	@ElementCollection(targetClass=Game.class, fetch=FetchType.EAGER)
    @JoinTable(name="event_games",
            joinColumns = {@JoinColumn(name="event_id")})
    @Column(name="game_id")
	@Enumerated(EnumType.ORDINAL)
	public List<Game> getGames() {
		return _games;
	}
	
	public void setGames(List<Game> games) {
		_games = games;
	}
	
	public void addGame(Game game) {
		if(_games == null) _games = new ArrayList<Game>();
		_games.add(game);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Event)
			return new EqualsBuilder()
				.append(_id, ((Event) obj).getEventId())
				.build();
		return false;
	}
}
