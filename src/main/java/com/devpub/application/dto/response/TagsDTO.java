package com.devpub.application.dto.response;

import com.devpub.application.dto.response.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagsDTO {
	private List<TagDTO> tags;
}
