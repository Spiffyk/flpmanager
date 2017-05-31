package cz.spiffyk.flpmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.ToString;

/**
 * A class for checking whether an update is available. The class uses GitHub API v3 for this. It gets the latest
 * release and compares its version with the current installed version of the program.
 * @author spiffyk
 */
public class UpdateChecker {
	
	/**
	 * The URL to fetch the release information from
	 */
	private static final String GITHUB_API_RELEASE_URL = "https://api.github.com/repos/Spiffyk/flpmanager/releases/latest";
	
	/**
	 * The {@code Accept} header for the GitHub API
	 */
	private static final String GITHUB_API_ACCEPT_HEADER = "application/vnd.github.v3+json";
	
	
	
	/**
	 * Disabled constructor, this is a static library class
	 */
	private UpdateChecker() {}
	
	
	
	/**
	 * Checks for updates and, if an update is available, returns information about it.
	 * @return Information about the update. If no update is available, returns {@code null}.
	 */
	public static UpdateInfo getUpdate() {
		return getUpdate(UpdateChecker.class.getPackage().getImplementationVersion());
	}
	
	/**
	 * Checks for updates and, if an update is available, returns information about it. Uses the version string
	 * provided as the "current" version installed (this is mainly for debugging purposes; normally,
	 * {@code getUpdate()} should be used). If no {@code version} is {@code null}, it returns {@code null}.
	 * 
	 * @param version The version to use as the "current" version installed
	 * @return Information about the update. If no update is available, returns {@code null}.
	 */
	public static UpdateInfo getUpdate(String version) {
		if (version == null) {
			return null;
		}
		
		try {
			final URL url = new URL(GITHUB_API_RELEASE_URL);
			final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.addRequestProperty("Accept", GITHUB_API_ACCEPT_HEADER);
			conn.setRequestMethod("GET");
			
			final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			
			final Gson gson = new Gson();
			final GitHubRelease release = gson.fromJson(sb.toString(), GitHubRelease.class);
			
			int[] current = parseVersion(version);
			int[] released = parseVersion(release.tag_name);
			
			if (isUpdateable(current, released)) {
				final UpdateInfo info = new UpdateInfo();
				info.name = release.name;
				info.version = released;
				info.notes = markdownToHtml(release.body);
				info.url = release.html_url;
				return info;
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			System.err.println("IOException while checking for updates.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Uses Commonmark to convert a markdown string into a HTML string.
	 * @param markdown The markdown string
	 * @return A HTML string
	 */
	private static String markdownToHtml(String markdown) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdown);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}
	
	/**
	 * Parses a version string and returns the version as an array of integers. Ignores any characters other than
	 * numbers ({@code 0-9}) and fullstops ({@code .}).
	 * @param string The version string to parse
	 * @return An array of integers representing the version, the first integer being the most major version, the last
	 * being the most minor version.
	 */
	private static int[] parseVersion(String string) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((c >= '0' && c <= '9') || c == '.') {
				sb.append(c);
			}
		}
		
		String[] numbers = sb.toString().split("\\.");
		int[] version = new int[numbers.length];
		for (int i = 0; i < version.length; i++) {
			version[i] = Integer.parseInt(numbers[i]);
		}
		
		return version;
	}
	
	/**
	 * Takes two arrays of integers as versions and compares them to check whether the released version is newer than
	 * the current installed version.
	 * @param current The current installed version
	 * @param released The current released version
	 * @return {@code true} if the released version is newer than the current installed version
	 */
	private static boolean isUpdateable(int[] current, int[] released) {
		if (current.length > released.length) {
			released = Arrays.copyOf(released, current.length);
		} else if (current.length < released.length) {
			current = Arrays.copyOf(current, released.length);
		}
		
		int length = current.length;
		for (int i = 0; i < length; i++) {
			if (released[i] > current[i]) {
				return true;
			} else if (released[i] < current[i]) {
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * A class for GSON to store parsed data from GitHub in.
	 * @author spiffyk
	 */
	private static class GitHubRelease {
		@Getter private String tag_name;
		@Getter private String name;
		@Getter private String body;
		@Getter private String html_url;
	}
	
	/**
	 * The final update information data. This data is immutable outside of the update checker.
	 * @author spiffyk
	 */
	@ToString(exclude="notes")
	public static class UpdateInfo {
		/**
		 * The name of the release
		 */
		@Getter private String name;
		
		/**
		 * The array representation of the version
		 */
		@Getter private int[] version;
		
		/**
		 * Release notes in HTML format
		 */
		@Getter private String notes;
		
		/**
		 * The download URL of the release
		 */
		@Getter private String url;
	}
}
