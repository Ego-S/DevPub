package com.devpub.application.model;

import com.devpub.application.enums.GlobalSettingValue;
import com.devpub.application.enums.GlobalSettingCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "global_settings")
public class GlobalSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GlobalSettingCode code;


	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GlobalSettingValue value;
}
