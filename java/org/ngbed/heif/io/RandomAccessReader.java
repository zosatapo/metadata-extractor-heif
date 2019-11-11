package org.ngbed.heif.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.StringValue;

public abstract class RandomAccessReader
{
	private boolean _isMotorolaByteOrder = true;

	/**
	 * Gets the byte value at the specified byte <code>index</code>.
	 * <p>
	 * Implementations should not perform any bounds checking in this method. That
	 * should be performed in <code>validateIndex</code> and
	 * <code>isValidIndex</code>.
	 * 
	 * @param index The index from which to read the byte
	 * @return The read byte value
	 * @throws IllegalArgumentException <code>index</code> is negative
	 * @throws BufferBoundsException    if the requested byte is beyond the end of
	 *                                  the underlying data source
	 * @throws IOException              if the byte is unable to be read
	 */
	public abstract byte getByte(long index) throws IOException;

	public byte getByte() throws IOException
	{
		return getByte(this.getPosition());
	}

	/**
	 * Returns the required number of bytes from the specified index from the
	 * underlying source.
	 * 
	 * @param index The index from which the bytes begins in the underlying source
	 * @param count The number of bytes to be returned
	 * @return The requested bytes
	 * @throws IllegalArgumentException <code>index</code> or <code>count</code> are
	 *                                  negative
	 * @throws BufferBoundsException    if the requested bytes extend beyond the end
	 *                                  of the underlying data source
	 * @throws IOException              if the byte is unable to be read
	 */
	
	public abstract byte[] getBytes(long index, long count) throws IOException;

	public byte[] getBytes(long count) throws IOException
	{
		return getBytes(this.getPosition(), count);
	}

	/**
	 * Skips forward in the sequence. If the sequence ends, an
	 * {@link EOFException} is thrown.
	 * 
	 * @param n
	 *            the number of byte to skip. Must be zero or greater.
	 * @throws EOFException
	 *             the end of the sequence is reached.
	 * @throws IOException
	 *             an error occurred reading from the underlying source.
	 */
	public abstract void skip(long n) throws IOException;

	/**
	 * Skips forward in the sequence, returning a boolean indicating whether the
	 * skip succeeded, or whether the sequence ended.
	 * 
	 * @param n
	 *            the number of byte to skip. Must be zero or greater.
	 * @return a boolean indicating whether the skip succeeded, or whether the
	 *         sequence ended.
	 * @throws IOException
	 *             an error occurred reading from the underlying source.
	 */
	public abstract boolean trySkip(long n) throws IOException;
	
	/**
	 * Ensures that the buffered bytes extend to cover the specified index. If not,
	 * an attempt is made to read to that point.
	 * <p>
	 * If the stream ends before the polong is reached, a
	 * {@link BufferBoundsException} is raised.
	 * 
	 * @param index          the index from which the required bytes start
	 * @param bytesRequested the number of bytes which are required
	 * @throws IOException if the stream ends before the required number of bytes
	 *                     are acquired
	 */
	protected abstract void validateIndex(long index, long bytesRequested) throws IOException;

	protected abstract boolean isValidIndex(long index, long bytesRequested) throws IOException;

	/**
	 * Returns the length of the data source in bytes.
	 * <p>
	 * This is a simple operation for implementations (such as
	 * {@link RandomAccessFileReader} and {@link ByteArrayReader}) that have the
	 * entire data source available.
	 * <p>
	 * Users of this method must be aware that sequentially accessed implementations
	 * such as {@link RandomAccessStreamReader} will have to read and buffer the
	 * entire data source in order to determine the length.
	 * 
	 * @return the length of the data source, in bytes.
	 */
	public abstract long getLength() throws IOException;

	public abstract void seek(long index) throws IOException;

	public abstract long getPosition() throws IOException;

	/**
	 * Sets the endianness of this reader.
	 * <ul>
	 * <li><code>true</code> for Motorola (or big) endianness (also known as network
	 * byte order), with MSB before LSB.</li>
	 * <li><code>false</code> for Intel (or little) endianness, with LSB before
	 * MSB.</li>
	 * </ul>
	 * 
	 * @param motorolaByteOrder <code>true</code> for Motorola/big endian,
	 *                          <code>false</code> for Intel/little endian
	 */
	public void setMotorolaByteOrder(boolean motorolaByteOrder)
	{
		_isMotorolaByteOrder = motorolaByteOrder;
	}

