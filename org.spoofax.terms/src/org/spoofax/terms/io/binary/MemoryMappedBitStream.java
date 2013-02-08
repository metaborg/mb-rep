/*
 * Licensed under the GNU Lesser General Public License, v2.1 
 */
package org.spoofax.terms.io.binary;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Reads bits from a FileInputStream using memory mapping. At least 2x 
 * faster than BitStream.
 * 
 * Unsafe:
 * 
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4715154 
 * 
 * @author Karl Trygve Kalleberg <karltk near strategoxt dot org>
 */
@Deprecated
public class MemoryMappedBitStream extends BitStream {

	private FileChannel channel; 
	private MappedByteBuffer byteBuffer;
	
	public MemoryMappedBitStream(FileInputStream inputStream) throws IOException {
		super(inputStream);
		this.channel = inputStream.getChannel();
		byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	}
	
    @Override
	protected int readByte() throws IOException {
    	return byteBuffer.get() & 0xFF; 
    }
    
    @Override
	public String readString() throws IOException {
        final int l = readInt();
        byte[] b = new byte[l];
        //byteBuffer.position(offset);
        byteBuffer.get(b);
        return new String(b);
    }
}
