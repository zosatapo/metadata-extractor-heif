package org.ngbed.heif.boxes;


import org.ngbed.heif.io.RandomAccessReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * ISO/IEC 23008-12:2017 pg.15
 */
public class ImageRotationBox extends Box
{
	int angle;

	public ImageRotationBox(RandomAccessReader reader, Box box)
			throws IOException
	{
		super(box);

		// First 6 bits are reserved
		angle = reader.getUInt8() & 0x03;
		
		countBytesRead = reader.getPosition() - offset;
	}

	public void addMetadata(HeifDirectory directory)
	{
		if (!directory.containsTag(HeifDirectory.TAG_IMAGE_ROTATION))
		{
			directory.setInt(HeifDirectory.TAG_IMAGE_ROTATION, angle);
		}
	}
}
