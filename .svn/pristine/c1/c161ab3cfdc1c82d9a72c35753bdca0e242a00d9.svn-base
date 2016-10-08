/*
EssentialScanner is a web and file system scanner and indexing engine with an 

easy to use web services interface.

Copyright (C) 2009  RiverGlass Inc.
 
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published  
by the Free Software Foundation, either version 3 of the License, or
any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package edu.illinois.ncsa.cline.helpers;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.rg.common.datatypes.ttw.InexactQueryException;

/**
 * This is an alternative implementation of the tilted time window optimized for speed. This tilted time window
 * implementation does not require the GregarianCalendar to be used to do computations on time boundaries. It will
 * operate over spans of seconds, minutes, hours, days and weeks only.
 * <p>
 * Since we are not supporting any time boundaries beyond weeks, there are a fixed number of milliseconds between each
 * update. At any point in time, the window spans the current second, including events that have not elapsed yet, and
 * goes back some amount in time from the end of the current second.
 * <p>
 * To take the next step and perform over months and years, the calendar will have to be used to compute the time
 * boundaries, since months have a varying number of days.
 * <p>
 * This tilted time window slides. By this I mean that provides a view of recent history, not fixed segments in time.
 * For example, say the time window does seconds, minutes and hours. The frame of the window for seconds spans the last
 * minute.
 * <p>
 * @author redman
 */
public class TimeWindow implements Serializable {

   // a TTW is composed of frames, one frame for each level of resolution
   // e.g., there may be a frame for seconds, another for minutes, and a
   // third for hours
   // each frame is composed of slots
   // e.g., there may be 60 slots in the seconds frame, 60 slots in the
   // minutes frame, and 24 slots in the hours frame

   /** */
   private static final long serialVersionUID = -7469408493653465934L;

   /**
    * contains the number of milliseconds spanned by a single slot in each of the different frames.
    */
   long[] millisPerSlot;

   /** the number of slots in each frame. */
   int[] slotsPerFrame;

   /**
    * these are the counts, including the roll-up accumulator between frames. There will be one roll-up accumulator
    * between each of the frames.
    */
   long[] counts;

   /** this is the timestamp marking the end of the most recent slot. */
   long last = -1;

   /**
    * this is the timestamp passed to the update() method the first time it is called on this instance.
    */
   long first = -1;

   /**
    * counts the number of times the buffer of a frame was updated. When that count equals the number of slots at the
    * next finer granularity, we shift everything, adding the last count to the buffer of the next coarser granularity.
    */
   int bufferUpdates[];

   /** the last slot containing a valid count. TODO last in what sense? */
   int lastSlot = 0;
   
   /**
    * We are given the frequencies, the frame sizes, and the start time. For example, to create an instance that
    * consists of 60 1-second slots at the finest granularity, followed by 60 1-minute slots, followed by 24 1-hour
    * slots, followed by 7 1-day slots at the most coarse granularity, do this:
    * <p>
    * 
    * <pre>
    * long[] freqs = { 1000, 60 * 1000, 60 * 60 * 1000, 24 * 60 * 60 * 1000 };
    * int[] sizes = { 60, 60, 24, 7 };
    * NTiltedTimeWindow ttw = new NTiltedTimeWindow(freqs, sizes, startTime);
    * </pre>
    * @param f the frequencies in milliseconds.
    * @param sizes the frame sizes.
    * @param start the start time in milliseconds.
    */
   public TimeWindow(long[] f, int[] sizes, long start) {
      // make our own copies of the arrays -- otherwise the caller could
      // change them underneath us at any time
      this.millisPerSlot = new long[f.length];
      System.arraycopy(f, 0, this.millisPerSlot, 0, f.length);
      this.slotsPerFrame = new int[sizes.length];
      System.arraycopy(sizes, 0, this.slotsPerFrame, 0, sizes.length);

      // Compute the size of the counts array. This will include
      // slots for each frame, and a buffer at the end to accumulate
      // the contents of the frame before it is rolled up
      int size = 0;
      for (int i = 0; i < this.slotsPerFrame.length; i++) {
         size += this.slotsPerFrame[i];

         // include the roll-up accumulator
         size++;
      }

      // allocate the counts.
      this.counts = new long[size];
      bufferUpdates = new int[this.millisPerSlot.length - 1];

      // Init the start times for the frames
      for (int i = 0; i < this.millisPerSlot.length - 1; i++) {
         bufferUpdates[i] = 0;
      }
   }

