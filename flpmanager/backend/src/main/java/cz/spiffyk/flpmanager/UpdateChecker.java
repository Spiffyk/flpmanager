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

public class UpdateChecker {
	private static final String GITHUB_API_RELEASE_URL = "https://api.github.com/repos/Spiffyk/flpmanager/releases/latest";
	
	public static UpdateInfo getUpdate() {
		return getUpdate(UpdateChecker.class.getPackage().getImplementationVersion());
	}
	
	public static UpdateInfo getUpdate(String version) {
		if (version == null) {
			return null;
		}
		
		try {
			final URL url = new URL(GITHUB_API_RELEASE_URL);
			final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
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
	
	private static String markdownToHtml(String markdown) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdown);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}
	
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
	
	private static class GitHubRelease {
		@Getter private String tag_name;
		@Getter private String name;
		@Getter private String body;
		@Getter private String html_url;
	}
	
	@ToString(exclude="notes")
	public static class UpdateInfo {
		@Getter private String name;
		@Getter private int[] version;
		@Getter private String notes;
		@Getter private String url;
	}
}
