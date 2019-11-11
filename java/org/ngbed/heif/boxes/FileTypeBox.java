package org.ngbed.heif.boxes;


import org.ngbed.heif.io.RandomAccessReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IEC 14496-12:2015 pg.8
 */
public class FileTypeBox extends Box
{
	String majorBrand;
	long minorVersion;
	ArrayList<String> compatibleBrands;

	public FileTypeBox(RandomAccessReader reader, Box box) throws IOException
	{
		super(box);

		majorBrand = reader.getString(4);
		minorVersion = reader.getUInt32();
		compatibleBrands = new ArrayList<String>();
		for (int i = 16; i < size; i += 4)
		{
			compatibleBrands.add(reader.getString(4));
		}
		
		countBytesRead = reader.getPosition() - offset;
	}

	public void addMetadata(HeifDirectory directory)
	{
		directory.setString(HeifDirectory.TAG_MAJOR_BRAND, majorBrand);
		directory.setLong(HeifDirectory.TAG_MINOR_VERSION, minorVersion);
		directory.setStringArray(HeifDirectory.TAG_COMPATIBLE_BRANDS,
				compatibleBrands.toArray(new String[compatibleBrands.size()]));
	}

	public ArrayList<String> getCompatibleBrands()
	{
		return compatibleBrands;
	}
}
