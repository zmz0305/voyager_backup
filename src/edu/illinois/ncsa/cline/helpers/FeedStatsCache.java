package edu.illinois.ncsa.cline.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.database.shared.FeedStats;

/**
 * cache for faster re-access of feeds data
 * @author zmz0305
 */
public class FeedStatsCache {

	/** an arrayList containing all the cache data */
	private ArrayList<FeedStats> data = null;

	/**
	 * reset cache data
	 */
	public void reset(){
	    data = null;
	}

	/**
	 * get the cached feedstats data
	 * @return the data
	 */
	public ArrayList<FeedStats> getData() {
		return data;
	}

	/**
	 * create a new data for the singleton, by days
	 * 
	 * @param days
	 *            number of days
	 * @throws SQLException
	 */
	public void create(int days) throws SQLException {
		data = CrawlStatsReader.getStats(days);
	}

	/**
	 * create a new data for the singleton, by date range
	 * 
	 * @param begin
	 *            beginning date
	 * @param end
	 *            end date
	 * @throws SQLException
	 */
	public void create(Date begin, Date end) throws SQLException {
		data = CrawlStatsReader.getStats(begin, end);
	}

	/**
	 * delete an entry in data by the matching url, since url is unique
	 * 
	 * @param url
	 */
	public void deleteByUrl(String url) {
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getUrl().equals(url)) {
					data.remove(i);
				}
			}
		}
		else{
			System.out.println("no data in cache, delete from cache failed");
		}
	}

	/**
	 * add a new data entry into cache
	 * @param item FeedStats item to be add into data
	 */
	public void addToCache(FeedStats item) {
		if (data != null) {
			data.add(new FeedStats(item.getUrl(), item.getScans(), item
					.getChanged(), item.getUnchanged(), item.getFailures(),
					item.getDup_urls(), item.getPages(), item.getRelevant(),
					item.getIrrelevant(), item.getExcluded(), item.getEmpty(),
					item.getUnparsable(), item.getForeign_language(), item
							.getCorrupted(), item.getDuplicate(), item
							.getNocontent(), item.getRowid()));
		}
		else{
			System.out.println("no data in cache, add to cache failed");
		}
	}

}
