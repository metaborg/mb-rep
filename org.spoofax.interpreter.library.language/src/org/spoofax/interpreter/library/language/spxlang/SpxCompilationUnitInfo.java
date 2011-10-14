package org.spoofax.interpreter.library.language.spxlang;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/**
 * Contains information regarding Spoofaxlang CompilationUnit. 
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 29, 2011
 */
class SpxCompilationUnitInfo implements Serializable
{
	private static final long serialVersionUID = 4874917828420267542L;

	private final long _recId;
	private int _version;
	private final URI _absPath;
	
	public SpxCompilationUnitInfo(URI absPath,long recourceId)
	{
		this (0 , absPath , recourceId);
	}

	public SpxCompilationUnitInfo( int versionNo, URI absPath,long recourceId)
	{
		_recId = recourceId;
		_version = versionNo;
		_absPath = absPath;
	}

	long getRecId() {
		return _recId;
	}

	int getVersionNo() {
		return _version;
	}
	
	URI getAbsPath() {
		return _absPath;
	}

	String getAbsPathString()
	{
		return new File(getAbsPath()).getAbsolutePath();
	}

	/**
	 * Increment version no
	 */
	public void IncrementVersionNo() { _version = _version + 1; }

	
	public String toString() {
		return "SpxComplicationUnitResourceInfo [ResourceId=" + _recId
				+ ", VersionNo=" + _version + ", AbsPath=" + _absPath + "]";
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