	/**
	 * Gets the endianness of this reader.
	 * <ul>
	 * <li><code>true</code> for Motorola (or big) endianness (also known as network
	 * byte order), with MSB before LSB.</li>
	 * <li><code>false</code> for Intel (or little) endianness, with LSB before
	 * MSB.</li>
	 * </ul>
	 */
	public boolean isMotorolaByteOrder()
	{
		return _isMotorolaByteOrder;
	}

	/**
	 * Gets whether a bit at a specific index is set or not.
	 * 
	 * @param index the number of bits at which to test
	 * @return true if the bit is set, otherwise false
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public boolean getBit(long index) throws IOException
	{
		long byteIndex = index / 8;
		long bitIndex = index % 8;

		validateIndex(byteIndex, 1);

		byte b = getByte(byteIndex);
		return ((b >> bitIndex) & 1) == 1;
	}

	public boolean getBit() throws IOException
	{
		return getBit(this.getPosition());
	}

	/**
	 * Returns an unsigned 8-bit long calculated from one byte of data at the
	 * specified index.
	 * 
	 * @param index position within the data buffer to read byte
	 * @return the 8 bit long value, between 0 and 255
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public short getUInt8(long index) throws IOException
	{
		validateIndex(index, 1);

		return (short) (getByte(index) & 0xFF);
	}

	public short getUInt8() throws IOException
	{
		return getUInt8(this.getPosition());
	}

	/**
	 * Returns a signed 8-bit long calculated from one byte of data at the specified
	 * index.
	 * 
	 * @param index position within the data buffer to read byte
	 * @return the 8 bit long value, between 0x00 and 0xFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public byte getInt8(long index) throws IOException
	{
		validateIndex(index, 1);

		return getByte(index);
	}

	public byte getInt8() throws IOException
	{
		return getInt8(this.getPosition());
	}

	/**
	 * Returns an unsigned 16-bit long calculated from two bytes of data at the
	 * specified index.
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the 16 bit long value, between 0x0000 and 0xFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public int getUInt16(long index) throws IOException
	{
		validateIndex(index, 2);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first
			return (getByte(index) << 8 & 0xFF00) | (getByte(index + 1) & 0xFF);
		}
		else
		{
			// Intel ordering - LSB first
			return (getByte(index + 1) << 8 & 0xFF00) | (getByte(index) & 0xFF);
		}
	}

	public int getUInt16() throws IOException
	{
		return getUInt16(this.getPosition());
	}

	/**
	 * Returns a signed 16-bit long calculated from two bytes of data at the
	 * specified index (MSB, LSB).
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the 16 bit long value, between 0x0000 and 0xFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public short getInt16(long index) throws IOException
	{
		validateIndex(index, 2);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first
			return (short) (((short) getByte(index) << 8 & (short) 0xFF00)
					| ((short) getByte(index + 1) & (short) 0xFF));
		}
		else
		{
			// Intel ordering - LSB first
			return (short) (((short) getByte(index + 1) << 8 & (short) 0xFF00)
					| ((short) getByte(index) & (short) 0xFF));
		}
	}

	public short getInt16() throws IOException
	{
		return getInt16(this.getPosition());
	}

	/**
	 * Get a 24-bit unsigned integer from the buffer, returning it as an int.
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the unsigned 24-bit long value as a long, between 0x00000000 and
	 *         0x00FFFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public int getInt24(long index) throws IOException
	{
		validateIndex(index, 3);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first (big endian)
			return (((int) getByte(index)) << 16 & 0xFF0000) | (((int) getByte(index + 1)) << 8 & 0xFF00)
					| (((int) getByte(index + 2)) & 0xFF);
		}
		else
		{
			// Intel ordering - LSB first (little endian)
			return (((int) getByte(index + 2)) << 16 & 0xFF0000) | (((int) getByte(index + 1)) << 8 & 0xFF00)
					| (((int) getByte(index)) & 0xFF);
		}
	}

	public int getInt24() throws IOException
	{
		return getInt24(this.getPosition());
	}

	/**
	 * Get a 32-bit unsigned integer from the buffer, returning it as a long.
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the unsigned 32-bit long value as a long, between 0x00000000 and
	 *         0xFFFFFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public long getUInt32(long index) throws IOException
	{
		validateIndex(index, 4);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first (big endian)
			return (((long) getByte(index)) << 24 & 0xFF000000L) | (((long) getByte(index + 1)) << 16 & 0xFF0000L)
					| (((long) getByte(index + 2)) << 8 & 0xFF00L) | (((long) getByte(index + 3)) & 0xFFL);
		}
		else
		{
			// Intel ordering - LSB first (little endian)
			return (((long) getByte(index + 3)) << 24 & 0xFF000000L) | (((long) getByte(index + 2)) << 16 & 0xFF0000L)
					| (((long) getByte(index + 1)) << 8 & 0xFF00L) | (((long) getByte(index)) & 0xFFL);
		}
	}

	public long getUInt32() throws IOException
	{
		return getUInt32(this.getPosition());
	}

	/**
	 * Returns a signed 32-bit integer from four bytes of data at the specified
	 * index the buffer.
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the signed 32 bit long value, between 0x00000000 and 0xFFFFFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public int getInt32(long index) throws IOException
	{
		validateIndex(index, 4);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first (big endian)
			return (getByte(index) << 24 & 0xFF000000) | (getByte(index + 1) << 16 & 0xFF0000)
					| (getByte(index + 2) << 8 & 0xFF00) | (getByte(index + 3) & 0xFF);
		}
		else
		{
			// Intel ordering - LSB first (little endian)
			return (getByte(index + 3) << 24 & 0xFF000000) | (getByte(index + 2) << 16 & 0xFF0000)
					| (getByte(index + 1) << 8 & 0xFF00) | (getByte(index) & 0xFF);
		}
	}

	public int getInt32() throws IOException
	{
		return getInt32(this.getPosition());
	}

	/**
	 * Get a signed 64-bit integer from the buffer.
	 * 
	 * @param index position within the data buffer to read first byte
	 * @return the 64 bit long value, between 0x0000000000000000 and
	 *         0xFFFFFFFFFFFFFFFF
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public long getInt64(long index) throws IOException
	{
		validateIndex(index, 8);

		if (_isMotorolaByteOrder)
		{
			// Motorola - MSB first
			return ((long) getByte(index) << 56 & 0xFF00000000000000L)
					| ((long) getByte(index + 1) << 48 & 0xFF000000000000L)
					| ((long) getByte(index + 2) << 40 & 0xFF0000000000L)
					| ((long) getByte(index + 3) << 32 & 0xFF00000000L)
					| ((long) getByte(index + 4) << 24 & 0xFF000000L) | ((long) getByte(index + 5) << 16 & 0xFF0000L)
					| ((long) getByte(index + 6) << 8 & 0xFF00L) | ((long) getByte(index + 7) & 0xFFL);
		}
		else
		{
			// Intel ordering - LSB first
			return ((long) getByte(index + 7) << 56 & 0xFF00000000000000L)
					| ((long) getByte(index + 6) << 48 & 0xFF000000000000L)
					| ((long) getByte(index + 5) << 40 & 0xFF0000000000L)
					| ((long) getByte(index + 4) << 32 & 0xFF00000000L)
					| ((long) getByte(index + 3) << 24 & 0xFF000000L) | ((long) getByte(index + 2) << 16 & 0xFF0000L)
					| ((long) getByte(index + 1) << 8 & 0xFF00L) | ((long) getByte(index) & 0xFFL);
		}
	}

	public long getInt64() throws IOException
	{
		return getInt64(this.getPosition());
	}

	/**
	 * Gets a s15.16 fixed polong float from the buffer.
	 * <p>
	 * This particular fixed polong encoding has one sign bit, 15 numerator bits and
	 * 16 denominator bits.
	 * 
	 * @return the floating polong value
	 * @throws IOException the buffer does not contain enough bytes to service the
	 *                     request, or index is negative
	 */
	public float getS15Fixed16(long index) throws IOException
	{
		validateIndex(index, 4);

		if (_isMotorolaByteOrder)
		{
			float res = (getByte(index) & 0xFF) << 8 | (getByte(index + 1) & 0xFF);
			long d = (getByte(index + 2) & 0xFF) << 8 | (getByte(index + 3) & 0xFF);
			return (float) (res + d / 65536.0);
		}
		else
		{
			// this particular branch is untested
			float res = (getByte(index + 3) & 0xFF) << 8 | (getByte(index + 2) & 0xFF);
			long d = (getByte(index + 1) & 0xFF) << 8 | (getByte(index) & 0xFF);
			return (float) (res + d / 65536.0);
		}
	}

