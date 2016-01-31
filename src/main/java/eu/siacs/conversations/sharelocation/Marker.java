package eu.siacs.conversations.sharelocation;

import android.support.annotation.Nullable;

public class Marker {
    private final String title, description;
    private final double longitude, latitude;

    public static Builder builder() {
        return new Builder();
    }

    private Marker(String title, String description, double longitude, double latitude) {
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Nullable
    public String title() {
        return title;
    }

    @Nullable
    public String description() {
        return description;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public static class Builder {
        private String title, description;
        private double longitude, latitude;

        private Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Marker build() {
            return new Marker(title, description, longitude, latitude);
        }
    }
}
