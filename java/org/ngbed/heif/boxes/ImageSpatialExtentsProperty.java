package org.ngbed.heif.boxes;


import org.ngbed.heif.io.RandomAccessReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * ISO/IEC 23008-12:2017 pg.11
 */
public class ImageSpatialExtentsProperty extends FullBox
{
	long width;
	long height;

	public ImageSpatialExtentsProperty(RandomAccessReader reader, Box box)
			throws IOException
	{
		super(reader, box);

		width = reader.getUInt32();
		height = reader.getUInt32();
		
		countBytesRead = reader.getPosition() - offset;
	}

	public void addMetadata(HeifDirectory directory)
	{
		if (!directory.containsTag(HeifDirectory.TAG_IMAGE_WIDTH)
				&& !directory.containsTag(HeifDirectory.TAG_IMAGE_HEIGHT))
		{
			directory.setLong(HeifDirectory.TAG_IMAGE_WIDTH, width);
			directory.setLong(HeifDirectory.TAG_IMAGE_HEIGHT, height);
		}
	}
}
