package org.ngbed.heif.boxes;

import java.io.IOException;

import org.ngbed.heif.io.RandomAccessReader;

import com.drew.metadata.heif.HeifDirectory;

/**
 * ISO/IEC 23008-12:2017 pg.14
 */
public class AuxiliaryTypeProperty extends FullBox
{
	String auxType;
	int[] auxSubtype;

	public AuxiliaryTypeProperty(RandomAccessReader reader, Box box)
			throws IOException
	{
		super(reader, box);

		auxType = getZeroTerminatedString((int) box.size - 12, reader);
		// auxSubtype
		
		countBytesRead = reader.getPosition() - offset;
	}

	private String getZeroTerminatedString(int maxLengthBytes,
			RandomAccessReader reader) throws IOException
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < maxLengthBytes; i++)
		{
			stringBuilder.append((char) reader.getByte());
			if (stringBuilder.charAt(stringBuilder.length() - 1) == 0)
			{
				break;
			}
		}
		return stringBuilder.toString().trim();
	}
	
	public void addMetadata(HeifDirectory directory)
	{
	}
}
