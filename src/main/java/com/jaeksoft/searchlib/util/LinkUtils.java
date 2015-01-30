/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2008-2014 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.utils.URIUtils;

import com.jaeksoft.searchlib.Logging;
import com.jaeksoft.searchlib.crawler.web.database.UrlFilterItem;
import com.jaeksoft.searchlib.crawler.web.database.UrlFilterList;
import com.opensearchserver.utils.StringUtils;

public class LinkUtils extends com.opensearchserver.utils.LinkUtils {

	public final static URL getLink(final URL currentURL, final String srcHref,
			final UrlFilterItem[] urlFilterList, final boolean removeFragment) {

		if (srcHref == null)
			return null;
		String href = srcHref.trim();
		if (href.length() == 0)
			return null;

		String fragment = null;
		URI u = null;
		try {
			if (!href.contains("://")) {
				URI currentURI = null;
				try {
					currentURI = currentURL.toURI();
				} catch (URISyntaxException e) {
					currentURI = new URI(URLEncoder.encode(
							currentURL.toString(), "UTF-8"));
				}
				try {
					u = URIUtils.resolve(currentURI, href);
				} catch (IllegalArgumentException e) {
					href = URLEncoder.encode(href, "UTF-8");
					u = URIUtils.resolve(currentURI, href);
				}
			} else {
				try {
					u = new URI(href);
				} catch (URISyntaxException e) {
					u = new URI(URLEncoder.encode(href, "UTF-8"));
				}
			}
			href = UrlFilterList.doReplace(u.getHost(), u.toString(),
					urlFilterList);
			URI uri = URI.create(href);
			uri = uri.normalize();

			String p = uri.getPath();
			if (p != null)
				if (p.contains("/./") || p.contains("/../"))
					return null;

			if (!removeFragment)
				fragment = uri.getRawFragment();

			URL finalURL = new URI(uri.getScheme(), uri.getUserInfo(),
					uri.getHost(), uri.getPort(), uri.getPath(),
					uri.getQuery(), fragment).normalize().toURL();
			return finalURL;
		} catch (MalformedURLException e) {
			Logging.info(e.getMessage());
			return null;
		} catch (URISyntaxException e) {
			Logging.info(e.getMessage());
			return null;
		} catch (IllegalArgumentException e) {
			Logging.info(e.getMessage(), e);
			return null;
		} catch (UnsupportedEncodingException e) {
			Logging.info(e.getMessage(), e);
			return null;
		}

	}

	public static void main(String[] args) throws MalformedURLException,
			UnsupportedEncodingException {
		System.out.println(getLink(new URL(
				"http://www.example.com/test/in-75?l=75&co=FR&start=20"),
				"?l=75&co=FR&start=20", null, false));
		System.out.println(getLink(new URL("http://www.example.com/test/"),
				"/category/index.jsp?categoryId=4955781&ab=denim & supply",
				null, false));
	}

	public final static URL getURL(String urlString, boolean logError) {
		if (StringUtils.isEmpty(urlString))
			return null;
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			if (logError)
				Logging.warn("Malformed URL: " + e);
			return null;
		}
	}
}