   /**
    * This instance is being initialized from a soap layer payload. All configuration and 
    * data are contained in the payload.
    * @param payload Payload contains all the information needed to init this instance.
    */
   public TimeWindow(HashMap<String, Object> payload) {
      this.setPayload(payload);
   }
   
   /**
    * This method will approximate the count between a given start time and end time. If the start time is more recent
    * than anything in the TTW, this method returns zero. If the end time is older than anything in the TTW, this method
    * returns zero.
    * @param startTime the start time.
    * @param endTime the ending time.
    * @return the approximate count.
    * @throws IllegalArgumentException if <code>startTime</code> is greater than <code>endTime</code>.
    */
   public long approximate(long startTime, long endTime) {

      // if the start time is greater than the end time, report the error
      if (startTime > endTime) {
         throw new IllegalArgumentException("Start must be less than end.");
      }

      // if the start time is later than the end of the TTW, the count is zero
      if (startTime > last) {
         return 0L;
      }

      // Compute the start time frame and slot
      long current = last;
      int startSlot = 0;
      double startFraction = 0;
      int frame = 0;

      // At each frame, check to see if the frame includes the start time
      // After each frame, check the roll-up accumulator as well.
      // If the start time is beyond the window, return -1;
      if (endTime >= last) {
         startSlot = 0;
         startFraction = 1.0;
      } else {
         for (; frame < millisPerSlot.length; frame++) {

            // check the roll-up accumulator to see if it contains the start time.
            long span;
            if (frame == 0)
               span = millisPerSlot[frame];
            else
               span = millisPerSlot[frame - 1] * bufferUpdates[frame - 1];
            current -= span;
            if (endTime > current) {

               // Compute the percent of the slot covered.
               startFraction = (double) (endTime - current) / (double) span;
               break;
            }
            startSlot++;

            // compute the time span this frame covers.
            span = millisPerSlot[frame] * slotsPerFrame[frame];
            current -= span;
            if (endTime >= current) {

               // Compute the offset into the frame
               long diff = endTime - current;
               int offset = (int) ((span - diff) / millisPerSlot[frame]);

               // Get the boundary of the previous slot.
               startFraction = endTime - (current + (millisPerSlot[frame] * (slotsPerFrame[frame] - offset - 1)));
               startFraction /= millisPerSlot[frame];
               startSlot += offset;
               break;
            }
            startSlot += slotsPerFrame[frame];
         }
      }

      // If we are outside the window, return zero
      if (frame == millisPerSlot.length) {
         return 0L;
      }

      // LAM-tlr this is inefficient, we are starting over, we don't really
      // need to, but the bookkeeping is simpler
      current = last;
      int endSlot = 0;
      double endFraction = 0;
      frame = 0;

      // At each frame, check to see if the frame includes the start time
      // After each frame, check the roll-up accumulator as well.
      for (; frame < millisPerSlot.length; frame++) {

         // check the roll-up accumulator to see if it contains the start time.
         long span;
         if (frame == 0)
            span = millisPerSlot[frame];
         else
            span = millisPerSlot[frame - 1] * bufferUpdates[frame - 1];
         current -= span;
         if (startTime >= current) {

            // The roll-up contained start time
            endFraction = (double) (span - (startTime - current)) / (double) span;
            break;
         }
         endSlot++;

         // compute the time span this frame covers.
         span = millisPerSlot[frame] * slotsPerFrame[frame];
         current -= span;
         if (startTime >= current) {

            // Compute the offset into the frame
            long diff = startTime - current;
            int offset = (int) ((span - diff) / millisPerSlot[frame]);

            // Get the boundry of the previous slot.
            endFraction = startTime - (current + (millisPerSlot[frame] * (slotsPerFrame[frame] - offset - 1)));
            endFraction /= millisPerSlot[frame];
            endFraction = 1.0 - endFraction;
            if (endFraction == 0) {
               offset--;
               endFraction = 1.0;
            }
            endSlot += offset;
            break;
         }

         endSlot += slotsPerFrame[frame];
      }

      // If we did not find an end slot that actually included the end time,
      // include all that we have.
      if (endSlot >= counts.length) {
         endSlot = counts.length - 1;
         endFraction = 1.0;
      }

      // Sum them all up.
      if (startSlot == endSlot) {

         // The entire requested timespan fell in one slot.
         double singleSlotPercent = (double) (endTime - startTime) / (double) this.millisPerSlot[frame];
         return (long) (((double) counts[startSlot] * singleSlotPercent) + 0.5);
      } else {

         // Sum the time window
         double sum = ((double) counts[startSlot] * startFraction);
         startSlot++;
         for (; startSlot < endSlot; startSlot++) {
            sum += counts[startSlot];
         }
         sum += ((double) counts[endSlot] * endFraction);
         return (long) (sum + 0.5);
      }
   }