	public float getS15Fixed16() throws IOException
	{
		return getS15Fixed16(this.getPosition());
	}

	public float getFloat32(long index) throws IOException
	{
		return Float.intBitsToFloat(getInt32(index));
	}

	public float getFloat32() throws IOException
	{
		return getFloat32(this.getPosition());
	}

	public double getDouble64(long index) throws IOException
	{
		return Double.longBitsToDouble(getInt64(index));
	}

	public double getDouble64() throws IOException
	{
		return getDouble64(this.getPosition());
	}

	
	public StringValue getStringValue(long index, long bytesRequested, @Nullable Charset charset) throws IOException
	{
		return new StringValue(getBytes(index, bytesRequested), charset);
	}

	public StringValue getStringValue(long bytesRequested, @Nullable Charset charset) throws IOException
	{
		return getStringValue(this.getPosition(), bytesRequested, charset);
	}

	
	public String getString(long index, long bytesRequested,  Charset charset) throws IOException
	{
		return new String(getBytes(index, bytesRequested), charset.name());
	}

	public String getString(long bytesRequested, @Nullable Charset charset) throws IOException
	{
		return getString(this.getPosition(), bytesRequested, charset);
	}

	
	public String getString(long index, long bytesRequested,  String charset) throws IOException
	{
		byte[] bytes = getBytes(index, bytesRequested);
		try
		{
			return new String(bytes, charset);
		}
		catch (UnsupportedEncodingException e)
		{
			return new String(bytes);
		}
	}

