package com.hqup.jokes.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ActivityManager;
import android.content.Context;

import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.utils.Logger;

/*
 * https://sites.google.com/site/pyximanew/blog/androidunderstandingddmslogcatmemoryoutputmessages
 */

/**
 * 
 * @author Andrew2212 Class realizes pattern Singlton in order to store
 *         pageCurrent and pageMain of the parsed site
 * 
 */
public class Parser {

	public static final String URL = "http://semechki.tv";
	private static final String FILE_NAME = "tempPageFile";
	private static final String DIR_NAME = "tempDir";
	private static final String CHARSET_NAME = "UTF-8";
	private static final String PATH_TO_PAGE = "/?page=";
	private static final int PAGE_MAX_NUM = 999;

	private static int pageMain;
	private static int pageCurrent;
	private static int pageFirst = 0;
	private static int deltaPage = 1;
	private static boolean isFirstParsingDone = false;

	private static Parser parser;
	private static Context context;

	private Elements listElementsJokes;
	private Elements listElementsNumbers;
	private List<Joke> listJokes = new ArrayList<Joke>();

	/**
	 * Just for time estimate
	 */
	private long timeStart;

	private Parser(Context context) {

	}

	/**
	 * Realizes pattern Singlton
	 * 
	 * @return Parser parser
	 */
	public static Parser getParser(Context context) {

		Parser.context = context;
		if (parser == null) {
			parser = new Parser(context);
		}

		// Check how much memory we should use
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Logger.v("am.getMemoryClass() = " + am.getMemoryClass());
		Logger.v("am.getLargeMemoryClass() = " + am.getLargeMemoryClass());

		return parser;
	}

	// ===========Public Methods=====================

	public static void setIsFirstParsingDone(boolean state) {
		isFirstParsingDone = state;
	}

	/**
	 * 
	 * @return sorted by Numbers List of jokes from current site page
	 */
	@SuppressWarnings("finally")
	public List<Joke> receiveListJokeFromSite() {

		Logger.v();
		timeStart = System.currentTimeMillis();

		// Get 'url' for destination page
		String url = createUrl();

		// Parse page
		parseSite(url);
		Logger.time(timeStart);

		// Clear List
		listJokes.clear();

		/*
		 * 'try-catch' is needed since (listElementsJokes.size() !=
		 * arrJokeNumbers.length)
		 */
		try {

			String joke = null;
			String number = null;

			// Getting List of jokes from current page
			for (int i = 0; i < listElementsNumbers.size(); i++) {

				joke = listElementsJokes.get(i).ownText();
				number = listElementsNumbers.get(i).ownText();
				listJokes.add(new Joke(number, joke));

			}

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();

		} finally {

			// Collections.sort(listJokes, Joke.byNumComparator);
			//
			// for (Joke joke : listJokes) {
			// Logger.v(joke.toString());
			// }

			Logger.time(timeStart);

			return listJokes;
		}
	}

	// ======Public static Methods for Current page==============

	public static void increaseCurrentPage() {
		pageCurrent += deltaPage;
		Logger.v("pageCurrent = " + pageCurrent);
	}

	public static void decreaseCurrentPage() {
		pageCurrent -= deltaPage;
		Logger.v("pageCurrent = " + pageCurrent);
	}

	public static int getCurrentPage() {
		Logger.v("pageCurrent = " + pageCurrent);
		if (pageCurrent < pageFirst) {
			pageCurrent = pageFirst;
		}
		if (pageMain < pageCurrent) {
			pageCurrent = pageMain;
		}
		return pageCurrent;
	}

	public static boolean isCurrentPageMain() {
		return (pageCurrent >= pageMain);
	}

	public static boolean isCurrentPageFirst() {
		return (pageCurrent <= pageFirst);
	}

	public static void setPageDelta(int deltaPage) {
		Parser.deltaPage = deltaPage;
	}

	// ===========Private Methods=====================

