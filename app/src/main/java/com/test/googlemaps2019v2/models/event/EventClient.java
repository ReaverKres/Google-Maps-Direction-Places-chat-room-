package com.test.googlemaps2019v2.models.event;

import android.app.Application;


public class EventClient extends Application {

        private Event event = null;

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

    }

