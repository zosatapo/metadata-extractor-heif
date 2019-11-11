package org.ngbed.heif.boxes;


import java.io.IOException;

import com.drew.lang.Charsets;
import org.ngbed.heif.io.RandomAccessReader;

/**
 * ISO/IEC 14496-12:2015 pg.30 ISO/IEC 23008-12:2017 pg.22
 */
public class HandlerBox extends FullBox
{
	String handlerType;
	String name;

	public HandlerBox(RandomAccessReader reader, Box box) throws IOException
	{
		super(reader, box);

		
		reader.skip(4); // Pre-defined
		handlerType = reader.getString(4);
		reader.skip(12); // Reserved
		int remainBytes = (int) (box.size - (reader.getPosition() - offset));
		name = reader.getNullTerminatedString(remainBytes,
				Charsets.UTF_8);
		countBytesRead = reader.getPosition() - offset;
	}

	public String getHandlerType()
	{
		return handlerType;
	}
}
