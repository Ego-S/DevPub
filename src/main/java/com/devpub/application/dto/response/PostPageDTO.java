package com.devpub.application.dto.response;

import com.devpub.application.dto.response.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPageDTO {
	private int count;
	private List<PostDTO> posts;
}
