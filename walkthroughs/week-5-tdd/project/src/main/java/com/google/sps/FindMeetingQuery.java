// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> collection = new ArrayList<TimeRange>();
    ArrayList<TimeRange> optionalCollection = new ArrayList<TimeRange>();
    TimeRange timeRange = TimeRange.fromStartDuration(0, 1440);
    collection.add(timeRange); //original timeRange
    if (request.getDuration() >= 1440) {
        collection.clear();
    }
    for (String attendee: request.getAttendees()) { //check every attendee in the request
        for (Event event: events) { //check every event
            if (event.getAttendees().contains(attendee)) { //check if the current attendee is in the event
                TimeRange eventTimeRange = event.getWhen();
                Iterator<TimeRange> iterator = collection.iterator();
                
                //Goes through and splices the day into sections where there are no time range overlaps
                for (int index = 0; index < collection.size(); index++) {
                    TimeRange currTimeRange = collection.get(index);
                    if (currTimeRange.overlaps(eventTimeRange)) {
                        int startDifference = Math.abs(eventTimeRange.start() - currTimeRange.start());
                        int endDifference = Math.abs(currTimeRange.end() - eventTimeRange.end());
                        int start = Math.min(currTimeRange.start(), eventTimeRange.start());
                        int end = Math.min(currTimeRange.end(), eventTimeRange.end());
                        collection.add(TimeRange.fromStartDuration(start, startDifference));
                        collection.add(TimeRange.fromStartDuration(end, endDifference));
                        collection.remove(currTimeRange);
                    }
                }

                //Gets rid of any possible events in the array or impossible time ranges
                for (int index = 0; index < collection.size(); index++) {
                    TimeRange currTimeRange = collection.get(index);
                    if (currTimeRange.overlaps(eventTimeRange)) {
                        collection.remove(currTimeRange);
                    }
                    if (currTimeRange.duration() < request.getDuration()) {
                        collection.remove(currTimeRange);
                    }
                }
            }
        }
    }

    //gets rid of additional impossible time ranges
    for (int  index = 0; index < collection.size(); index++) {
        TimeRange currTimeRange = collection.get(index);
        if (currTimeRange.duration() == 0) {
            collection.remove(currTimeRange);
        }
    }
    optionalCollection.addAll(collection);
    
    //for optional attendees
    for (String attendee: request.getOptionalAttendees()) {
        for (Event event: events) {
            if (event.getAttendees().contains(attendee)) {
                TimeRange eventTimeRange = event.getWhen();
                Iterator<TimeRange> iterator = collection.iterator();
                //checks if the optional attendees can make it
                if (request.getAttendees().size() != 0) {
                    for (int index = 0; index < collection.size(); index++) {
                        TimeRange currTimeRange = collection.get(index);
                        if (currTimeRange.overlaps(eventTimeRange)) {
                            optionalCollection.remove(currTimeRange);
                        }   
                    }
                }
                else {
                    //Goes through and splices the day into sections where there are no time range overlaps if there are only optional attendees
                    for (int index = 0; index < optionalCollection.size(); index++) {
                        TimeRange currTimeRange = optionalCollection.get(index);
                        if (currTimeRange.overlaps(eventTimeRange)) {
                            int startDifference = Math.abs(eventTimeRange.start() - currTimeRange.start());
                            int endDifference = Math.abs(currTimeRange.end() - eventTimeRange.end());
                            int start = Math.min(currTimeRange.start(), eventTimeRange.start());
                            int end = Math.min(currTimeRange.end(), eventTimeRange.end());
                            optionalCollection.add(TimeRange.fromStartDuration(start, startDifference));
                            optionalCollection.add(TimeRange.fromStartDuration(end, endDifference));
                            optionalCollection.remove(currTimeRange);
                        }   
                    }

                    //Gets rid of any possible events in the array or impossible time ranges
                    for (int index = 0; index < optionalCollection.size(); index++) {
                        TimeRange currTimeRange = optionalCollection.get(index);
                        if (currTimeRange.overlaps(eventTimeRange)) {
                            optionalCollection.remove(currTimeRange);
                        }
                        if (currTimeRange.duration() < request.getDuration()) {
                            optionalCollection.remove(currTimeRange);
                        }
                    }                    
                }           
            }
        }
    }

    //gets rid of additional impossible time ranges
    for (int  index = 0; index < optionalCollection.size(); index++) {
        TimeRange currTimeRange = optionalCollection.get(index);
        if (currTimeRange.duration() == 0) {
            optionalCollection.remove(currTimeRange);
        }
    }

    //if there are time ranges within the collection that optional attendees can attend, return the time ranges.
    if (optionalCollection.size() != 0) {
        collection = optionalCollection;
    }
    return collection;
  }
}
