package org.ngbed.heif.boxes;


import org.ngbed.heif.io.RandomAccessReader;

import java.io.IOException;

/**
 * ISO/IEC 14496-12:2015 pg.7
 */
public class FullBox extends Box
{
	byte[] flags;
	int version;

	public FullBox(RandomAccessReader reader, Box box) throws IOException
	{
		super(box);

		version = reader.getUInt8();
		flags = reader.getBytes(3);
		
		countBytesRead = reader.getPosition() - offset;
	}

	public FullBox(FullBox box)
	{
		super(box);

		this.version = box.version;
		this.flags = box.flags;
	}
}
