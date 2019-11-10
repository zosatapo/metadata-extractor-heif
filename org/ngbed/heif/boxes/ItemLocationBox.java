package org.ngbed.heif.boxes;

import java.io.IOException;
import java.util.ArrayList;

import org.ngbed.heif.io.RandomAccessReader;

/**
 * ISO/IEC 14496-12:2015 pg.77-80
 */
public class ItemLocationBox extends FullBox
{
	int indexSize;
	int offsetSize;
	int lengthSize;
	int baseOffsetSize;
	long itemCount;
	ItemLocation[] locations;

	public ItemLocationBox(RandomAccessReader reader, Box box) throws IOException
	{
		super(reader, box);

		int holder = reader.getUInt8();
		offsetSize = (holder & 0xF0) >> 4;
		lengthSize = (holder & 0x0F);

		holder = reader.getUInt8();
		baseOffsetSize = (holder & 0xF0) >> 4;
		if ((version == 1) || (version == 2))
		{
			indexSize = (holder & 0x0F);
		}
		else
		{
			// Reserved
		}
		if (version < 2)
		{
			itemCount = reader.getUInt16();
		}
		else if (version == 2)
		{
			itemCount = reader.getUInt32();
		}

		locations = new ItemLocation[(int) itemCount + 1];

		long itemID = 0;
		int constructionMethod = 0;
		int dataReferenceIndex;
		byte[] baseOffset;
		int extentCount;

		for (int i = 1; i <= itemCount; i++)
		{
			if (version < 2)
			{
				itemID = reader.getUInt16();
			}
			else if (version == 2)
			{
				itemID = reader.getUInt32();
			}
			if ((version == 1) || (version == 2))
			{
				holder = reader.getUInt16();
				constructionMethod = (holder & 0x000F);
			}

			dataReferenceIndex = reader.getUInt16();
			baseOffset = reader.getBytes(baseOffsetSize);
			extentCount = reader.getUInt16();
			locations[i] = new ItemLocation(itemID, constructionMethod, dataReferenceIndex, baseOffset, extentCount);

			Long extentIndex = null;
			long extentOffset;
			long extentLength;
			Extent extent;
			for (int j = 0; j < extentCount; j++)
			{
				if ((version == 1) || (version == 2) && (indexSize > 0))
				{
					extentIndex = getIntFromUnknownByte(indexSize, reader);
				}
				extentOffset = getIntFromUnknownByte(offsetSize, reader);
				extentLength = getIntFromUnknownByte(lengthSize, reader);
				extent = new Extent(extentIndex == null ? null : extentIndex, extentOffset, extentLength);
				//System.out.println(itemID + ", " + constructionMethod + ", " + extentOffset + ", " + extentLength);
				locations[i].addExtent(extent);
			}
		}

		countBytesRead = reader.getPosition() - offset;
	}

	public ItemLocation getLocation(int itemID)
	{
		return locations[itemID];
	}

	public Long getIntFromUnknownByte(int variable, RandomAccessReader reader) throws IOException
	{
		switch (variable)
		{
		case (1):
			return (long) reader.getUInt8();
		case (2):
			return (long) reader.getUInt16();
		case (4):
			return (long) reader.getUInt32();
		case (8):
			return reader.getInt64();
		default:
			return null;
		}
	}

	public static class ItemLocation
	{
		public long itemID;
		public int constructionMethod;
		public int dataReferenceIndex;
		public byte[] baseOffset;
		public int extentCount;
		public ArrayList<Extent> extents = new ArrayList<Extent>();

		public ItemLocation(long itemID, int constructionMethod, int dataReferenceIndex, byte[] baseOffset,
				int extentCount)
		{
			this.itemID = itemID;
			this.constructionMethod = constructionMethod;
			this.dataReferenceIndex = dataReferenceIndex;
			this.baseOffset = baseOffset;
			this.extentCount = extentCount;
		}

		public void addExtent(Extent extent)
		{
			extents.add(extent);
		}
	}

	public static class Extent
	{
		public Long index;
		public long offset;
		public long length;

		public Extent(Long index, long offset, long length)
		{
			this.index = index;
			this.offset = offset;
			this.length = length;
		}
	}
}