   /**
    * Compute the start of the window, the earliest point in time that this window can cover.
    * @return the start time for this vector.
    * @see org.rg.common.datatypes.ttw.XTimeWindow#getStartTime()
    */
   public long getStartTime() {
      long current = getCurrentTime();
      current -= millisPerSlot[0] * slotsPerFrame[0];
      for (int i = 1; i < millisPerSlot.length; i++) {

         // subtract the accumulator.
         current -= millisPerSlot[i - 1] * bufferUpdates[i - 1];
         current -= millisPerSlot[i] * slotsPerFrame[i];
      }
      return current;
   }

   /**
    * returns the time of the most current slot that is fully updated. This does not include the accumulator for the
    * current slot of the smallest frequency, since it is not yet complete, and therefore not accurate.
    * @return the current time.
    */
   public long getCurrentTime() {
      return last + millisPerSlot[0];
   }

   /**
    * I see no use for this. This method also approximates, for now.
    * @param startTime the start time.
    * @param endTime the end time.
    * @return the approximate count.
    * @throws InexactQueryException
    */
   public long query(long startTime, long endTime) throws InexactQueryException {
      return approximate(startTime, endTime);
   }

   /**
    * @see org.rg.common.datatypes.ttw.XTimeWindow#approximate(long, long, long)
    * @deprecated
    */
   @SuppressWarnings("javadoc")
public Object approximate(long queryTime, long startTime, long endTime) {
      return new Long(this.approximate(startTime, endTime));
   }

   /**
    * @param queryTime the query time.
    * @param startTime the start time.
    * @param endTime the end time.
    * @return the approximate query.
    * @throws InexactQueryException
    * @see org.rg.common.datatypes.ttw.XTimeWindow#approximate(long, long, long)
    * @deprecated
    */
   public Object query(long queryTime, long startTime, long endTime) throws InexactQueryException {
      return new Long(this.query(startTime, endTime));
   }

   /**
    * LAM-tlr this is also useless.
    * @see org.rg.common.datatypes.ttw.XTimeWindow#summary()
    * @deprecated
    */
   @SuppressWarnings("javadoc")
public long summary() {
      new Exception("Summary was called!").printStackTrace();
      return 0L;
   }

   /**
    * This method is deprecated in favor of the version that takes a long rather than an Object.
    * @param data the input data is a long.
    * @param timestamp the timestamp to update.
    * @deprecated
    */
   public void update(Object data, long timestamp) {
      this.update(((Long) data).longValue(), timestamp);
   }

   /**
    * If necessary, this method will update the tilted time window by summing frames earlier in history and shifting
    * frames that have accumulated to the next time boundary. This method is expected to be called in chronological
    * order, that is timestamps for the calls should always be greater than or equal to the timestamps of previous
    * calls.
    * @param data the count to add in.
    * @param timestamp the timestamp of the count.
    */
   public void update(long data, long timestamp) {

      // we need to roll up at least the last slot.
      if (timestamp >= last) {
         if (first == -1)
            first = timestamp;

         // if last is -1, this is our first time through, we need to initialize
         // the last slot time relative to the data and timestamp of this call.
         if (last == -1) {
            long f = millisPerSlot[0];
            last = timestamp + (f - (timestamp % f));
         } else {

            // Roll up slots until we have a current slot that includes
            // the provided timestamp.
            while (timestamp > last) {
               this.shift(0, 0);
               last += millisPerSlot[0];
            }
         }
      }
      counts[0] += data;
   }

   /**
    * Shift the frame up. If the roll-up accumulator is full, then roll-up the accumulator to the next frame. The rollup
    * method may also called shift, so consider this method recursive.
    * @param index the current index in the counts array.
    * @param frame the frame to roll up.
    */
   private void shift(int index, int frame) {

      // check that our window has rolled this far back in time.
      int nextRollupIndex = index + slotsPerFrame[frame] + 1;

      // if we are not at the end, and the slot we would roll up into the
      // accumulator is valid
      if (nextRollupIndex != counts.length && lastSlot >= (nextRollupIndex - 1)) {

         // We need to roll up into the next frames accumulator.
         counts[nextRollupIndex] += counts[nextRollupIndex - 1];
         bufferUpdates[frame]++;

         // Need to roll up the next buffer too.
         // If there is another frame, and it is ready to shift
         if (bufferUpdates[frame] == (millisPerSlot[frame + 1] / millisPerSlot[frame])) {
            shift(index + slotsPerFrame[frame] + 1, frame + 1);
            bufferUpdates[frame] = 0;
         }
      }

      // This is less efficient, but cleaner.
      System.arraycopy(counts, index, counts, index + 1, slotsPerFrame[frame]);
      counts[index] = 0;
      if (lastSlot < nextRollupIndex)
         lastSlot++;
   }

