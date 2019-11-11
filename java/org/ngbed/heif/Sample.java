package org.ngbed.heif;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class Sample
{

	public static void main(String[] args)
	{
		try
		{
			String fileName = "sample/fruit_3024x4032.heic";
			if (args.length > 0)
			{
				fileName = args[0];
			}

			Metadata metadata = readMetadata(new File(fileName));

			for (Directory directory : metadata.getDirectories())
			{
				for (Tag tag : directory.getTags())
				{
					System.out.println(tag);
				}
			}
		}
		catch (ImageProcessingException | IOException e)
		{
			e.printStackTrace();
		}

	}

	private static Metadata readMetadata(File imageFile) throws IOException, ImageProcessingException
	{
		Metadata metadata = null;

		InputStream inputStream = new FileInputStream(imageFile);
		try
		{
			BufferedInputStream bufferedInputStream = inputStream instanceof BufferedInputStream
					? (BufferedInputStream) inputStream
					: new BufferedInputStream(inputStream);
			FileType fileType = FileTypeDetector.detectFileType(bufferedInputStream);
			if (fileType == FileType.Heif)
			{
				RandomAccessFile streamFile = new RandomAccessFile(imageFile, "rw");
				try
				{
					metadata = HeifMetadataReader.readMetadata(streamFile, streamFile.length());
				}
				finally
				{
					streamFile.close();
				}
			}
			else
			{
				metadata = ImageMetadataReader.readMetadata(bufferedInputStream, imageFile.length(), fileType);
			}
		}
		finally
		{
			inputStream.close();
		}

		return metadata;
	}

}
