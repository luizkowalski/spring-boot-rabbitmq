package com.inkdrop.domain.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inkdrop.domain.deserializers.PrivateMessageDeserializer;
import com.inkdrop.helpers.UUIDHelper;

@Entity
@Table(name="private_messages")
@JsonDeserialize(using = PrivateMessageDeserializer.class)
public class PrivateMessage extends BasePersistable {

	private static final long serialVersionUID = 2438631648733992355L;

	@ManyToOne
	@JoinColumn(name="from_id")
	private User from;

	@ManyToOne
	@JoinColumn(name="to_id")
	private User to;

	@Lob
	private String content;

	@Column(nullable=false, unique=true, length=15)
	private String uniqueId;

	public PrivateMessage(String content) {
		this.content = content;
	}

	public PrivateMessage() {}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	@PrePersist
	public void prePersist(){
		super.prePersist();
		if(uniqueId == null)
			uniqueId = UUIDHelper.generateHash();
	}

	@Override
	public String toString() {
		return "PrivateMessage [id=" + getId() + ", from=" + from + ", to=" + to + "]";
	}
}