   /**
    * Returns a printable string nice and ready to display on the console, useful for debugging.
    * @return a printable string which describes this window
    */
   public String printable() {
      int len = millisPerSlot.length;
      StringBuffer sb = new StringBuffer();
      int where = 0;
      long ticker = last;

      // For each frame
      for (int i = 0; i < len; i++) {

         // First print the frequency, and the rollup accumulator
         long startRange = ticker;
         sb.append(millisPerSlot[i]);
         sb.append(" : ");
         sb.append(" (");
         sb.append(counts[where++]);
         sb.append('[');
         if (i == 0) {
            sb.append('?');
            ticker -= millisPerSlot[i];
         } else {
            sb.append(bufferUpdates[i - 1]);
            ticker -= millisPerSlot[i] * bufferUpdates[i - 1];
         }
         sb.append("]), ");
         int frameSize = slotsPerFrame[i];
         for (int j = 0; j < frameSize; j++, ticker -= millisPerSlot[i]) {
            if (j != 0)
               sb.append(", ");
            sb.append(counts[where++]);
         }
         sb.append(new Date(startRange) + " - " + new Date(ticker));
         sb.append('\n');
      }
      return sb.toString();
   }

   /**
    * print a representation of a query.
    * @param start the start time.
    * @param end the end time.
    * @param nttw2 the tilted time window.
    */
   static final void printQuery(long start, long end, TimeWindow nttw2) {
      long tmp = (long) nttw2.approximate(start, end);
      System.out.println(end + " - " + start + " : " + tmp);
   }

   /**
    * return the time of the first update received.
    * @return the time of the first update.
    * @see org.rg.common.datatypes.ttw.XTimeWindow#getFirstUpdateTime()
    */
   public long getFirstUpdateTime() {
      return first;
   }

   /** name for millis per slot. */
   static final String MILLISPERSLOT_NAME = "MILLIS";

   /** slots per slot. */
   static final String SLOTSPERFRAME_NAME = "SLOTSPERFRAME";

   /** all the counts. */
   static final String COUNTS_NAME = "COUNTS";

   /** last entry in the slot. */
   static final String LAST_NAME = "LAST";

   /** first entry in the slot. */
   static final String FIRST_NAME = "FIRST";

   /** buffer intermediate updates. */
   static final String BUFFERUPDATES_NAME = "BUFFERUPDATES";

   /** nthe last slot. */
   static final String LASTSLOT_NAME = "LASTSLOT";

   /**
    * @return representation as a 2d array of objects for tranport over SOAP layer.
    */
   public HashMap<String, Object> getPayload() {
      HashMap<String, Object>shank = new HashMap<String, Object>();
      shank.put(MILLISPERSLOT_NAME, this.millisPerSlot);
      shank.put(SLOTSPERFRAME_NAME, this.slotsPerFrame);
      shank.put(COUNTS_NAME, this.counts);
      shank.put(LAST_NAME,new Long(last));
      shank.put(FIRST_NAME,new Long(first));
      shank.put(BUFFERUPDATES_NAME, this.bufferUpdates);
      shank.put(LASTSLOT_NAME, new Integer(lastSlot));
      return shank;
   }

   /**
    * Set the instand from the given payload.
    * @param tmp the hashmap with the stuff in it.
    * @param pl the payload to config from.
    */
   public void setPayload(HashMap<String, Object> tmp) {
      this.millisPerSlot = (long[]) tmp.get(MILLISPERSLOT_NAME);
      this.slotsPerFrame = (int[]) tmp.get(SLOTSPERFRAME_NAME);
      this.counts = (long[]) tmp.get(COUNTS_NAME);
      this.last = ((Long)tmp.get(LAST_NAME)).longValue();
      this.first = ((Long)tmp.get(FIRST_NAME)).longValue();
      this.bufferUpdates = (int[]) tmp.get(BUFFERUPDATES_NAME);
      this.first = ((Integer)tmp.get(LASTSLOT_NAME)).intValue();
   }

}