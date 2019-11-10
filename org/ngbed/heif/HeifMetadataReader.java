package org.ngbed.heif;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

import org.ngbed.heif.metadata.HeifBoxHandler;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class HeifMetadataReader
{

	public static final void main(String[] args)
	{
		// File contentFile = new File("D:\\data\\cheers_1440x960.heic");
		File contentFile = new File("D:\\data\\test.heic");

		try
		{

			RandomAccessFile streamFile = new RandomAccessFile(contentFile, "rw");
			try
			{
				Metadata metadata = readMetadata(streamFile, contentFile.length());
				for (Directory directory : metadata.getDirectories())
				{
					for (Tag tag : directory.getTags())
					{
						String tagName = tag.getTagName();
						System.out.println(directory.getName() + " - " + tag.getTagType() + " - " + tagName + " - "
								+ tag.getDescription());
					}
				}

			}
			finally
			{
				streamFile.close();
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Metadata readMetadata(File imageFile) throws IOException
	{
		RandomAccessFile streamFile = new RandomAccessFile(imageFile, "rw");
		try
		{
			return HeifMetadataReader.readMetadata(streamFile, streamFile.length());
		}
		finally
		{
			streamFile.close();
		}
	}

	public static Metadata readMetadata(RandomAccessFile streamFile, long streamLength) throws IOException
	{
		try
		{
			Metadata metadata = new Metadata();
			HeifReader.extract(metadata, streamFile, streamLength, new HeifBoxHandler(metadata));
			return metadata;
		}
		catch (DataFormatException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
