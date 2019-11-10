package org.ngbed.heif.boxes;

import org.ngbed.heif.io.RandomAccessReader;

import java.io.IOException;

/**
 * ISO/IEC 14496-12:2015 pg.80
 */
public class PrimaryItemBox extends FullBox
{
	long itemID;

	public PrimaryItemBox(RandomAccessReader reader, Box box) throws IOException
	{
		super(reader, box);

		if (version == 0)
		{
			itemID = reader.getUInt16();
		}
		else
		{
			itemID = reader.getUInt32();
		}
		
		countBytesRead = reader.getPosition() - offset;
	}
}
