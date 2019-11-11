package org.ngbed.heif.boxes;

import org.ngbed.heif.io.RandomAccessReader;

import java.io.IOException;

/**
 * ISO/IEC 14496-12:2015 pg.6
 */
public class Box
{
	private long firstsize;
	private long largesize;
	public String type;
	public String usertype;

	public long size;
	public long offset;
	public long countBytesRead;

	public Box(RandomAccessReader reader) throws IOException
	{
		this.offset = reader.getPosition();
		
		this.firstsize = reader.getUInt32();
		this.type = reader.getString(4);
		if (firstsize == 1)
		{
			largesize = reader.getInt64();
		}

		if (type.equals("uuid"))
		{
			usertype = reader.getString(16);
		}

		countBytesRead = reader.getPosition() - offset;
		size = (firstsize == 1) ? largesize : firstsize;
	}

	public Box(Box box)
	{
		this.offset = box.offset;
		this.countBytesRead = box.countBytesRead;
		this.size = box.size;

		this.firstsize = box.firstsize;
		this.largesize = box.largesize;
		this.type = box.type;
		this.usertype = box.usertype;
	}

	public boolean isLastBox()
	{
		return firstsize == 0;
	}

	public long countBytesUnread()
	{
		return this.size - this.countBytesRead;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[" + this.getClass().getName() + "]-[" + this.type + "]");
		sb.append(" offset=" + this.offset);
		sb.append(", size=" + this.size);
		sb.append(", bytesRead=" + this.countBytesRead);
		return sb.toString();
	}
}
