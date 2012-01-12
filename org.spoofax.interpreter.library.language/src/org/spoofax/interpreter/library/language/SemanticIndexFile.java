package org.spoofax.interpreter.library.language;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A URI/time stamp container. Only one SemanticIndexFile may exist per URI.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexFile {
	
	private final Set<SemanticIndexEntry> entries = new HashSet<SemanticIndexEntry>();

	private final URI uri;
	
	private Date time;
	
	public URI getURI() {
		return uri;
	}
	
	public Date getTime() {
		return time;
	}
	
	public Set<SemanticIndexEntry> getEntries() {
		return entries;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public SemanticIndexFile(URI uri, Date time) {
		this.uri = uri;
		this.time = time;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SemanticIndexFile && ((SemanticIndexFile) obj).uri.equals(uri);
	}
}
