/**
 * com.nowakmaj.loc.scanner.FileValidator
 */
package com.nowakmaj.loc.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sprawdza czy dany plik jest zgodny z rozszerzeniem.
 */
public class FileValidator {

	/**
	 * Poczatego regex'a do sprawdzania rozszerzenia.
	 */
	private final String FILE_PATTERN_BEGIN = "([^\\s]*(\\.(?i)(";
	/**
	 * Koniec regex'a do sprawdzania rozszerzenia.
	 */
	private final String FILE_PATTERN_END = "))$)";
	/**
	 * Lista paterrnow do sprawdzania regex'ow.
	 */
	private ArrayList<Pattern> patterns;
	
	/**
	 * Konstruktor.
	 *
	 *  @param lista akceptowanych rozszerzen.
	 */
	public FileValidator(ArrayList<String> acceptedExtensions)
	{
		patterns = new ArrayList<Pattern>();
		for (String extension: acceptedExtensions)
		{
			patterns.add(Pattern.compile(
				FILE_PATTERN_BEGIN + extension + FILE_PATTERN_END));
		}
	}
	
	/**
	 * Sprawdza czy plik ma odpowiednie rozszrzenie.
	 *
	 * @param plik
	 * @return true jesli tak, false jest nie
	 */ 
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
