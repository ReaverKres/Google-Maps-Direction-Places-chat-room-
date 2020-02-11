package com.test.googlemaps2019v2;

import android.app.Application;
import com.test.googlemaps2019v2.models.Event;



    public class EventClient extends Application {

        private Event event = null;

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

    }

