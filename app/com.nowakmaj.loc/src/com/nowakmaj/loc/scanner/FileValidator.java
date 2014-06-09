package com.nowakmaj.loc.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileValidator {

	private final String FILE_PATTERN_BEGIN = "([^\\s]*(\\.(?i)(";
	private final String FILE_PATTERN_END = "))$)";
	
	private ArrayList<Pattern> patterns;
	
	public FileValidator(ArrayList<String> acceptedExtensions)
	{
		patterns = new ArrayList<Pattern>();
		for (String extension: acceptedExtensions)
		{
			patterns.add(Pattern.compile(
				FILE_PATTERN_BEGIN + extension + FILE_PATTERN_END));
		}
	}
	
	public boolean isFileValid(File file)
	{
		Matcher matcher;
		for (Pattern pattern: patterns)
		{
			matcher = pattern.matcher(file.getName());
			if (matcher.matches()) return true;
		}
		return false;
	}
}