	public String getString(long bytesRequested, @Nullable String charset) throws IOException
	{
		return getString(this.getPosition(), bytesRequested, charset);
	}

	public String getString(long bytesRequested) throws IOException
	{
		return getString(this.getPosition(), bytesRequested, Charset.defaultCharset());
	}
	
	/**
	 * Creates a String from the _data buffer starting at the specified index, and
	 * ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
	 * 
	 * @param index          The index within the buffer at which to start reading
	 *                       the string.
	 * @param maxLengthBytes The maximum number of bytes to read. If a zero-byte is
	 *                       not reached within this limit, reading will stop and
	 *                       the string will be truncated to this length.
	 * @return The read string.
	 * @throws IOException The buffer does not contain enough bytes to satisfy this
	 *                     request.
	 */
	
	public String getNullTerminatedString(long index, long maxLengthBytes,  Charset charset) throws IOException
	{
		return new String(getNullTerminatedBytes(index, maxLengthBytes), charset.name());
	}

	public String getNullTerminatedString(long bytesRequested, @Nullable Charset charset) throws IOException
	{
		return getNullTerminatedString(this.getPosition(), bytesRequested, charset);
	}

	
	public StringValue getNullTerminatedStringValue(long index, long maxLengthBytes, @Nullable Charset charset)
			throws IOException
	{
		byte[] bytes = getNullTerminatedBytes(index, maxLengthBytes);

		return new StringValue(bytes, charset);
	}

	public StringValue getNullTerminatedStringValue(long bytesRequested, @Nullable Charset charset) throws IOException
	{
		return getNullTerminatedStringValue(this.getPosition(), bytesRequested, charset);
	}

	/**
	 * Returns the sequence of bytes punctuated by a <code>\0</code> value.
	 * 
	 * @param index          The index within the buffer at which to start reading
	 *                       the string.
	 * @param maxLengthBytes The maximum number of bytes to read. If a
	 *                       <code>\0</code> byte is not reached within this limit,
	 *                       the returned array will be <code>maxLengthBytes</code>
	 *                       long.
	 * @return The read byte array, excluding the null terminator.
	 * @throws IOException The buffer does not contain enough bytes to satisfy this
	 *                     request.
	 */
	
	public byte[] getNullTerminatedBytes(long index, long maxLengthBytes) throws IOException
	{
		byte[] buffer = getBytes(index, maxLengthBytes);

		// Count the number of non-null bytes
		int length = 0;
		while (length < buffer.length && buffer[length] != 0)
			length++;

		if (length == maxLengthBytes) return buffer;

		byte[] bytes = new byte[length];
		if (length > 0) System.arraycopy(buffer, 0, bytes, 0, length);
		return bytes;
	}

	public byte[] getNullTerminatedBytes(long maxLengthBytes) throws IOException
	{
		return getNullTerminatedBytes(this.getPosition(), maxLengthBytes);
	}

}
