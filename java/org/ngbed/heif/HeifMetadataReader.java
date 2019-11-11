package org.ngbed.heif;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;

import org.ngbed.heif.metadata.HeifBoxHandler;

import com.drew.metadata.Metadata;

public class HeifMetadataReader
{

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