	/**
	 * 
	 * @param url
	 *            address destination site page<br>
	 *            Parses site page: obtains Elements 'listElementsJokes' and
	 *            String[] of 'joke numbers'</br>It's executed into
	 *            AsyncTask::doInBackground()
	 */
	private void parseSite(String url) {

		Logger.v();
		Document doc = null;

		// Getting Document
		try {
			doc = createDoc(url);
		} catch (IOException e) {
			Logger.e("Document doc == null! Connection is absent!");
			e.printStackTrace();
			return;
		} catch (Throwable th) {
			th.printStackTrace();
			Logger.i("***OutOfMemory!****");
			return;
		}
		Logger.time(timeStart, " createDoc");

		/*
		 * Getting pugeCurrent and setting pageMain
		 * http://jsoup.org/cookbook/extracting-data/selector-syntax
		 */
		if (!isFirstParsingDone) {

			try {
				pageCurrent = Integer.parseInt(doc.select("[href^=/?]").first()
						.text());
			} catch (NumberFormatException e) {
				pageCurrent = PAGE_MAX_NUM;
				e.printStackTrace();
			}

			pageMain = pageCurrent;
			isFirstParsingDone = true;
		}
		Logger.time(timeStart, " currentPage");

		// Getting array of jokes numbers such as '#54350'
		// String jokeNumbers = doc.getElementsByClass("bor-none").text();
		listElementsNumbers = doc.select("a:containsOwn(#)");
		Logger.time(timeStart, " listElementsNumbers");

		// Getting List of jokes 'listElementsJokes'
		listElementsJokes = doc.select("p");
		Logger.time(timeStart, " listElementsJokes");
	}

	/**
	 * 
	 * @return URL of the page where we'll get jokes from</br>It's executed into
	 *         AsyncTask::doInBackground()
	 */
	private String createUrl() {

		String url = null;
		if (!isFirstParsingDone) {
			url = URL;
		} else {
			url = URL + PATH_TO_PAGE + getCurrentPage();
		}
		Logger.v("Current url = " + url);
		return url;

	}

	/**
	 * 
	 * @param URL
	 *            parsed site URL
	 * @return Document doc = Jsoup.connect(URL).get()</br>It's executed into
	 *         AsyncTask::doInBackground()
	 */
	private Document createDoc(String url) throws IOException {

		/*
		 * Try to save page to file and parse file to Document
		 */
		Connection connection = Jsoup.connect(url);
		Response response = connection.execute();
		String body = response.body();
		Logger.time(timeStart, "response.body()");

		/*
		 * Get Document from parsed File (and before write string 'body' to
		 * 'tempFile')
		 */
		// writeStringToFile(tempFile, body);
		// Logger.time(timeStart, "writeStringToFile");
		// return Jsoup.parse(tempFile, CHARSET_NAME);

		/*
		 * Get Document from parsed HTML String
		 */
		return Jsoup.parse(body, CHARSET_NAME);
	}

	// ==========Private Methods for parsingFromFile==================
	/**
	 * 
	 * @param body
	 *            String (from the parsed HTML page) which will be written
	 */
	@SuppressWarnings("unused")
	private void writeStringToFile(String body) {

		// Create 'tempFile' for parsingFromFile
		File tempFile = createFileTemp();

		Logger.v(tempFile.getAbsolutePath());
		try {
			/*
			 * FileWriter - extends java.io.OutputStreamWriter Convenience class
			 * for writing character files.
			 */
			FileWriter fileWriter = new FileWriter(tempFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(body);

			bufferedWriter.flush();
			if (bufferedWriter != null)
				bufferedWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		Logger.v("tempFile size in kByte = " + tempFile.length() / 1024);
		tempFile.delete();
		Logger.v("tempFile.exists() = " + tempFile.exists());

	}

	/**
	 * Creates tempFile for saving 'String body = response.body()' for following
	 * parsing
	 */
	private File createFileTemp() {
		String path = context.getFilesDir() + "/" + DIR_NAME;
		File dir = new File(path);
		dir.mkdirs();
		return new File(dir, FILE_NAME);
	}

}
