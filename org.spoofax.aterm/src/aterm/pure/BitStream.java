/**
 * 
 */
package aterm.pure;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads bits from any InputStream. 
 * 
 * @author Karl Trygve Kalleberg &lt; karltk near strategoxt dot org &gt;
 *
 */
public class BitStream {

    InputStream stream;
    private int bitsInBuffer;
    private int bitBuffer;

    public BitStream(InputStream inputStream) {
        stream = inputStream;
        bitsInBuffer = 0;
        bitBuffer = 0;
    }

    public int readInt() throws IOException {
        int[] buf = new int[5];

        buf[0] = readByte();
        
        // Check if 1st character is enough
        if((buf[0] & 0x80) == 0)
            return buf[0];

        buf[1]  = readByte();
        
        // Check if 2nd character is enough
        if((buf[0] & 0x40) == 0)
            return buf[1] + ((buf[0] & ~0xc0) << 8);

        buf[2] = readByte();

        // Check if 3rd character is enough
        if((buf[0] & 0x20) == 0 )
            return buf[2] + (buf[1] << 8) + ((buf[0] & ~0xe0) << 16);

        buf[3] = readByte();
        
        // Check if 4th character is enough
        if((buf[0] & 0x10) == 0 )
            return buf[3] + (buf[2] << 8) + (buf[1] << 16) +
              ((buf[0] & ~0xf0) << 24);
        
        buf[4] = readByte();

        return buf[4] + (buf[3] << 8) + (buf[2] << 16) + (buf[1] << 24);
    }

    protected int readByte() throws IOException {
        int c = stream.read();
        if(c == -1)
            throw new EOFException();
        return c;
    }

    public String readString() throws IOException {
        int l = readInt();
        byte[] b = new byte[l];
        int v = 0;
        while(v < b.length) {
            v += stream.read(b, v, b.length - v);
        }
        return new String(b);
    }

    public int readBits(int nrBits) throws IOException {
        int mask = 1;
        int val = 0;
        
        for (int i=0; i<nrBits; i++) {
          if (bitsInBuffer == 0) {
            int v = readByte();
            if (v == -1)
              return -1;
            bitBuffer = v;
            bitsInBuffer = 8;
          }
          val |= (((bitBuffer & 0x80) != 0) ? mask : 0);
          mask <<= 1;
          bitBuffer <<= 1;
          bitsInBuffer--;
        }
        
        return val;
    }

    public void flushBitsFromReader() {
        bitsInBuffer = 0;
    }
}