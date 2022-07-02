package com.stl.bsky.actuator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id="release-notes")
public class CustomEndpoint {
	Map<String, List<String>> releaseNotes=new LinkedHashMap<>();
	
	@PostConstruct
	public void initNotes() {
		releaseNotes.put("version-1.0", Arrays.asList("Actuator" , " Spring Security"));
		releaseNotes.put("version-1.1", Arrays.asList("Custom Actuator End Point "," JWT Token"));
	}
	
	@ReadOperation
	public Map<String, List<String>> getReleaseNotes(){
		return releaseNotes;
	}
	
	@ReadOperation
	public List<String> getNotesByVersion(@Selector String version){
		return releaseNotes.get(version);
	}
	
	@WriteOperation
	public void addReleaseNotes(@Selector String version, String releaseNote) {
		releaseNotes.put(version, Arrays.stream(releaseNote.split(",")).collect(Collectors.toList()));
	}
}
