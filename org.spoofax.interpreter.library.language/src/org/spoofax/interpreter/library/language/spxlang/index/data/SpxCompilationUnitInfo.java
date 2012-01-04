package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.security.acl.LastOwnerException;

/**
 * Contains information regarding Spoofaxlang CompilationUnit. 
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 29, 2011
 */
public class SpxCompilationUnitInfo implements Serializable
{
	private static final long serialVersionUID = 4366484691839493105L;

	private final long _recId;
	private int _version;
	private final URI _absPath;
	private long _lastModifiedOn ;
	
	public SpxCompilationUnitInfo(URI absPath,long recourceId){
		this (0 , absPath , recourceId);
	}

	public SpxCompilationUnitInfo( int versionNo, URI absPath,long recourceId){
		_recId = recourceId;
		_version = versionNo;
		_absPath = absPath;
		_lastModifiedOn = System.currentTimeMillis();
	}

	public  long getRecId() {
		return _recId;
	}

	public int getVersionNo() {
		return _version;
	}
	
	public long getLastModifiedOn(){return _lastModifiedOn;}
	URI getAbsPath() {
		return _absPath;
	}

	public String getAbsPathString()
	{
		return new File(getAbsPath()).getAbsolutePath();
	}

	/**
	 * Increment version no
	 */
	public synchronized void incrVersion() {
		_lastModifiedOn = System.currentTimeMillis();
		_version = _version + 1;  
	}
	
	public String toString() {
		return "SpxComplicationUnitResourceInfo [ResourceId=" + _recId
				+ ", VersionNo=" + _version + ", AbsPath=" + _absPath + ", LastModifiedOn=" + this.getLastModifiedOn()+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_absPath == null) ? 0 : _absPath.hashCode());
		result = prime * result + (int) (_recId ^ (_recId >>> 32));
		result = prime * result + _version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpxCompilationUnitInfo other = (SpxCompilationUnitInfo) obj;
		if (_absPath == null) {
			if (other._absPath != null)
				return false;
		} else if (!_absPath.equals(other._absPath))
			return false;
		if (_recId != other._recId)
			return false;
		if (_version != other._version)
			return false;
		return true;
	}
	
	/**
	 * Creates a new instance of {@code inf}.
	 * 
	 * @param inf an instance of {@link SpxCompilationUnitInfo}
	 * @return a cloned version of {@code inf}
	 */
	public static SpxCompilationUnitInfo newInstance( SpxCompilationUnitInfo inf)
	{
		if( inf == null) 
			return null; 
		else	
			return new SpxCompilationUnitInfo(
					inf.getVersionNo(), 
					inf.getAbsPath() , 
					inf.getRecId());
	}
}