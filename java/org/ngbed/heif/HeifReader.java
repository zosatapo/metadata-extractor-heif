package org.ngbed.heif;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

import org.ngbed.heif.boxes.Box;
import org.ngbed.heif.io.RandomAccessFileReader;
import org.ngbed.heif.io.RandomAccessReader;

import com.drew.metadata.Metadata;

public class HeifReader
{
//	public final static String[] ZERO_PADDINGS =
//	{ "", "  -->", "    -->" };

	public static void extract(Metadata metadata, RandomAccessFile streamFile, long streamLength, HeifHandler handler)
			throws IOException, DataFormatException
	{
		RandomAccessFileReader reader = new RandomAccessFileReader(streamFile);
		reader.setMotorolaByteOrder(true);
		processBoxes(0, reader, streamLength, handler);
	}

	public static void processBoxes(int depth, RandomAccessReader reader, long atomEnd, HeifHandler handler)
	{
		try
		{
			while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd)
			{

				Box box = new Box(reader);
				// Determine if fourCC is container/atom and process accordingly
				// Unknown atoms will be skipped

				if (handler.shouldAcceptContainer(box))
				{
//					System.out.println(ZERO_PADDINGS[depth] + "box[acceptContainer] -->" + box);
					handler.processContainer(depth, box, reader);
				}
				else if (handler.shouldAcceptBox(box))
				{
//					System.out.println(ZERO_PADDINGS[depth] + "box[acceptBox] -->" + box);
					handler.processBox(depth, box, reader);
				}
				else if (box.size > 0)
				{
//					System.out.println(ZERO_PADDINGS[depth] + "box[Unknown] -->" + box);
					reader.skip(box.size - box.countBytesRead);
				}
				else
				{
					break;
				}
			}

			handler.processCompleted(depth, reader);

		}
		catch (IOException e)
		{
			// Currently, reader relies on IOException to end
		}
	}
}
