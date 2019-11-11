package org.ngbed.heif.boxes;

import org.ngbed.heif.io.RandomAccessReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IEC 14496-12:2015 pg.80, 89-90
 */
public class ItemProtectionBox extends FullBox
{
	int protectionCount;
	ArrayList<ProtectionSchemeInfoBox> protectionSchemes;

	public ItemProtectionBox(RandomAccessReader reader, Box box)
			throws IOException
	{
		super(reader, box);

		protectionCount = reader.getUInt16();
		protectionSchemes = new ArrayList<ProtectionSchemeInfoBox>(
				protectionCount);
		for (int i = 1; i <= protectionCount; i++)
		{
			protectionSchemes.add(new ProtectionSchemeInfoBox(reader, box));
		}
		
		countBytesRead = reader.getPosition() - offset;
	}

	class ProtectionSchemeInfoBox extends Box
	{
		public ProtectionSchemeInfoBox(RandomAccessReader reader, Box box)
				throws IOException
		{
			super(box);
		}

		class OriginalFormatBox extends Box
		{
			String dataFormat;

			public OriginalFormatBox(RandomAccessReader reader, Box box)
					throws IOException
			{
				super(reader);

				dataFormat = reader.getString(4);
			}
		}
	}
}
