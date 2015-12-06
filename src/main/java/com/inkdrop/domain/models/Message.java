package com.inkdrop.domain.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inkdrop.domain.deserializers.MessageDeserializer;
import com.inkdrop.helpers.UUIDHelper;

@Entity
@JsonDeserialize(using = MessageDeserializer.class)
@Table(name = "messages")
public class Message extends BasePersistable {
	private static final long serialVersionUID = -5293724621181603251L;

	public Message() {}

	public Message(String content) {
		this.content = content;
	}

	@ManyToOne
	private Room room;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@ManyToOne
	@JsonIgnore
	private User sender;

	@Column(nullable = false, unique = true, length = 15)
	private String uniqueId;

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	@PrePersist
	public void prePersist() {
		super.prePersist();
		if (uniqueId == null)
			uniqueId = UUIDHelper.generateHash();
	}

	@Override
	public String toString() {
		return "Message [room=" + room + ", content=" + content + ", sentAt=" + getCreatedAt() + "]";
	}

}
