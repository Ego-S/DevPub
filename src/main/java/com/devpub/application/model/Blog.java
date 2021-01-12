package com.devpub.application.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@ToString
public class Blog {
	private static Blog instance;

	private String title;
	private String subtitle;
	private String phone;
	private String email;
	private String copyright;
	private String copyrightFrom;

	private Blog(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom){
		this.title = title;
		this.subtitle = subtitle;
		this.phone = phone;
		this.email = email;
		this.copyright = copyright;
		this.copyrightFrom = copyrightFrom;
	}

	public static Blog getInstance(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom) {
		if (instance == null) {
			instance = new Blog(title, subtitle, phone, email, copyright, copyrightFrom);
		}
		return instance;
	}
}
