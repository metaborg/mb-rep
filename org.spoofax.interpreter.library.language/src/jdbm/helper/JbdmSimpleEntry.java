package jdbm.helper;

import java.util.Map;

public abstract class JbdmSimpleEntry<K, V> implements Map.Entry<K, V>
{
	K key;
    V value;
   
    public JbdmSimpleEntry(K paramK, V paramV)
    {
      this.key = paramK;
      this.value = paramV;
    }
    public JbdmSimpleEntry(Map.Entry<K, V> paramEntry)
    {
      this.key = paramEntry.getKey();
      this.value = paramEntry.getValue();
    }

    
	public K getKey() {
		return this.key;
	}

	public V getValue() {
		 return this.value;
	}

	public V setValue(V paramV) {
		 V localObject = this.value;
	     this.value = paramV;
	     return localObject;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (!(obj instanceof Map.Entry))
			return false;
		
		if (!(obj instanceof JbdmSimpleEntry))
			return false;
		
		JbdmSimpleEntry other = (JbdmSimpleEntry) obj;
	
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		
		return true;
	}
}