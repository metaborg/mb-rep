package org.spoofax.interpreter.library.language.spxlang;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/**
 * Represent information regarding Spoofaxlang CompilationUnit 
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 29, 2011
 */
class SpxComplicationUnitInfo implements Serializable
{
	private static final long serialVersionUID = 4874917828420267542L;

	private long _recId;
	
	private int _version;
	
	private URI _absPath;
	
	public SpxComplicationUnitInfo( long recourceId, int versionNo, URI absPath)
	{
		_recId = recourceId;
		_version = versionNo;
		_absPath = absPath;
	}

	long getRecId() {
		return _recId;
	}

	void setRecId(long recId) {
		this._recId = recId;
	}

	int getVersionNo() {
		return _version;
	}

	void setVersionNo(int version) {
		this._version = version;
	}

	URI getAbsPath() {
		return _absPath;
	}

	String getAbsPathString()
	{
		return new File(getAbsPath()).getAbsolutePath();
	}
	
	void setAbsPath(URI absPath) {
		this._absPath = absPath;
	}
	
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
		SpxComplicationUnitInfo other = (SpxComplicationUnitInfo) obj;
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
}