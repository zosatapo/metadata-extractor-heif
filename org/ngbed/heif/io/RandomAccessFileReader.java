package org.ngbed.heif.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.drew.lang.BufferBoundsException;

public class RandomAccessFileReader extends RandomAccessReader
{
	
	private final RandomAccessFile _file;
	private final long _length;
	private long _currentIndex;

	public RandomAccessFileReader( RandomAccessFile file) throws IOException
	{
		if (file == null) throw new NullPointerException();

		_file = file;
		_length = _file.length();
	}

	@Override
	public long getLength()
	{
		return _length;
	}

	@Override
	public byte getByte(long index) throws IOException
	{
		if (index != _currentIndex) seek(index);

		final int b = _file.read();
		if (b < 0) throw new BufferBoundsException("Unexpected end of file encountered.");
		assert (b <= 0xff);
		_currentIndex++;
		return (byte) b;
	}

	@Override
	
	public byte[] getBytes(long index, long count) throws IOException
	{
		validateIndex(index, count);

		if (index != _currentIndex) seek(index);

		byte[] bytes = new byte[(int) count];
		final int bytesRead = _file.read(bytes);
		_currentIndex += bytesRead;
		if (bytesRead != count) throw new BufferBoundsException("Unexpected end of file encountered.");
		return bytes;
	}

	public void seek(long index) throws IOException
	{
		if (index == _currentIndex) return;

		_file.seek(index);
		_currentIndex = index;
	}

	public long getPosition() throws IOException
	{
		return _file.getFilePointer();
	}

	@Override
	protected boolean isValidIndex(long index, long bytesRequested) throws IOException
	{
		return bytesRequested >= 0 && index >= 0 && (long) index + (long) bytesRequested - 1L < _length;
	}

	@Override
	protected void validateIndex(final long index, final long bytesRequested) throws IOException
	{
		if (!isValidIndex(index, bytesRequested)) throw new BufferBoundsException((int)index, (int)bytesRequested, _length);
	}

	@Override
	public void skip(long n) throws IOException
	{
		if (n < 0) throw new IllegalArgumentException("n must be zero or greater.");

		long skippedCount = skipInternal(n);

		if (skippedCount != n) throw new EOFException(
				String.format("Unable to skip. Requested %d bytes but skipped %d.", n, skippedCount));
	}

	@Override
	public boolean trySkip(long n) throws IOException
	{
		if (n < 0) throw new IllegalArgumentException("n must be zero or greater.");

		return skipInternal(n) == n;
	}

	private long skipInternal(long n) throws IOException
	{
		long skippedTotal = 0;
		while (skippedTotal != n)
		{
			long skipped = _file.skipBytes((int) (n - skippedTotal));
			assert (skipped >= 0);
			skippedTotal += skipped;
			if (skipped == 0) break;
		}
		_currentIndex += skippedTotal;
		return skippedTotal;
	}
}
